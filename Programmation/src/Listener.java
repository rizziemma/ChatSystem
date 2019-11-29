package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Listener extends Thread {
	private ServerSocket server;
	private ArrayList<GestionnaireConversation> convs;
	
	public Listener() {
		try {
			server = new ServerSocket(12345);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(true) {
			try {
                Socket sock = server.accept();
                convs.add(new GestionnaireConversation(sock));
            }
            catch(Exception e){
                System.out.println("Erreur acceptation de connexion");
                return;
            }
			
			convs.removeIf(g -> (g.getSock() == null));
		}
		
	}
	
}