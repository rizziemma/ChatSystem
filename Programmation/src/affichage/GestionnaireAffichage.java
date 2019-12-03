package src.affichage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GestionnaireAffichage implements ActionListener{
	MainFrame main;
	ContactsFrame contacts;
	ConversationFrame conversation;
	
	public GestionnaireAffichage () {
		//TODO
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		main = new MainFrame();
		contacts = new ContactsFrame();
		conversation = new ConversationFrame();
		
	}

}
