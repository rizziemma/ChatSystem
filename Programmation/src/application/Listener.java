package src.application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import src.resources.Property;

public class Listener extends Thread {
	private int port = Property.TCPServerSocketPort;
	private ServerSocket server = null;
	private boolean isRunning = true;

	public Listener() {
		try {
			server = new ServerSocket(port,100,ChatSystem.self.getAddrIP());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		
		while (isRunning == true) {
			try {
				Socket sock = null;
				sock = server.accept();
				System.out.println("Connection entrante");
				traitement(sock);
			} 
			catch(SocketException e) {
				 //do nothing normal use
				//server is closing
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void traitement(Socket sock) {
		Conversation conv = new Conversation(ChatSystem.getUserByIp(sock.getInetAddress()));
		ChatSystem.convs.add(conv);
		conv.nouveauSYN(sock);
	}
	
	
	public void fin() {
		isRunning = false;
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}