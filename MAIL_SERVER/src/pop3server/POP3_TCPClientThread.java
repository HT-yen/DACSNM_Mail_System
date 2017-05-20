package pop3server;

import java.io.ObjectInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import mainserver.Server_GUI;

public class POP3_TCPClientThread extends Thread {
	public static final int USER_STATE = 0;
	public static final int PASS_STATE = 1;
	public static final int TRAN_STATE = 2;
	public static final int END_STATE = 3;
	public String clientName;
	public Socket socket;
	ObjectOutputStream output;
	ObjectInputStream reader;
	private int state = 0;

	public POP3_TCPClientThread(Socket socket) {
		this.socket = socket;
		try {
			output = new ObjectOutputStream(this.socket.getOutputStream());
			reader = new ObjectInputStream(this.socket.getInputStream());
		} catch (Exception ex) {
			Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public void sendMessage(String message) {
		try {
			output.writeUTF(message);
			output.flush();
		} catch (IOException ex) {
			Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void run() {
		String response = "", user = "";
		try {
			String line_from_client = null;
			if (socket.isConnected()) {
				System.out.println("POP3 connected to " + socket.getInetAddress().getHostAddress());
				output.writeUTF("+OK POP3 server ready!");
				output.flush();
			}
			while (!socket.isClosed()) {
				line_from_client = reader.readUTF();
				if (line_from_client != null) {
					Server_GUI.addElementModel(line_from_client);
					line_from_client = line_from_client.toLowerCase().trim();
					/*
					 * if received quit command so close connection
					 */
					if (line_from_client.equals("quit")) {
						if (state == END_STATE) {
							System.out.println(line_from_client);
							if (InfoMessageOfUser.deleteAllEmail(user))
								sendMessage("+OK " + user);
							Server_GUI.addElementModel("_____________________________________________________");
							this.output.close();
							this.reader.close();
							this.socket.close();
							return;
						}

					}

					/*
					 * get request and response to client
					 */
					switch (state) {
					case USER_STATE:
						if (line_from_client.startsWith("user")) {
							System.out.println(line_from_client);
							user = line_from_client.substring(5, line_from_client.length()).trim();
							// take user from "USER user"
							response = "+OK User name accepted, password please";
							sendMessage(response);
							state++;
						} else {
							sendMessage("-ERR user error ");
							return;
						}
						break;
					case PASS_STATE:
						if (line_from_client.startsWith("pass")) {
							System.out.println(line_from_client);
							// neednt take pass because if can login so user
							// and pass is true
							response = "+OK Mailbox open, " + Integer.toString(InfoMessageOfUser.numberMailOfUser(user))
									+ " messages";
							sendMessage(response);
							state++;
						} else{
							sendMessage("-ERR password error ");
							return;
						}
						break;
					case TRAN_STATE:
						if (line_from_client.equals("stat")) {
							// response number of mail and sum size
							System.out.println(line_from_client);
							response = "+OK " + Integer.toString(InfoMessageOfUser.numberMailOfUser(user)) + " "
									+ Long.toString(InfoMessageOfUser.sumSizeMailOfUser(user));
							sendMessage(response);
							break;
						}
						if (line_from_client.equals("list")) {
							try {
								System.out.println(line_from_client);
								response = "+OK Mailbox scan listing follows.";
								for (int i = 0; i < InfoMessageOfUser.numberMailOfUser(user); i++) {
									response += Integer.toString(i + 1) + " "
											+ Long.toString(InfoMessageOfUser.getEmailSize(user, i)) + ".";
								}
								sendMessage(response);
								if (InfoMessageOfUser.numberMailOfUser(user) == 0) {
									response = "-EER No such message";
									sendMessage(response);
									return;
								}
								break;
							} catch (Exception e) {
								Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, e);
								return;
							}
						}
						if (line_from_client.startsWith("retr")) {
							System.out.println(line_from_client);
							try {
								int ID = Integer.parseInt(line_from_client.substring(5, line_from_client.length()));
								if (InfoMessageOfUser.getEmailString(user, ID - 1) == null) {
									response = "-EER No such message";
									sendMessage(response);
									return;
								} else {
									response = "+OK " + InfoMessageOfUser.getEmailString(user, ID - 1);
									sendMessage(response);
								}
								break;
							} catch (Exception e) {
								Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, e);
								return;
							}
						}

						if (line_from_client.startsWith("dele")) {
							System.out.println(line_from_client);
							try {
								int ID = Integer.parseInt(line_from_client.substring(5, line_from_client.length()));
								try {
									response = "+OK Message deleted";
									sendMessage(response);
									if (InfoMessageOfUser.numberMailOfUser(user) == ID)
										state++;
									// if client has taken all mail already,
									// state++ to END_STATE
								} catch (Exception e) {
									response = "-EER No such message";
									sendMessage(response);
									return;
								}
							} catch (Exception e) {
								Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, e);
								return;
							}
						}
						break;
					case END_STATE:
						return;
					}
				}
			}
		} catch (Exception e1) {
			Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, e1);
		}
	}

}

class InfoMessageOfUser {
	public static int numberMailOfUser(String user) {
		File file = new File("db/" + user.split("@")[0].trim()+"/inbox");
		file.mkdirs();
		if (file.listFiles() == null)
			return 0;
		return file.listFiles().length;
	}

	public static long getEmailSize(String user, int ID) throws Exception {
		File f;
		File file = new File("db/" + user.split("@")[0].trim()+"/inbox");
		file.mkdirs();
		// System.out.println(file.getName());
		if (file.listFiles() == null)
			return 0;
		else {
			f = file.listFiles()[ID];
			return f.length();
		}
	}

	public static String getEmailString(String user, int ID) throws Exception {
		File f;
		File file = new File("db/" + user.split("@")[0].trim()+"/inbox");
		file.mkdirs();
		// System.out.println(file.getName());
		String arrl = "";
		if (file.listFiles() == null)
			return null;
		else {
			f = file.listFiles()[ID];
			BufferedReader dis = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));

			// Đọc dữ liệu
			try {
				String line;
				while ((line = dis.readLine()) != null) {
					arrl += line + ".";
				}
				dis.close();
			} catch (Exception ex) {
				Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
				return null;
			}
		}
		return arrl;
	}

	public static long sumSizeMailOfUser(String user) {
		long sum = 0;
		File file = new File("db/" + user.split("@")[0].trim()+"/inbox");
		file.mkdirs();
		if (file.listFiles() == null)
			System.out.println("null");
		else {
			for (File f : file.listFiles())
				sum += f.length();
		}
		return sum;
	}

	public static boolean deleteAllEmail(String user) {
		File file = new File("db/" + user.split("@")[0].trim()+"/inbox");
		file.mkdirs();
		if (file.listFiles() == null)
			System.out.println("null");
		else {
			for (File f : file.listFiles())
				f.delete();
			return true;
		}
		return false;
	}

}