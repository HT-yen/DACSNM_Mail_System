package authserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import mainserver.Server_GUI;
import smtpserver.SMTP_TCPClientThread;

public class AUTH_TCPClientThread extends Thread {

	public String clientName;
	public Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream reader;

	public AUTH_TCPClientThread(Socket socket) {
		this.socket = socket;
		try {
			output = new ObjectOutputStream(this.socket.getOutputStream());
			reader = new ObjectInputStream(socket.getInputStream());
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
		System.out.println("connected to " + socket.getInetAddress().getHostAddress());
		String line = "";
		while (!socket.isClosed()) {
			try {
				line = reader.readUTF().trim();	
				Server_GUI.addElementModel(line);
				/*
				 * if line.equals("auth login") do check authentication login
				 */
				if (line.equals("auth login")) {

					line = reader.readUTF();
					Server_GUI.addElementModel(line);
					Server_GUI.addElementModel("_____________________________________________________");
					/*
					 * send true to client if usermane and password match with
					 * db
					 */
					if (Account_Server.Authentication(line)) {
						sendMessage("true");
						
						System.out.println("auth login - accept");
						
						reader.close();
						output.close();
						socket.close();
					} else {
						/*
						 * send false to client if username and password match
						 * with db
						 */
						sendMessage("false");

						System.out.println("auth login - not accept");
						
						reader.close();
						output.close();
						socket.close();
					}
				} else if (line.equals("create")) {
					/*
					 * if line.equals("create") do create acount
					 */
					line = reader.readUTF().trim();
					Server_GUI.addElementModel(line);
					Server_GUI.addElementModel("_____________________________________________________");
					if (Account_Server.userIsExist(line.split(" ")[0])) {
						/*
						 * send exist to cllient if username is exist in db
						 */
						sendMessage("exist");

						System.out.println("create - account existed");
						
						reader.close();
						output.close();
						socket.close();
					} else if (Account_Server.CreateAccount(line)) {
						/*
						 * send true to client if username dose not exist in db
						 * and create new email successfully
						 */
						sendMessage("true");

						System.out.println("create - successfully");
						
						reader.close();
						output.close();
						socket.close();
					} else {
						/*
						 * send false to client if can not create email
						 */
						sendMessage("false");

						System.out.println("create - not success");
						
						reader.close();
						output.close();
						socket.close();
					}
				} else {
					reader.close();
					output.close();
					socket.close();
					return;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
