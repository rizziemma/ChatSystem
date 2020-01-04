package model;

import java.util.ArrayList;

public class Historique {

	private Utilisateur contact;

	private ArrayList<Datagram> Datagrammes;

	public Historique() {
		super();
	}
	
	public Historique(Utilisateur u) {
		this.contact = u;
		this.Datagrammes = new ArrayList<Datagram>();
	}

	public Utilisateur getContact() {
		return contact;
	}

	public void setContact(Utilisateur contact) {
		this.contact = contact;
	}

	public ArrayList<Datagram> getMessages() {
		return Datagrammes;
	}

	public void setMessages(ArrayList<Datagram> messages) {
		this.Datagrammes = messages;
	}
	
	public void addMessage(Datagram D) {
		this.Datagrammes.add(D);
	}

	public void nouveauMessage(Datagram m) {
		Datagrammes.add(m);
	}
}