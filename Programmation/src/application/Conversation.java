package src.application;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import src.resources.Properties;
import src.model.Historique;
import src.model.Utilisateur;

public class Conversation {
	private Historique historique;
	private GestionnaireConversation gestionnaire;
	
	public Conversation(Utilisateur u) {
		this.historique = new Historique(u);
	}
	
	
	public void nouveauSYN(Socket sock) { //instentie le gestionnaire conversation du SYN reÃ§us pour commencer la conversation avec la session distante ou resyncronyse si la connexion a Ã©tÃ© perdu
		Boolean done =false ;
		for (Conversation conv : ChatSystem.convs) {
			if (conv.getHistorique().getContact().getAddrIP().equals(sock.getInetAddress())) {
				if(conv.getGestionnaire() == null) { 
					conv.setGestionnaire(new GestionnaireConversation(sock,historique));	
				
				}
				done = true;
				break;
			}
			
		}
		if (!done ) {
			throw (new NoSuchFieldError("Erreur dans la reception du Syn sur le socket:" + sock.toString()+ "\n" + "la conversassion associée n'a pas été trouvée"));
		}
	}
	
	public void envoyerMessage(String m) {
		if(this.gestionnaire == null) {
			try {
				Socket sock = new Socket (this.historique.getContact().getAddrIP(),Properties.TCPServerSocketPort);
				gestionnaire= new GestionnaireConversation(sock,historique);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		gestionnaire.envoyerMessage(m);
	}

	public Historique getHistorique() {
		return historique;
	}

	public void setHistorique(Historique historique) {
		this.historique = historique;
	}

	public GestionnaireConversation getGestionnaire() {
		return gestionnaire;
	}

	public void envoyerFicher(File f) {
		if(this.gestionnaire == null) {
			try {
				Socket sock = new Socket (this.historique.getContact().getAddrIP(),Properties.TCPServerSocketPort);
				gestionnaire= new GestionnaireConversation(sock,historique);
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
		this.gestionnaire.fin();
		
	}

}