package src;

import java.io.IOException;
import java.net.Socket;

public class GestionnaireConversation extends Thread {
	private Socket sock;

	public GestionnaireConversation(Socket sock) {
		this.sock = sock;
	}

	public Socket getSock() {
		return sock;
	}

	public void run() {
		while() {
		
		}
		// check en continue si on recoit des messages, puis update bdd
		// receive doit pas etre bloquant pour donner la main a l'envoi regulierement
		// fonction pour envoyer un message

	}

	public void fin() {
		try {
			this.sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.sock = null;
		this.interrupt();
	}
}
