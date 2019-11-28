package src;

import java.util.ArrayList;

public class ChatSystem {

	public static ArrayList<Utilisateur> tableUtilisateur;
	public static Utilisateur self;

	public static void addUtilisateur(Utilisateur nouvel_utilisateur) {
		tableUtilisateur.add(nouvel_utilisateur);
	}

	public static void addAllUsers(ArrayList<Utilisateur> table) {
		tableUtilisateur.addAll(table);
		
	}

	
}
