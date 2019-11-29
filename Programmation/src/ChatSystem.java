package src;

import java.util.ArrayList;

public class ChatSystem {

	public static ArrayList<Utilisateur> tableUtilisateur;
	public static Utilisateur self;
	public static Boolean doRun;
	public static void addUtilisateur(Utilisateur nouvel_utilisateur) {
		if(nouvel_utilisateur.getStatus().equals("NEW")) {
			tableUtilisateur.add(nouvel_utilisateur);
		}
		else {
			for (Utilisateur user : tableUtilisateur) {
				if(user.getAddrMAC().equals(nouvel_utilisateur.getAddrMAC())) {
					user.setPseudo(nouvel_utilisateur.getPseudo());
					user.setStatus(nouvel_utilisateur.getStatus());
					user.setAddrIP(nouvel_utilisateur.getAddrMAC());
					user.setDerniereConnexion(nouvel_utilisateur.getDerniereConnexion());
					break;
				}
			}
		}
	}

	public static void addAllUsers(ArrayList<Utilisateur> table) {
		tableUtilisateur.addAll(table);
		
	}
	
	

	public static void main(String[] args) {
		doRun = true;
		tableUtilisateur = new ArrayList<Utilisateur>();
		self = new Utilisateur();
		Initialiseur.initApp();
		doRun = false;
	}
	
	
}
