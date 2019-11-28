package src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

public class ListenerBroadcast extends Thread{
	private DatagramSocket serveur;
	private int port;
	private InetAddress AddrIp;
	private byte[] mac;
	private String pseudo;

public ListenerBroadcast(int port,InetAddress AddrIp,byte[] mac,String pseudo) {
	this.port=port;
	this.AddrIp=AddrIp;
	this.mac = mac;
	this.pseudo=pseudo;
	this.serveur=null;	
}
	
	
	public void run() {

		try {
			serveur = new DatagramSocket(port);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(true){

			//On s'occupe maintenant de l'objet paquet
			byte[] buffer = new byte[8192];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			//Cette méthode permet de récupérer le datagramme envoyé par le client
			//Elle bloque le thread jusqu'à ce que celui-ci ait reçu quelque chose.
			try {
				serveur.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String recu = new String (packet.getData());
			String[] part = recu.split(" ");
			if (part.length == 2){//reception d'un nouvel utilisateur sans pseudo avec un statu NEW
				@SuppressWarnings("deprecation")
				Date d = new Date(part[1]);
				Utilisateur nouvel_utilisateur = new Utilisateur(/*??? TODO*/0, "TBD",packet.getAddress().toString() ,part[0],"NEW",d);
				ChatSystem.addUtilisateur(nouvel_utilisateur);



				packet.setLength(buffer.length);

				//et nous allons répondre à notre client, donc même principe
				byte[] buffer2 = new String(AddrIp.toString() + mac.toString() + pseudo + (new Date()).toString() ).getBytes();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				packet2.setLength(buffer2.length);
			}
			else {
				if(part.length== 3) {//reception du pseudo d'un utilisateur -> ajout a la table + mise a jour du status vers connecté
					//TODO
				}
			}
		}
	}
}