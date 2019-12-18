package src;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import src.model.Datagram;
import src.model.Datatype;
import src.model.Historique;
import src.model.Utilisateur;

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
			InputStream IS = sock.getInputStream();
			BufferedInputStream BIS = new BufferedInputStream(IS);
			this.in = new ObjectInputStream(BIS);
			
			
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
					switch(Data.getType()) {
					case MESSAGE:
						traiterMessage(Data);
						break;
					case UTILISATEUR:
						traiterUtilisateur(Data);
						break;
					case VU:
						traiterVu();
						break;
					case IMAGE:
						traiterImage(Data);
						break;
					case FICHIER:
						traiterFichier(Data);
						break;
					default:
						System.out.println("Datagram not recognised");
						break;
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

	private void traiterFichier(Datagram data) {
		// TODO Auto-generated method stub
		
	}


	private void traiterImage(Datagram data) {
		// TODO Auto-generated method stub
		
	}


	private void traiterUtilisateur(Datagram data) {
		Utilisateur u = (Utilisateur)data.getData();
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
		try {
			out.flush();
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
		//TODO notifier la BDD locale du VU envoyé
	}
	
	public void fin() { // arrete le thread proprement (fin du thread dans le Run())
		this.isRunning = false;
	}
}
