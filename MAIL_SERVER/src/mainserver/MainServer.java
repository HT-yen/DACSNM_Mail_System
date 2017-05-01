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
import imapserver.IMAP_TCPClientThread;
import pop3server.POP3_TCPClientThread;
import smtpserver.SMTP_TCPClientThread;

public class MainServer {	
	private ServerSocket serverSMTP,serverPOP3,serverIMAP,serverAUTH;
	private List<SMTP_TCPClientThread> smtpTcpClients = new ArrayList<>();
	private List<POP3_TCPClientThread> pop3TcpClients = new ArrayList<>();
	private List<AUTH_TCPClientThread> authTcpClients = new ArrayList<>();
	private List<IMAP_TCPClientThread> imapTcpClients = new ArrayList<>();
	Thread imapTcpMainThread,pop3TcpMainThread,smtpTcpMainThread,authTcpMainThread;

	public void start(int smtpTcpPort, int pop3TcpPort,int imapTcpPort, int authTcpPort) {
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
		try {
			serverSMTP = new ServerSocket(smtpTcpPort);
			serverPOP3 = new ServerSocket(pop3TcpPort);
			serverAUTH = new ServerSocket(authTcpPort);
			serverIMAP = new ServerSocket(imapTcpPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		smtpTcpMainThread = new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("Listening on smtp TCP port " + smtpTcpPort);
					while (true) {
						Socket client = serverSMTP.accept();
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

		pop3TcpMainThread = new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("Listening on POP3 TCP port " + pop3TcpPort);
					while (true) {
						Socket client = serverPOP3.accept();
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
		
		imapTcpMainThread = new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("Listening on imap TCP port " + imapTcpPort);
					while (true) {
						Socket client = serverIMAP.accept();
						IMAP_TCPClientThread th = new IMAP_TCPClientThread(client);
						imapTcpClients.add(th);
						th.start();

					}
				} catch (IOException ex) {
					Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
				}

			}
		};
		imapTcpMainThread.start();

		authTcpMainThread = new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("Listening on authentication TCP port " + authTcpPort);
					while (true) {
						Socket client = serverAUTH.accept();
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
	public boolean offServer() {
		try{
			smtpTcpMainThread.stop();
			pop3TcpMainThread.stop();
			imapTcpMainThread.stop();
			authTcpMainThread.stop();
			for(SMTP_TCPClientThread smtp:smtpTcpClients)
				try {
					smtp.socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			for(POP3_TCPClientThread pop3:pop3TcpClients)
				try {
					pop3.socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			for(IMAP_TCPClientThread imap:imapTcpClients)
				try {
					imap.socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			for(AUTH_TCPClientThread auth:authTcpClients)
				try {
					auth.socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			serverAUTH.close();
			serverIMAP.close();
			serverPOP3.close();
			serverSMTP.close();
			return true;	
		}catch(Exception e1){
			e1.printStackTrace();
			return false;
		}	
	}
}
