package mainserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import authserver.AUTH_TCPClientThread;
import pop3server.POP3_TCPClientThread;
import smtpserver.SMTP_TCPClientThread;

public class MainServer {

	public static void main(String[] args) {
		File dbFolder = new File("db");
		if (dbFolder.isFile()) {
			dbFolder.delete();
		}
		if (!dbFolder.exists()) {
			dbFolder.mkdir();
		}
		File accFolder = new File("account");
		if (accFolder.isFile()) {
			accFolder.delete();
		}
		if (!accFolder.exists()) {
			accFolder.mkdir();
		}
		if (args.length < 3) {
			System.out.println("You must provide TCP port for smtp, pop3 and authentication.");
			return;
		}
		MainServer m = new MainServer();
		try {
			m.start(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));

		} catch (Exception e) {
			System.out.println("TCP port must a number.");
		}
	}

	private List<Thread> smtpTcpClients = new ArrayList<>();
	private List<Thread> pop3TcpClients = new ArrayList<>();
	private List<Thread> authTcpClients = new ArrayList<>();

	public void start(int smtpTcpPort, int pop3TcpPort, int authTcpPort) {
		Thread smtpTcpMainThread = new Thread() {
			private ServerSocket server;

			@Override
			public void run() {
				try {
					server = new ServerSocket(smtpTcpPort);
					System.out.println("Listening on smtp TCP port " + smtpTcpPort);
					while (true) {
						Socket client = server.accept();
						SMTP_TCPClientThread th = new SMTP_TCPClientThread(client);
						smtpTcpClients.add(th);
						th.start();

					}
				} catch (IOException ex) {
					Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
				}

			}
		};
		smtpTcpMainThread.start();

		Thread pop3TcpMainThread = new Thread() {
			private ServerSocket server;
			@Override
			public void run() {
				try {
					server = new ServerSocket(pop3TcpPort);
					System.out.println("Listening on POP3 TCP port " + pop3TcpPort);
					while (true) {
						Socket client = server.accept();
						POP3_TCPClientThread th = new POP3_TCPClientThread(client);
						pop3TcpClients.add(th);
						th.start();

					}
				} catch (IOException ex) {
					Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
				}

			}
		};
		pop3TcpMainThread.start();

		Thread authTcpMainThread = new Thread() {
			private ServerSocket server;

			@Override
			public void run() {
				try {
					server = new ServerSocket(authTcpPort);
					System.out.println("Listening on authentication TCP port " + authTcpPort);
					while (true) {
						Socket client = server.accept();
						AUTH_TCPClientThread th = new AUTH_TCPClientThread(client);
						authTcpClients.add(th);
						th.start();
					}
				} catch (IOException ex) {
					Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
				}

			}

		};
		authTcpMainThread.start();
	}
}
