package src.application;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import src.model.DatagramUDP;

import src.model.Utilisateur;

public class ListenerBroadcast extends Thread {
	private DatagramSocket serveur;
	private int port;
	private boolean isRunning = true;
	public ListenerBroadcast(int port) {
		try {
			this.serveur = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		try {
			serveur.setSoTimeout(1000);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.port = port;
	}
	public void fin() {
		isRunning = false;
		this.interrupt();
	}

	public void run() {

		while (isRunning) {

			// On s'occupe maintenant de l'objet paquet
			byte[] buffer = new byte[8192];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			// Cette méthode permet de récupérer le datagramme envoyé par le client
			// Elle bloque le thread jusqu'à ce que celui-ci ait reçu quelque chose.
			try {
				serveur.receive(packet);
				Utilisateur nouvel_utilisateur = null;
				ObjectInputStream OIS = null;
				DatagramUDP datagram = null;
				try {
					OIS = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				try {
					datagram = (DatagramUDP) OIS.readObject();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if(datagram.type.equals("User")) {
					nouvel_utilisateur = datagram.payload;

					if (Arrays.equals(nouvel_utilisateur.getAddrMAC(), ChatSystem.self.getAddrMAC())
							&& nouvel_utilisateur.getAddrIP().equals(ChatSystem.self.getAddrIP())) {
						System.out.println("reception du \"self\"");
					} 
					else {
						if(nouvel_utilisateur.getOnline()) {
							System.out.println("Reception UDP : "+nouvel_utilisateur.toString() + " is Online");
							ChatSystem.popup("new_user.png","Utilisateur en ligne", nouvel_utilisateur.getPseudo());
							HistoriqueDAO.getInstance().updateUser(nouvel_utilisateur);
						}
						else {
							System.out.println("Reception UDP : "+nouvel_utilisateur.toString() + " is Offline");
						}
						ChatSystem.addUtilisateur(nouvel_utilisateur);
						packet.setLength(buffer.length);
						if (nouvel_utilisateur.getOnline()==false && nouvel_utilisateur.getPseudo().equals("TBD")) {
							ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
							ObjectOutputStream OOS = null;
							try {
								OOS = new ObjectOutputStream(BAOS);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							try {
								OOS.writeObject(new DatagramUDP("User",ChatSystem.self));
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							byte[] buffer2 = BAOS.toByteArray();
							DatagramPacket packet2 = new DatagramPacket(buffer2, // Les données
									buffer2.length, // La taille des données
									packet.getAddress(), // L'adresse de l'émetteur
									port // Le port de l'émetteur
									);

							// Et on envoie vers l'émetteur du datagramme reçu précédemment
							try {
								serveur.send(packet2);
							} catch (IOException e) {
								e.printStackTrace();
							}
							packet2.setLength(buffer2.length);
						}
					}
				}
				else {
					if(datagram.type.equals("Fin User")) {
						System.out.println("PASS");
						Utilisateur UserDisconnecting = datagram.payload;
						for(Utilisateur u : ChatSystem.tableUtilisateur) {
							if (u.getAddrMAC().equals(UserDisconnecting.getAddrMAC())) {
								ChatSystem.getConv(u).fin();
								ChatSystem.tableUtilisateur.remove(u);
								HistoriqueDAO.getInstance().updateUser(UserDisconnecting);
								System.out.println(u.getPseudo() + " Disconected");
								break;
							}
						}
						//a voirs ici si il y a pas des truc en plus a faire a ce moment 
					}
				}
			} catch (IOException e) {
				//do nothing it's the Listener BR timeout to kill it at the end
			}
		}
	}
}