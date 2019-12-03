package src;

import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

public class Historique {
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "CONTACT", referencedColumnName = "ID")
	private Utilisateur contact;

	@OneToMany(mappedBy = "MESSAGES")
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
		HistoriqueDAO dao = new HistoriqueDAO();
		dao.updateHistorique(this);
	}
}