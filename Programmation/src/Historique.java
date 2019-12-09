package src;

import java.util.ArrayList;

public class Historique {

	private Utilisateur contact;

	private ArrayList<Message> messages;

	public Historique() {
		super();
	}

	public Utilisateur getContact() {
		return contact;
	}

	public void setContact(Utilisateur contact) {
		this.contact = contact;
	}

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}

	public void nouveauMessage(Message m) {
		messages.add(m);
	}
}