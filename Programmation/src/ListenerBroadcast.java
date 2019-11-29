package src;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ListenerBroadcast extends Thread{
	private DatagramSocket serveur;
	private int port;
	
	
	public ListenerBroadcast(int port) {
		this.port=port;
		try {
			this.serveur=new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}	
	}


	public void run() {


		while(ChatSystem.doRun){

			//On s'occupe maintenant de l'objet paquet
			byte[] buffer = new byte[8192];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			//Cette méthode permet de récupérer le datagramme envoyé par le client
			//Elle bloque le thread jusqu'à ce que celui-ci ait reçu quelque chose.
			try {
				serveur.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Message reçu");
			Utilisateur nouvel_utilisateur= null;
			ObjectInputStream OIS = null;
			try {
				OIS = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				nouvel_utilisateur= (Utilisateur)OIS.readObject();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println(nouvel_utilisateur.toString());
			ChatSystem.addUtilisateur(nouvel_utilisateur);
			packet.setLength(buffer.length);
			if(nouvel_utilisateur.getStatus().equals("NEW")) {
				ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
				ObjectOutputStream OOS = null;
				try {
					OOS = new ObjectOutputStream (BAOS);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					OOS.writeObject(ChatSystem.self);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				byte[] buffer2 =  BAOS.toByteArray();
				DatagramPacket packet2 = new DatagramPacket(
						buffer2,             //Les données 
						buffer2.length,      //La taille des données
						packet.getAddress(), //L'adresse de l'émetteur
						packet.getPort()     //Le port de l'émetteur
						);

				//Et on envoie vers l'émetteur du datagramme reçu précédemment
				try {
					serveur.send(packet2);
				} catch (IOException e) {
					e.printStackTrace();
				}
				packet2.setLength(buffer2.length);
			}
		}
	}
}