package src;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import src.Utilisateur;
import src.UtilisateurDAO;

public class Initialiseur {


	public Utilisateur initApp () {
		InetAddress address = null;
		try {
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    NetworkInterface ni = null;
		try {
			ni = NetworkInterface.getByInetAddress(address);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    byte[] mac = null;
		try {
			mac = ni.getHardwareAddress();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Utilisateur self = new Utilisateur();
		demandeTableUtilisateur(mac);
		self.setPseudo(choixPseudo());
		self.setStatus("Nouvel Utilisateur");
		self.setAddrMAC(address.toString());
		self.setAddrIP(mac.toString());
		self.setDerniereConnexion(new Date());
		return(self);
	}
	private void initListener () {
		//TODO
	}
	private ListenerBroadcast initListenerBroadcast (int port) throws UnknownHostException{
		ListenerBroadcast listener = new ListenerBroadcast(port, InetAddress.getByName(ChatSystem.self.getAddrIP()) ,(ChatSystem.self.getAddrMAC()).getBytes() ,ChatSystem.self.getPseudo() );
		listener.run();
		return listener;
		
	}

	private String demandePseudo(String message) {
		//java Swing
		return null;
	}

	private ListenerBroadcast demandeTableUtilisateur (byte [] mac) {
		ChatSystem.addAllUsers(UtilisateurDAO.getTable()); //table venant du serveur
		int port = 42069;
		ListenerBroadcast listenerBR=null;
		try {
			listenerBR = initListenerBroadcast(port);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		DatagramSocket UDPsocket=null;
		try {
			UDPsocket = new DatagramSocket(port);
		} catch (SocketException e) {
			System.out.println("Impossible de creer le socket UDP");
			e.printStackTrace();
		}
		InetAddress brAddr = null;
		try {
			brAddr = InetAddress.getByName("255.255.255.255");
		} catch (UnknownHostException e) {
			System.out.println("Impossible recuperer l'adresse de Broadcast UDP");
			e.printStackTrace();
		}
		byte buffer[] = (mac.toString () + new String((new Date()).toString())).getBytes();
		DatagramPacket UDPpacket = new DatagramPacket(buffer,buffer.length,brAddr,port);
		try {
			UDPsocket.setBroadcast(true);
		} catch (SocketException e) {
			System.out.println("Impossible configurer le socket UDP en broadcast");
			e.printStackTrace();
		}
		try {
			UDPsocket.send(UDPpacket);
		} catch (IOException e) {
			System.out.println("Impossible d'envoyer le paquer UDP en broadcast");
			e.printStackTrace();
		}
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			System.out.println("Impossible de sleep (dans Initialiseur.demandeTableUtilisateur)");
			e.printStackTrace();
		}		
		return listenerBR;
	}

	private String choixPseudo() {
		boolean done = false;
		String pseudo=null;
		boolean libre=true;
		while(!done ){
			if (libre==false){
				pseudo = demandePseudo("Pseudo deja pris choisisez en un autre");
			}
			else {
				pseudo = demandePseudo("Choisisez un Pseudo");
			}
			libre = true;
			for (Utilisateur user : ChatSystem.tableUtilisateur){
				if(pseudo == user.getPseudo()){
					libre = false;
				}
			}
			if(libre = true){
				done = true;
			}
		}
		return pseudo;
	}





}