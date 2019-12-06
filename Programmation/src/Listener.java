package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Listener extends Thread {
	private ServerSocket server;
	private static ArrayList<Conversation> convs;

	public Listener() {
		try {
			server = new ServerSocket(12345);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// recupere les conversations ouvertes lors de la derniere utilisation
		convs = Conversation.getConvs();
	}

	public void run() {
		while (true) {
			try {
				Socket sock = server.accept();
				connectConversation(new GestionnaireConversation(sock));
			} catch (Exception e) {
				System.out.println("Erreur acceptation de connexion");
				return;
			}

		}

	}

	private void connectConversation(GestionnaireConversation g) {
		// cherche l'utilisateur associe a l'ip de la conversation initiee
		Utilisateur contact = null;
		for (Utilisateur u : ChatSystem.tableUtilisateur) {
			if (u.getAddrIP().equals(g.getSock().getInetAddress().toString())) {
				contact = u;
			}
		}
		// cherche la conversation associee avec le contact, ajoute le gestionnaire de
		// conv, et start
		for (Conversation c : convs) {
			if (c.getHistorique().getContact().equals(contact)) {
				c.setGestionnaire(g);
				g.start();
			}
		}
	}
	
	public static Conversation getConversationByGestionnaire(GestionnaireConversation g) {
		Conversation found = null;
		for (Conversation c : convs) {
			if (c.getGestionnaire().equals(g)) {
				found = c;
			}
		}
		return found;
	}
	
	public void fin() {
		
	}
}