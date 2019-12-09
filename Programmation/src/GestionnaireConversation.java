package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import src.model.Message;

public class GestionnaireConversation extends Thread {
	private Socket sock;
	private Boolean run;
	private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

	public GestionnaireConversation(Socket sock) {
		this.sock = sock;
		try {
			this.out = new ObjectOutputStream(sock.getOutputStream());
			this.in = new ObjectInputStream(sock.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}

	public Socket getSock() {
		return sock;
	}

	public void run() {
		while(this.run) {
			try {
				if(in.available()>0) {
					Message m = (Message) in.readObject(); //NOUVEAU MESSAGE RECU
					Listener.getConversationByGestionnaire(this).nouveauMessage(m);
					//NOTIFY OBSERVER MAIN POUR UPDATE AFFICHAGE
				}			
			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		// receive doit pas etre bloquant pour donner la main a l'envoi regulierement
		// fonction pour envoyer un message

	}

	public void envoyerMessage(Message m) {
		
	}
	
	
	//arrete le thread proprement
	public void fin() {
		try {
			this.sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.sock = null;
		this.run = false;
	}
}
