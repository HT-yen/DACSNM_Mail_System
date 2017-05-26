package client.auth;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;

import client.connetion.ConnectionSocket;

public class CreateAuth {
	Socket authSocket = null;
	ConnectionSocket conn = null;

	public void connect(String server, int port) throws Exception {
		authSocket = new Socket(server, port);
		conn = new ConnectionSocket(authSocket);
	}

	public boolean command(String user, String pass) {
		if ((user.contains("/")) || (user.contains("-")) || (user.contains("+")) || (user.contains("*"))
				|| (user.contains("\\")) || (user.contains("|"))||(user.contains(","))||(user.contains(".")))
			return false;
		//name cannot have \,|,/,.,,,*,-,+,...
		if(user.split("@")[0].equals("")) return false;//cannot @abc
		if((user.split("@").length!=2) ||!(user.split("@")[1].trim().equals("abc")) )return false;
		//name must follow format: name@abc
		String response = "";
		try {
			conn.sendMsg("create");
			conn.sendMsg(user + " " + pass);
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("true"))) {
				conn.closeConnection();
				return false;
			}
			conn.closeConnection();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
