package client.connetion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionSocket {

	private ObjectOutputStream output = null;
	private ObjectInputStream reader = null;

	private Socket socket;

	public ConnectionSocket(Socket socket) {
		this.socket = socket;
		try {
			output = new ObjectOutputStream(this.socket.getOutputStream());
			reader = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendMsg(String msg) throws IOException {

		output.writeUTF(msg);
		output.flush();
	}

	public String receive() throws IOException {
		String receive = "";
		if (!this.socket.isClosed()) {
			String line = reader.readUTF();
			receive += line;
		}
		return receive;
	}

	public Object getObject() throws ClassNotFoundException, IOException {
		Object obj = reader.readObject();
		return obj;
	}

	public boolean closeConnection() throws IOException {
		if (reader != null)
			reader.close();
		if (output != null)
			output.close();
		if (reader != null)
			reader.close();
		this.socket.close();
		return this.socket.isClosed();
	}
}
