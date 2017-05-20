package client.imap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.connetion.ConnectionSocket;

public class GetMailIMAP {
	Socket imapSocket = null;
	ConnectionSocket conn = null;
	ArrayList<String> listMessage,listMailBox;
	public void connect(String server, int port) throws Exception {
		imapSocket = new Socket(server, port);
		conn = new ConnectionSocket(imapSocket);
	}
	public boolean command(String user, String pass) {

		try {
			String response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("OK"))) {
				conn.closeConnection();
				return false;
			}
			
			conn.sendMsg("CAPABILITY");
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("OK"))) {
				conn.closeConnection();
				return false;
			}
			conn.sendMsg("LOGIN " +user +" "+pass);
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("OK"))) {
				conn.closeConnection();
				return false;
			}	
			listMailBox = (ArrayList<String>) conn.getObject();
			return true;
		}
			catch (Exception e) {
				return false;
			}
		
	}
	public boolean addMailbox(String nameMailbox) {
		try {
			conn.sendMsg("CREATE "+nameMailbox);//gửi đi như là tên hòm thư
			String response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("OK"))) {
				return false;
			}
			return true;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
	}
	public boolean delMailbox(String nameMailbox) {
		try {
			conn.sendMsg("DELETE "+nameMailbox);//gửi đi như là tên hòm thư
			String response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("OK"))) {
				return false;
			}
			return true;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
	}
	public boolean selectMailbox(String nameMailbox) {
		try {
			conn.sendMsg("SELECT "+nameMailbox);//gửi đi như là tên hòm thư
			String response = conn.receive();
			System.out.println(response);
			response = conn.receive();
			System.out.println(response);
			try {
				listMessage=(ArrayList<String>) conn.getObject();
				if(listMessage.size()==0) System.out.println("eeeeeeeeeeeeeeeeeee");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			conn.sendMsg("CHECK");
			response = conn.receive();
			System.out.println(response);
			//lệnh này k quan trọng trả về gì thì vẫn tiếp tục
			conn.sendMsg("CLOSE");//đóng lệnh select
			response = conn.receive();
			System.out.println(response);
			return true;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
	}
	public String getMessageContent(String nameMail) {
		try{
			conn.sendMsg("FETCH "+nameMail);
			String response = conn.receive();
			System.out.println(response);
			if (!response.trim().startsWith("OK")) {
				return "";
			}
			else {
				response = conn.receive();
				System.out.println(response);
				return response;
			}
		} catch (Exception e) {
			return "";
		}
	}
	public boolean closeConnect() {
	try{
		conn.sendMsg("LOGOUT ");
		String response = conn.receive();
		System.out.println(response);
		if (response.trim().startsWith("OK")) {
			System.out.println("DONE! close all connection");
			conn.closeConnection();
			return true;
		}
		return false;
	} catch (Exception e) {
		return false;
	}
}
	public ArrayList<String > getAllMail()
	{
		if(listMessage.size()!=0)
			System.out.println(listMessage.get(0));;
		return  listMessage;
	}
	public ArrayList<String > getAllMailBox()
	{
		return  listMailBox;
	}
}
