package client.smtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import client.connetion.ConnectionSocket;

public class SendMailSMTP {

	Socket smtpSocket = null;
	OutputStream sockOut = null;
	InputStream sockIn = null;
	ConnectionSocket conn = null;
	boolean sendalready = false;

	public void connect(String server, int port) throws Exception {
		smtpSocket = new Socket(server, port);
		conn = new ConnectionSocket(smtpSocket);
	}

	// Sending e-mail
	public boolean command(String mailfrom, String mailto, String subject, String data) {

		try {
			String response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("220"))) {
				conn.closeConnection();
				return false;
			}
			/*
			 * send command
			 */
			conn.sendMsg("HELO");
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("250"))) {
				conn.closeConnection();
				return false;
			}

			conn.sendMsg("MAIL FROM: <" + mailfrom + ">");
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("250"))) {
				conn.closeConnection();
				return false;
			}

			conn.sendMsg("RCPT TO: <" + mailto + ">");
			response = conn.receive();
			System.out.println(response);
			if ((response.trim().startsWith("250"))) {
				sendalready = true;
			} else {
				conn.closeConnection();
				return true;
			}

			conn.sendMsg("DATA");
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("354"))) {
				conn.closeConnection();
				return false;
			}
			/*
			 * send data mail
			 */
			conn.sendMsg("Subject: " + subject);
			conn.sendMsg(data);
			conn.sendMsg(".");
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("250"))) {
				conn.closeConnection();
				return false;
			}
			conn.sendMsg("QUIT");
			response = conn.receive();
			if (response.trim().startsWith("251")) {
				System.out.println("BYE");
				conn.closeConnection();
				return true;
			}
		} catch (

		Exception e)

		{
			e.printStackTrace();
		}

		try

		{
			conn.closeConnection();
		} catch (

		IOException e)

		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	public boolean getSendAlready() {
		return sendalready;
	}

}
