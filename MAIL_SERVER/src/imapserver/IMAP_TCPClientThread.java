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
		String response = "", user = "";
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
							if (Account_Server.Authentication(user_pass))
							{
								response = "OK - login completed, now in authenticated state";
							}
							else
								response = "NO - login failure: user name or password rejected ";
							sendMessage(response);
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

					if (line_from_client.startsWith("select")) {
						check = true;
						System.out.println(line_from_client);
						response = Integer.toString(InfoMessageOfUser.numberMailOfUser(user)) + " EXISTS";
						sendMessage(response);
						response = Integer.toString(InfoMessageOfUser.numberOfMessageRecentUser(user, new Date()))
								+ " RECENT";
						sendMessage(response);
						output.writeObject(InfoMessageOfUser.arrNameMessageSend);
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
							return;
						}
					}
					if (line_from_client.startsWith("fetch")) {
						System.out.println(line_from_client);
						try {
							int ID = Integer.parseInt(line_from_client.substring(6, line_from_client.length()));
							if (InfoMessageOfUser.getEmailString(user, ID - 1) == null) {
								response = "Fetch error: can't fetch that data";
								sendMessage(response);
							} else {
								response = "OK - fetch completed";
								sendMessage(response);
								response = InfoMessageOfUser.getEmailString(user, ID - 1);
								sendMessage(response);
							}
							continue;
						} catch (Exception e) {
							response = "BAD - command unknown or arguments invalid ";
							sendMessage(response);
							return;
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
	public static int numberMailOfUser(String user) {
		File file = new File("db/" + user.split("@")[0].trim());
		file.mkdir();
		if (file.listFiles() == null)
			return 0;
		return file.listFiles().length;
	}

	public static String getEmailString(String user, int ID) throws Exception {
		File f;
		File file = new File("db/" + user.split("@")[0].trim());
		file.mkdir();
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
				Logger.getLogger(IMAP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
				return null;
			}
		}
		return arrl;
	}

	public static Date gettimeConectUserBefore(String user) {
		File file = new File("timeConnectBefore/" + user.split("@")[0].trim()+"_time");
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
		file=new File("timeConnectBefore/" + user.split("@")[0].trim()+"_time");
		try {
			FileOutputStream output = new FileOutputStream(file);
			/* FileOutputStream output = new FileOutputStream(file,false) hoặc FileOutputStream output = new FileOutputStream(file)
			 *  ở phần append-> mỗi lần ghi là ghi đè */

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

	public static int numberOfMessageRecentUser(String user, Date datecompr) {
	while(arrNameMessageSend.size()!=0) arrNameMessageSend.remove(0);
	//remove tất cat phần tử của arrNameMessageSend để tránh add thêm nối vao danh sách trc
		File file = new File("db/" + user.split("@")[0].trim());
		file.mkdirs();
		try {
			if (file.listFiles() == null)
				return 0;
			else {
				Date datebefore = gettimeConectUserBefore(user);
				//ngay sau khi lấy ngày vào trc đó thì set lại ngày vào trước đó bằng thời gian hiện tại 
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
						System.out.println("NGAY VAO: "+datebefore);
						System.out.println("NGAY GUI MESSAGE: "+date);
						if (date.compareTo(datebefore) == 1) {
							count += 1;
							arrNameMessageSend.add(f.getName() + " NEW");
						} else
							arrNameMessageSend.add(f.getName());
						/*
						 * message dc gửi sau lần truy cập trước đó của tài
						 * khoản này bằng IMAP
						 */
					} catch (ParseException e) {
						e.printStackTrace();
					}
					dis.close();
				}
				return count;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return 0;

		}
	}

}
