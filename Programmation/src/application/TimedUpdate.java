package src.application;

import java.util.ArrayList;

import src.model.Utilisateur;

public class TimedUpdate extends Thread{
	private Boolean isRunning;

	public TimedUpdate(){
		isRunning = true;
		this.run();
	}

	public void run() {
		while(isRunning) {
			ArrayList<Utilisateur> TableDistante = UtilisateurDAO.getInstance().getOnlineUsers();
			for(Utilisateur u : TableDistante) {
				ChatSystem.addUtilisateur(u);
			}
			try {
				TimedUpdate.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
	
	public void fin() {
		isRunning = false;
	}
	
}
