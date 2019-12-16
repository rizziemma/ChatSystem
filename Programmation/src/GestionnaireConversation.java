package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import src.model.Utilisateur;
import src.model.Datagram;
import src.model.Datatype;
import src.model.Historique;

public class GestionnaireConversation extends Thread {
	private Socket sock;
	private boolean isRunning = true;
	private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private Historique h;

	public GestionnaireConversation(Socket sock,Historique hist) {
		this.sock = sock;
		this.h = hist;
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
					Data.setStatus(Datagram.status_type.RECEIVED);
					if(Data.getType() == Datatype.MESSAGE) {
						traiterMessage(Data);
					}
					if(Data.getType() == Datatype.UTILISATEUR) {
						traiterUtilisateur((Utilisateur)Data.getData());
					}
					if(Data.getType()==Datatype.VU) {
						traiterVu();
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


	private void traiterMessage(Datagram data) {
		System.out.println("message reçu : " + (String)data.getData());
		//TODO envoyer le message aux classes qui en ont besoin
		h.addMessage(data);
		// notify observer
		//HistoriqueDAO DAO = new HistoriqueDAO();
		//DAO.nouveauDatagramme(h,data);
		//DAO.close();
	}


	public void envoyerMessage(String m) {
		Datagram data = new Datagram(Datatype.MESSAGE, (Object)m);
		try {
			out.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		data.setStatus(Datagram.status_type.SENT);
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
	
	private void traiterVu() {
		//TODO envoyer a observer +dao +local (ordre important)
	}
	
	
	public void envoyerVu() {
		Datagram data = new Datagram(Datatype.VU,null);
		try {
			out.writeObject(data);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//arrete le thread proprement
	public void fin() {
		this.isRunning = false;
	}
}
