package client.auth;

import java.net.Socket;

import client.connetion.ConnectionSocket;

public class CheckAuth {
	Socket authSocket = null;
	ConnectionSocket conn = null;

	public void connect(String server, int port) throws Exception {
		authSocket = new Socket(server, port);
		conn = new ConnectionSocket(authSocket);
	}
	public boolean command(String user, String pass) {
         String response="";
		try {
			conn.sendMsg("auth login");
			conn.sendMsg(user+" "+pass+"\n");
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