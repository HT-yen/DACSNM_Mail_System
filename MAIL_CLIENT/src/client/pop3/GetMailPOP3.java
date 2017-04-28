package client.pop3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.connetion.ConnectionSocket;

public class GetMailPOP3 {
	Socket pop3Socket = null;
	ConnectionSocket conn = null;

	public void connect(String server, int port) throws Exception {
		pop3Socket = new Socket(server, port);
		conn = new ConnectionSocket(pop3Socket);
	}
	public boolean command(String user, String pass) {

		try {
			int numberOfMail=0;
			//so mail cua user tren server
			String response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("+OK"))) {
				conn.closeConnection();
				return false;
			}
			/*
			 * send command
			 */
			conn.sendMsg("USER "+user);
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("+OK"))) {
				conn.closeConnection();
				return false;
			}
			conn.sendMsg("PASS " +pass);
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("+OK"))) {
				conn.closeConnection();
				return false;
			}

			conn.sendMsg("STAT");
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("+OK"))) {
				conn.closeConnection();
				return false;
			}
			else{
				try {
					numberOfMail=Integer.parseInt(response.substring(4, response.substring(4,response.length()).indexOf(" ")+4));
				   System.out.println("numberOfMail "+numberOfMail);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}

			conn.sendMsg("LIST");
			response = conn.receive();
			for(int i=0;i<response.length();i++){
				if(response.charAt(i)!='.')
					System.out.print(response.charAt(i));
				else {
					System.out.println();
				}
			}
			if (!(response.trim().startsWith("+OK"))) {
				conn.closeConnection();
				return false;
			}
			/*
			 * request list of mail and information of them
			 */
			for(int i=1;i<=numberOfMail;i++) {
				conn.sendMsg("RETR "+i);
				response = conn.receive();
				System.out.println(response);
				if (!(response.trim().startsWith("+OK"))) {
					conn.closeConnection();
					return false;
				}
				else {
					saveIntoMailBox(user,response.substring(4,response.length()));
					//save content mail into mailbox
					conn.sendMsg("DELE "+i);
					//delete mail in db server if take mail successly 
					response = conn.receive();
					System.out.println(response);
					if (!(response.trim().startsWith("+OK"))) {
						conn.closeConnection();
						return false;
					}
				}
			}
			
			conn.sendMsg("QUIT");
			response = conn.receive();
			System.out.println(response);
			if (response.trim().startsWith("+OK")) {
				System.out.println("DONE! close all connection");
				conn.closeConnection();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			conn.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	public boolean saveIntoMailBox(String user, String response) {
		
		String folderName = user.split("@")[0].trim();
		File receiverFolder = new File("Mailbox/" + folderName);
		receiverFolder.mkdir();
		//tao folder

		System.out.println(folderName);
		String[] contentmail=response.split("\\.");
		String nameMail=contentmail[1].substring(7)+"-"+contentmail[3].substring(9);
		//name mail in client is sender+subject

		int count = 0;
		for (File file : receiverFolder.listFiles()) {
			if (file.getName().equals(nameMail)) {
				count++;
			}
		}
		File emailFile = new File("Mailbox/" + folderName + "/" +nameMail+ (count == 0 ? "" : ("_" + count)));
		//tao file
		String writeToFile = contentmail[0]  + "\n" + contentmail[1] + "\n" + contentmail[2] + "\n" + contentmail[3]+ "\n" + contentmail[4];
				FileOutputStream output;
		try {
			output = new FileOutputStream(emailFile);
			output.write(writeToFile.getBytes("UTF-8"));
			output.flush();
			output.close();
			return true;
		} catch (Exception ex) {
			Logger.getLogger(GetMailPOP3.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
	}
	public ArrayList<String > getAllMail(String user)
	{
		ArrayList<String > allmail=new ArrayList<>();
		File file = new File("Mailbox/" + user.split("@")[0].trim());
		file.mkdir();
		if (file.listFiles() == null)
			System.out.println("null");
		else {
			for (File f : file.listFiles())
				allmail.add(f.getName());
		}
		return  allmail;
	}
}
