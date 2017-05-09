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
	ArrayList<String> listMessage;
	public void connect(String server, int port) throws Exception {
		imapSocket = new Socket(server, port);
		conn = new ConnectionSocket(imapSocket);
	}
	public boolean command(String user, String pass) {

		try {
			//so mail cua user tren server
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

			conn.sendMsg("SELECT "+user);//với user gửi đi như là tên hòm thư
			response = conn.receive();
			System.out.println(response);
			response = conn.receive();
			System.out.println(response);
			try{
				listMessage=(ArrayList<String>) conn.getObject();	
			}catch(Exception e)
			{
				return false;
			}
			
			conn.sendMsg("CHECK");
			response = conn.receive();
			System.out.println(response);
			//lệnh này k quan trọng trả về gì thì vẫn tiếp tục
			 
			conn.sendMsg("CLOSE");//đóng lệnh select
			response = conn.receive();
			System.out.println(response);
			return true;
		}
			catch (Exception e) {
				return false;
			}
		
	}
	public String getMessageContent(int id) {
		try{
			conn.sendMsg("FETCH "+Integer.toString(id));
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
	public ArrayList<String > getAllMail(String user)
	{
		return  listMessage;
	}
}
