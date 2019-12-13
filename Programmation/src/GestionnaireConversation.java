package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import src.model.Utilisateur;
import src.model.Datagram;
import src.model.Datatype;
import src.model.Message;

public class GestionnaireConversation extends Thread {
	private Socket sock;
	private boolean isRunning = true;
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


	public void run() {
		while(this.isRunning) {
			try {
				if(in.available()>0) {
					Datagram Data = (Datagram) in.readObject(); //NOUVEAU MESSAGE RECU
					if(Data.getType() == Datatype.MESSAGE) {
						traiterMessage((Message)Data.getData());
					}
					if(Data.getType() == Datatype.UTILISATEUR) {
						traiterUtilisateur((Utilisateur)Data.getData());
					}
				}			
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// receive doit pas etre bloquant pour donner la main a l'envoi regulierement
		// fonction pour envoyer un message
		try {
			this.sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.sock = null;

	}

	private void traiterUtilisateur(Utilisateur u) {
		ChatSystem.addUtilisateur(u);
		
	}


	private void traiterMessage(Message m) {
		System.out.println("message reçu : " + m.toString());
		//TODO envoyer le message aux classes qui en ont besoin
		// vers DAO
		// vers stockage local
		// notify observer
	}


	public void envoyerMessage(Message m) {
		Datagram data = new Datagram(Datatype.MESSAGE, (Object)m);
		try {
			out.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO envoyer le message aux classes qui en ont besoin
	}
	
	public void envoyerUtilisateur(Utilisateur u) {
		Datagram data = new Datagram(Datatype.UTILISATEUR, (Object)u);
		try {
			out.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//arrete le thread proprement
	public void fin() {
		this.isRunning = false;
	}
}
