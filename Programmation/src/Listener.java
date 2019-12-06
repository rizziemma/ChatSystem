package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Listener extends Thread {
	private int port = 12345;
	private ServerSocket server = null;
	private boolean isRunning = true;

	public Listener() {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (isRunning == true) {
			try {
				Socket sock = server.accept();
				traitement(sock);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			server.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			server=null;
		}
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void traitement(Socket sock) {
		Conversation.nouveauSYN(sock);
	}
	
	
	public void fin() {
		isRunning = false;
	}
}