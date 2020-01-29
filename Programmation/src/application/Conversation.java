package src.application;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import src.resources.Property;
import src.model.Utilisateur;

public class Conversation {
	private Utilisateur user;
	private GestionnaireConversation gestionnaire;
	
	public Conversation(Utilisateur u) {
		this.user = u;
	}
	
	
	public void nouveauSYN(Socket sock) { //instentie le gestionnaire conversation du SYN reçus pour commencer la conversation avec la session distante ou resyncronyse si la connexion a été perdu
		Boolean done =false ;
		for (Conversation conv : ChatSystem.convs) {
			if (conv.getUser().getAddrIP().equals(sock.getInetAddress())) {
				if(conv.getGestionnaire() == null) { 
					conv.setGestionnaire(new GestionnaireConversation(sock,user));	
				
				}
				done = true;
				break;
			}
			
		}
		if (!done ) {
			throw (new NoSuchFieldError("Erreur dans la reception du Syn sur le socket:" + sock.toString()+ "\n" + "la conversassion associ�e n'a pas �t� trouv�e"));
		}
	}
	
	public void envoyerMessage(String m) {
		if(this.gestionnaire == null) {
			try {
				Socket sock = new Socket (this.user.getAddrIP(),Property.TCPServerSocketPort);
				gestionnaire= new GestionnaireConversation(sock,user);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		gestionnaire.envoyerMessage(m);
	}

	public GestionnaireConversation getGestionnaire() {
		return gestionnaire;
	}

	public void envoyerFicher(File f) {
		if(this.gestionnaire == null) {
			try {
				Socket sock = new Socket (this.user.getAddrIP(),Property.TCPServerSocketPort);
				gestionnaire= new GestionnaireConversation(sock,user);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		gestionnaire.envoyerFicher(f);
	}
	public void setGestionnaire(GestionnaireConversation gestionnaire) {
		this.gestionnaire = gestionnaire;
	}


	public void fin() {
		if (this.gestionnaire != null) {
			this.gestionnaire.fin();
		}
	}


	public Utilisateur getUser() {
		return user;
	}


}