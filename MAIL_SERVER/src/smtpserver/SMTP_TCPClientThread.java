package smtpserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import authserver.Account_Server;
import mainserver.Server_GUI;
import pop3server.POP3_TCPClientThread;

public class SMTP_TCPClientThread extends Thread {

	public static final int EHLO_STATE = 0;
	public static final int MAIL_FROM_STATE = 1;
	public static final int RCPT_TO_STATE = 2;
	public static final int DATA_STATE = 3;
	public static final int END_STATE = 4;

	public String clientName;
	public Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream reader;
	private int state = 0;

	public SMTP_TCPClientThread(Socket socket) {
		this.socket = socket;
		try {
			output = new ObjectOutputStream(this.socket.getOutputStream());
			reader = new ObjectInputStream(this.socket.getInputStream());
		} catch (Exception ex) {
			Logger.getLogger(SMTP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void sendMessage(String message) {
		try {
			output.writeUTF(message);
			output.flush();
		} catch (IOException ex) {
			Logger.getLogger(SMTP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void run() {
		String response = "", data = "", senderName = "", receiverName = "";
		try {
			String line_from_client = null;
			if (socket.isConnected()) {
				System.out.println("connected to " + socket.getInetAddress().getHostAddress());
				output.writeUTF("220 Server access OK");
				output.flush();
			}
			while (!socket.isClosed()) {
				line_from_client = reader.readUTF();
				if (line_from_client != null) {
					line_from_client = line_from_client.toLowerCase().trim();
					Server_GUI.addElementModel(line_from_client);
					/*
					 * if received quit command so close connection
					 */
					if (line_from_client.equals("quit")) {
						if (state == END_STATE) {
							if (saveEmail(receiverName, senderName, data))
								System.out.println(line_from_client);
						}
						sendMessage("251, Bye");
						Server_GUI.addElementModel("_____________________________________________________");
						this.output.close();
						this.reader.close();
						this.socket.close();
						return;
					}

					/*
					 * get request and response to client
					 */
					switch (state) {
					case EHLO_STATE:
						if (line_from_client.equals("helo") || line_from_client.startsWith("ehlo ")) {
							System.out.println(line_from_client);
							response = "250 hello " + InetAddress.getLocalHost().getHostName() + " ,OK";
							sendMessage(response);
							state++;
						} else {
							response = "ERROR HELO/HELO mail.example.com ";
							sendMessage(response);
							return;
						}
						break;
					case MAIL_FROM_STATE:
						if (line_from_client.startsWith("mail from: <") && line_from_client.endsWith(">")
								&& !line_from_client.split("<")[1].equals(">")) {
							/*
							 * check sender name is null?
							 */
							System.out.println(line_from_client);
							senderName = line_from_client.split("<")[1].split(">")[0];
							response = "250 sender <" + senderName + "> ,OK";
							sendMessage(response);
							/*
							 * insert code to check validate sender name here
							 */
							state++;
						} else {
							response = "ERROR need command : MAIL FROM: <example@example.com>";
							sendMessage(response);
							return;
						}
						break;
					case RCPT_TO_STATE:
						if (line_from_client.startsWith("rcpt to: <") && line_from_client.trim().endsWith(">")
								&& !line_from_client.split("<")[1].equals(">")) {
							/*
							 * check receiver name is null?
							 */
							System.out.println(line_from_client);
							receiverName = line_from_client.split("<")[1].split(">")[0];
							if(!Account_Server.userIsExist(receiverName)) response="user not exist in server mail";
							else response = "250 receiver <" + receiverName + "> ,OK";
							sendMessage(response);
							/*
							 * insert code check validate receiver name here
							 */
							state++;
						} else {
							response = "ERROR need command : RCPT TO: <example@example.com>";
							sendMessage(response);
							return;
						}
						break;
					case DATA_STATE:
						if (line_from_client.equals("data")) {
							System.out.println(line_from_client);
							response = "354 Send message, end with a \".\" on a line_from_client by itself";
							sendMessage(response);
							/*
							 * start to get DATA here
							 */
							data = "";
							line_from_client = reader.readUTF();
							while (!line_from_client.equals(".")) {
								System.out.println(line_from_client);
								data += line_from_client + "\n";
								line_from_client = reader.readUTF();
							}
							/*
							 * got DATA
							 */
							response = "250 DATA OK";
							sendMessage(response);
							state++;
						} else {
							data = "ERROR need command DATA";
							sendMessage(data);
							return;
						}
						break;
					case END_STATE:
						sendMessage("ERROR send QUIT to disconnect this communication");
						return;
					}
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(SMTP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private boolean saveEmail(String receiverName, String senderName, String data) {

		String folderName = receiverName.split("@")[0].trim();
		File receiverFolder = new File("db/" + folderName);
		receiverFolder.mkdir();
		// tao folder

		String subject = "no subject";
		int indexSubject_start;
		if ((indexSubject_start = data.toLowerCase().lastIndexOf("subject: ")) >= 0) {
			int indexSubject_end;
			indexSubject_end = data.substring(indexSubject_start, data.length() - 1).indexOf("\n");
			subject = data.substring(indexSubject_start, indexSubject_end).toLowerCase().replaceFirst("subject: ", "")
					.replace(':', '_').replace('\\', '_').replace('/', '_').replace('*', '_').replace('|', '_')
					.replace('>', '_').replace('<', '_').replace('?', '_');
			// file name can't contain \/:*?<>|
		}

		int count = 0;
		for (File file : receiverFolder.listFiles()) {
			if (file.getName().equals(subject + "-" + senderName + ".email")) {
				count++;
			}
		}

		File emailFile = new File("db/" + folderName + "/" + subject + "-" + senderName + ""
				+ (count == 0 ? "" : ("_" + count)) + ".email");
		// tao file

		Date current = new Date();
		String pattern = "yyyy-MM-dd hh:mm:ss";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		String dateStr = format.format(current);

		String writeToFile = dateStr + "\nfrom : " + senderName + "\nto : " + receiverName + "\n" + data;
		FileOutputStream output;
		try {
			output = new FileOutputStream(emailFile);
			output.write(writeToFile.getBytes("UTF-8"));
			output.flush();
			output.close();
			return true;
		} catch (Exception ex) {
			Logger.getLogger(SMTP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
	}
}
