package src.application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import src.resources.Properties;

public class Listener extends Thread {
	private int port = Properties.TCPServerSocketPort;
	private ServerSocket server = null;
	private boolean isRunning = true;

	public Listener() {
		try {
			server = new ServerSocket(port,100,ChatSystem.self.getAddrIP());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		
		while (isRunning == true) {
			try {
				Socket sock = null;
				sock = server.accept();
				System.out.println("Connection entrante");
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
		Conversation conv = new Conversation(ChatSystem.getUserByIp(sock.getInetAddress()));
		ChatSystem.convs.add(conv);
		conv.nouveauSYN(sock);
	}
	
	
	public void fin() {
		isRunning = false;
	}
}