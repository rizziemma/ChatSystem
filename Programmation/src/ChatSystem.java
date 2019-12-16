package src;

import java.net.InetAddress;
import java.util.ArrayList;

import src.model.Utilisateur;

public class ChatSystem {

	public static ArrayList<Utilisateur> tableUtilisateur;
	public static Utilisateur self;
	public static Listener List;
	public static ListenerBroadcast ListBR;
	public static ArrayList<Conversation> convs;
	

	public static void addUtilisateur(Utilisateur nouvel_utilisateur) {
		if (nouvel_utilisateur.getStatus().equals("NEW")) {
			tableUtilisateur.add(nouvel_utilisateur);
		} else {
			for (Utilisateur user : tableUtilisateur) {
				if (user.getAddrMAC().equals(nouvel_utilisateur.getAddrMAC())) {
					user.setPseudo(nouvel_utilisateur.getPseudo());
					user.setStatus(nouvel_utilisateur.getStatus());
					user.setAddrIP(nouvel_utilisateur.getAddrIP());
					user.setAddrMAC(nouvel_utilisateur.getAddrMAC());
					user.setDerniereConnexion(nouvel_utilisateur.getDerniereConnexion());
					break;
				}
			}
		}
	}
	
	public static Utilisateur getUserByIp(InetAddress IP) {
		Utilisateur ret=null;
		for (Utilisateur u : tableUtilisateur) {
			if(u.getAddrIP().equals(IP.toString())) {
				ret = u;
				break;
			}
		}
		return ret;
	}
	
	public static void addAllUsers(ArrayList<Utilisateur> table) {
		tableUtilisateur.addAll(table);

	}

	public static String printTableUtilisateur() {
		String str = new String("Table Utilisateurs: \n");
		for (Utilisateur user : tableUtilisateur) {
			str = str + user.toString() + "\n";
		}
		return str;
	}

	public static void main(String[] args) {
		tableUtilisateur = new ArrayList<Utilisateur>();
		self = new Utilisateur();
		Initialiseur.initApp();
		while(tableUtilisateur.isEmpty()) {}
		Conversation conv = new Conversation(tableUtilisateur.get(0));
		conv.nouveauMessage("Ceci est un test");
		
		
		
		ListBR.interrupt();
		List.fin();
		System.out.println(printTableUtilisateur());

	}

}
