package src;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import src.model.Historique;
import src.model.Utilisateur;

public class Conversation {
	private Historique historique;
	private GestionnaireConversation gestionnaire;
	
	public Conversation(Utilisateur u) {
		this.historique = new Historique(u);
		HistoriqueDAO DAO = new HistoriqueDAO();
		historique.setMessages(DAO.getDatagrams10(historique));
		DAO.close();
		//TODO notify affichage messages chargés
	}
	
	
	public void nouveauSYN(Socket sock) { //instentie le gestionnaire conversation du SYN reçus pour commencer la conversation avec la session distante ou resyncronyse si la connexion a été perdu
		for (Conversation conv : ChatSystem.convs) {
			if (conv.getHistorique().getContact().getAddrIP().equals(sock.getInetAddress().toString())) {
				if(conv.getGestionnaire() == null) {
					conv.setGestionnaire( new GestionnaireConversation(sock,historique));
				}
				else {
					//TODO arrive uniquement en cas de perte de connexion d'une des deux machines
				}
				
				break;
			}
			
		}
	}
	
	
	public void nouveauMessage(String m) {
		if(this.gestionnaire == null) {
			try {
				gestionnaire= new GestionnaireConversation(new Socket(InetAddress.getByName(this.historique.getContact().getAddrIP()),ChatSystem.List.getPort()),historique);
			} catch (IOException e) {
				e.printStackTrace();
			}
			gestionnaire.envoyerMessage(m);
		}
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

	public void setGestionnaire(GestionnaireConversation gestionnaire) {
		this.gestionnaire = gestionnaire;
	}

}