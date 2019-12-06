package src;

import java.util.ArrayList;

public class Conversation {
	private Historique historique;
	private GestionnaireConversation gestionnaire;

	public void nouveauMessage(Message m) {
		// if gestionnaire = null
		// creer gestionnaire, creation du socket

		// verifie si message envoyé ou recu
		// ajout a la bdd locale historique
		// envoi par le gestionnaire de conv
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

	public static ArrayList<Conversation> getConvs() {
		// TODO
		// recuperer la liste des convs associees aux utilisateurs connectes mtn + convs
		// dans lhistorique, avec status connecte ou pas

		return null;
	}

}