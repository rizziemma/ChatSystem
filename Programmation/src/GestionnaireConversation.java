package src;

import java.net.Socket;

public class GestionnaireConversation extends Thread{
	private Socket sock;
	private Historique historique;

	public GestionnaireConversation (Socket sock) {
		this.sock=sock;
		HistoriqueDAO dao = new HistoriqueDAO();
		this.historique = dao.getHistoriqueByIP(sock.getInetAddress());
	}

	public Socket getSock() {
		return sock;
	}
	
	public run() {
		//check en continue si on recoit des messages, puis update bdd
		//receive doit pas etre bloquant pour donner la main a l'envoi regulierement
		//fonction pour envoyer un message 
		
	}
}
