package imapserver;

import java.io.ObjectInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import authserver.Account_Server;
import mainserver.Server_GUI;
import pop3server.POP3_TCPClientThread;
import smtpserver.SMTP_TCPClientThread;

public class IMAP_TCPClientThread extends Thread {
	public String clientName;
	public Socket socket;
	ObjectOutputStream output;
	ObjectInputStream reader;

	public IMAP_TCPClientThread(Socket socket) {
		this.socket = socket;
		try {
			output = new ObjectOutputStream(this.socket.getOutputStream());
			reader = new ObjectInputStream(this.socket.getInputStream());
		} catch (Exception ex) {
			Logger.getLogger(IMAP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public void sendMessage(String message) {
		try {
			output.writeUTF(message);
			output.flush();
		} catch (IOException ex) {
			Logger.getLogger(IMAP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void run() {
		String response = "", user = "", nameMailbox = "";
		boolean check = false;
		// ktra select hay chưa
		try {
			String line_from_client = null;
			if (socket.isConnected()) {
				System.out.println("IMAP connected to " + socket.getInetAddress().getHostAddress());
				output.writeUTF("OK IMAP server ready!");
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
					if (line_from_client.equals("logout")) {
						System.out.println(line_from_client);
						try {
							sendMessage("OK - logout completed");
							this.output.close();
							this.reader.close();
							this.socket.close();
							Server_GUI.addElementModel("_____________________________________________________");
							return;
						} catch (Exception e) {
							sendMessage("BAD - command unknown or arguments invalid");
							return;
						}
					}
					if (line_from_client.startsWith("login")) {
						try {
							System.out.println(line_from_client);
							user = line_from_client.substring(6, line_from_client.substring(6).indexOf(" ") + 6).trim();
							String user_pass = line_from_client.substring(6).trim();
							if (Account_Server.Authentication(user_pass)) {
								response = "OK - login completed, now in authenticated state";
							} else
								response = "NO - login failure: user name or password rejected ";
							sendMessage(response);
							output.writeObject(InfoMessageOfUser.getAllMailBox(user));
							// gửi danh sách cách message cho clien lựa chọn
							output.flush();
						} catch (Exception e) {
							sendMessage("BAD - command unknown or arguments invalid");
							return;
							// login k dc thì return luôn
						}
					}
					if (line_from_client.startsWith("capability")) {
						System.out.println(line_from_client);
						try {
							response = "OK - capability completed IMAP4rev1";
							sendMessage(response);
							continue;
						} catch (Exception e) {
							response = "BAD - command unknown or arguments invalid";
							sendMessage(response);
							return;
						}
					}
					if (line_from_client.startsWith("check")) {
						System.out.println(line_from_client);
						if (check) {
							response = "OK - check completed";
						} else
							response = "BAD - command unknown or arguments invalid";
						sendMessage(response);
						continue;
					}

					if (line_from_client.startsWith("create")) {
						try {
							System.out.println(line_from_client);
							String nameMailBox = line_from_client.substring(7);
							if (InfoMessageOfUser.createMailBox(user, nameMailBox)) {
								response = "OK - create completed";
							} else
								response = "NO - create failure: can't create mailbox with that name ";
							sendMessage(response);
							continue;
						} catch (Exception e) {
							response = "BAD - command unknown or arguments invalid ";
							sendMessage(response);
						}
					}
					if (line_from_client.startsWith("delete")) {
						try {
							System.out.println(line_from_client);
							String nameMailBox = line_from_client.substring(7);
							if (InfoMessageOfUser.delMailBox(user, nameMailBox)) {
								response = "OK - delete completed";
							} else
								response = "NO - delete failure: can't delete mailbox with that name ";
							sendMessage(response);
							continue;
						} catch (Exception e) {
							response = "BAD - command unknown or arguments invalid ";
							sendMessage(response);
						}
					}
					if (line_from_client.startsWith("copy")) {
						try {
							System.out.println(line_from_client);
							String nameMail = line_from_client.substring(5,
									line_from_client.substring(5).indexOf(" ") + 5);
							String nameMailBox = line_from_client
									.substring(line_from_client.substring(5).indexOf(" ") + 6);
							String content = reader.readUTF();
							if (InfoMessageOfUser.copyMail(user, nameMail, nameMailBox, content)) {
								response = "OK - copy completed";
							} else
								response = "NO - copy error: can't copy those messages or to that name";
							sendMessage(response);
							continue;
						} catch (Exception e) {
							response = "BAD - command unknown or arguments invalid ";
							sendMessage(response);
						}
					}

					if (line_from_client.startsWith("select")) {
						check = true;
						System.out.println(line_from_client);
						nameMailbox = line_from_client.substring(7).trim();
						response = Integer.toString(InfoMessageOfUser.numberMailOfUser(user, nameMailbox)) + " EXISTS";
						sendMessage(response);
						response = Integer.toString(
								InfoMessageOfUser.numberOfMessageRecentUser(user, nameMailbox, new Date())) + " RECENT";
						sendMessage(response);
						output.writeObject(InfoMessageOfUser.arrNameMessageSend);
						if (InfoMessageOfUser.arrNameMessageSend.size() == 0)
							// gửi danh sách cách message cho clien lựa chọn
							output.flush();
						continue;
					}
					if (line_from_client.equals("close")) {
						System.out.println(line_from_client);
						try {
							response = "OK - close completed, now in authenticated state";
							sendMessage(response);
							continue;
						} catch (Exception e) {
							response = "BAD - command unknown or arguments invalid";
							sendMessage(response);
						}
					}
					if (line_from_client.startsWith("fetch")) {
						System.out.println(line_from_client);
						try {
							String nameMail = line_from_client.substring(6, line_from_client.length());
							if (InfoMessageOfUser.getEmailString(user, nameMailbox, nameMail) == null) {
								response = "Fetch error: can't fetch that data";
								sendMessage(response);
							} else {
								response = "OK - fetch completed";
								sendMessage(response);
								response = InfoMessageOfUser.getEmailString(user, nameMailbox, nameMail);
								System.out.println("contenttttt mail" + response + " \n \n");
								sendMessage(response);
							}
							continue;
						} catch (Exception e) {
							response = "BAD - command unknown or arguments invalid ";
							sendMessage(response);
						}
					}
				}
			}

		} catch (Exception e1) {
			Logger.getLogger(IMAP_TCPClientThread.class.getName()).log(Level.SEVERE, null, e1);
		}
	}

}

class InfoMessageOfUser {
	public static ArrayList<String> arrNameMessageSend = new ArrayList<>();

	// danh sách tên các message gủi cho client trong đó message nào recent thì
	// thêm từ new vào sau tên
	public static int numberMailOfUser(String user, String nameMailbox) {
		File file = new File("db/" + user.split("@")[0].trim() + "/" + nameMailbox);
		file.mkdirs();
		return file.listFiles().length;
	}

	public static String getEmailString(String user, String nameMailbox, String nameMail) throws Exception {
		try {
			if (nameMail.contains("NEW"))
				nameMail = nameMail.substring(0, nameMail.length() - 3);
			File file = new File("db/" + user.split("@")[0].trim() + "/" + nameMailbox + "/" + nameMail);
			String arrl = "";
			BufferedReader dis = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			// Đọc dữ liệu
			String line;
			while ((line = dis.readLine()) != null) {
				arrl += line + ".";
			}
			dis.close();
			return arrl;
		} catch (Exception ex) {
			Logger.getLogger(IMAP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public static Date gettimeConectUserBefore(String user) {
		File file = new File("timeConnectBefore/" + user.split("@")[0].trim() + "_time");
		// System.out.println(file.getName());
		if (file.length() == 0)
			return new Date(110, 1, 2);
		// lấy ngày mặc định cho là Tue Feb 02 00:00:00 ICT 2010// tự cho miễn
		// sao nhỏ hơn hiện tại
		else {
			// Đọc dữ liệu
			try {
				BufferedReader dis = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				String line = dis.readLine();
				dis.close();
				String pattern = "yyyy-MM-dd hh:mm:ss";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
				Date date = simpleDateFormat.parse(line);
				System.out.println(date);
				return date;
			} catch (Exception ex) {
				Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
				return new Date(110, 1, 2);
			}
		}
	}

	public static boolean settimeConectUser(String user) {
		File file = new File("timeConnectBefore");
		file.mkdirs();
		System.out.println(user);
		file = new File("timeConnectBefore/" + user.split("@")[0].trim() + "_time");
		try {
			FileOutputStream output = new FileOutputStream(file);
			/*
			 * FileOutputStream output = new FileOutputStream(file,false) hoặc
			 * FileOutputStream output = new FileOutputStream(file) ở phần
			 * append-> mỗi lần ghi là ghi đè
			 */

			String pattern = "yyyy-MM-dd hh:mm:ss";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String date = simpleDateFormat.format(new Date());
			output.write(date.getBytes());
			output.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;

		}
	}

	public static ArrayList<String> getAllMailBox(String user) {
		File file = new File("db/" + user.split("@")[0].trim());
		file.mkdirs();
		ArrayList<String> arr = new ArrayList<>();
		if (file.listFiles() != null) {
			for (File f : file.listFiles())
				if (f.isDirectory())
					arr.add(f.getName());
		}
		return arr;
	}

	public static int numberOfMessageRecentUser(String user, String nameMailbox, Date datecompr) {
		arrNameMessageSend = new ArrayList<>();
		File file = new File("db/" + user.split("@")[0].trim() + "/" + nameMailbox);
		file.mkdirs();
		try {
			Date datebefore = gettimeConectUserBefore(user);
			// ngay sau khi lấy ngày vào trc đó thì set lại ngày vào trước
			// đó bằng thời gian hiện tại
			InfoMessageOfUser.settimeConectUser(user);
			int count = 0;
			File f;
			for (int i = 0; i < file.listFiles().length; i++) {
				f = file.listFiles()[i];
				BufferedReader dis = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
				// Đọc time gửi của mỗi message(dòng đầu tiên)
				String line = dis.readLine();
				String pattern = "yyyy-MM-dd hh:mm:ss";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
				Date date;
				try {
					date = simpleDateFormat.parse(line);
					System.out.println("NGAY VAO: " + datebefore);
					System.out.println("NGAY GUI MESSAGE: " + date);
					if (date.compareTo(datebefore) == 1) {
						count += 1;
						arrNameMessageSend.add(f.getName() + " NEW");
					} else
						arrNameMessageSend.add(f.getName());
					/*
					 * message dc gửi sau lần truy cập trước đó của tài khoản
					 * này bằng IMAP
					 */
				} catch (ParseException e) {
					e.printStackTrace();
				}
				dis.close();
			}
			return count;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;

		}
	}

	public static boolean createMailBox(String user, String nameMailBox) {
		File file = new File("db/" + user.split("@")[0].trim() + "/" + nameMailBox);
		return file.mkdirs();
	}

	////////
	public static boolean copyMail(String user, String nameMail, String nameMailBox, String content) {
		try {
			File file = new File("db/" + user.split("@")[0].trim() + "/" + nameMailBox + "/" + nameMail);
			FileOutputStream output;
			content = content.replaceAll("\\.", "\n");
			try {
				output = new FileOutputStream(file);
				output.write(content.getBytes("UTF-8"));
				output.flush();
				output.close();
				return true;
			} catch (Exception ex) {
				Logger.getLogger(IMAP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean delMailBox(String user, String nameMailBox) {
		File file = new File("db/" + user.split("@")[0].trim() + "/" + nameMailBox);
		file.mkdirs();
		if (file.listFiles() == null)
			return file.delete();
		else {
			for (File f : file.listFiles())
				f.delete();
			return file.delete();
		}
	}
}
