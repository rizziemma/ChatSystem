package src;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import resources.Properties;
import src.model.Utilisateur;

public class Initialiseur {
	
	static DatagramSocket UDPsocket = null;
	static int portBRServer = 42069;
	static int portBRClient = 42070;
	public static void initApp() {
		ChatSystem.List = initListener();
		InetAddress lanIp = null;
		try {
			String ipAddress = null;
			Enumeration<NetworkInterface> net = null;
			net = NetworkInterface.getNetworkInterfaces();

			while (net.hasMoreElements()) {
				NetworkInterface element = net.nextElement();
				if (!element.getName().equals("lo")) {
					Enumeration<InetAddress> addresses = element.getInetAddresses();

					while (addresses.hasMoreElements() && element.getHardwareAddress().length > 0) {
						InetAddress ip = addresses.nextElement();
						if (ip instanceof Inet4Address) {

							if (ip.isSiteLocalAddress()) {
								ipAddress = ip.getHostAddress();
								lanIp = InetAddress.getByName(ipAddress);
							}

						}
					}
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		System.out.println(lanIp.toString());
		NetworkInterface ni = null;
		try {
			ni = NetworkInterface.getByInetAddress(lanIp);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		byte[] mac = null;
		try {
			mac = ni.getHardwareAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		System.out.println(String.format("%2x", mac[0]) + ":" + String.format("%2x", mac[1]) + ":"
				+ String.format("%2x", mac[2]) + ":" + String.format("%2x", mac[3]) + ":" + String.format("%2x", mac[4])
				+ ":" + String.format("%2x", mac[5]));
		ChatSystem.self.setAddrIP(lanIp);
		ChatSystem.self.setAddrMAC(mac);
		ChatSystem.ListBR=demandeTableUtilisateur(mac, lanIp);
		ChatSystem.self.setPseudo(choixPseudo());
		ChatSystem.self.setStatus("Nouvel Utilisateur");
		ChatSystem.self.setDerniereConnexion(new Date());
		notifyNewPseudo();
		
		ChatSystem.convs = new ArrayList<Conversation>();
		initBaseLocale();

	}

	private static void notifyNewPseudo(/*self*/) {
		try {
			UDPsocket = new DatagramSocket(portBRServer);
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
		ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
		ObjectOutputStream OOS = null;
		try {
			OOS = new ObjectOutputStream(BAOS);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			OOS.writeObject(ChatSystem.self);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		byte[] buffer = BAOS.toByteArray();
		DatagramPacket UDPpacket = new DatagramPacket(buffer, buffer.length, brAddr, portBRClient);
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
		UDPsocket.close();
	}
	
	
	private static Listener initListener() {
		Listener L = new Listener();
		L.start();
		return(L);
	}

	private static ListenerBroadcast initListenerBroadcast(int port) throws UnknownHostException {
		ListenerBroadcast listener = new ListenerBroadcast(port);
		listener.start();
		return listener;

	}

	private static String demandePseudo(String message) {
		// java Swing
		return "TestUser "+ChatSystem.self.getAddrIP();
	}

	private static ListenerBroadcast demandeTableUtilisateur(byte[] mac, InetAddress Ip) {
		// ChatSystem.addAllUsers(UtilisateurDAO.getTable()); //table venant du serveur


		ListenerBroadcast listenerBR = null;
		try {
			listenerBR = initListenerBroadcast(portBRClient);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		try {
			UDPsocket = new DatagramSocket(portBRServer);
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
		ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
		ObjectOutputStream OOS = null;
		try {
			OOS = new ObjectOutputStream(BAOS);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			OOS.writeObject(new Utilisateur("TBD", Ip, mac, "NEW", new Date()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		byte[] buffer = BAOS.toByteArray();
		DatagramPacket UDPpacket = new DatagramPacket(buffer, buffer.length, brAddr, portBRClient);
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
		UDPsocket.close();
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			System.out.println("Impossible de sleep (dans Initialiseur.demandeTableUtilisateur)");
			e.printStackTrace();
		}
		return listenerBR;
	}

	private static String choixPseudo() {
		boolean done = false;
		String pseudo = null;
		boolean libre = true;
		while (!done) {
			if (libre == false) {
				pseudo = demandePseudo("Pseudo deja pris choisisez en un autre");
			} else {
				pseudo = demandePseudo("Choisisez un Pseudo");
			}
			libre = true;
			for (Utilisateur user : ChatSystem.tableUtilisateur) {
				if (pseudo == user.getPseudo()) {
					libre = false;
				}
			}
			if (libre = true) {
				done = true;
			}
		}
		return pseudo;
	}
	
	private static void initBaseLocale() {
		//Si bdd pas initialisée
		if (!(new File(Properties.BaseLocalePath)).exists()) {
			try {          
	            Connection conn = DriverManager.getConnection(Properties.SQLiteDriver+Properties.BaseLocalePath);
	            //String utilisateurs = "CREATE TABLE UTILISATEUR (PSEUDO text, IP text, MAC text PRIMARY KEY, STATUS text, CONNEXION text);";
	            //String messages = "CREATE TABLE MESSAGE (ID integer PRIMARY KEY, DATE text, TYPE integer, DATA blob, STATUS integer, SENT integer, CONTACT text, FOREIGN KEY (CONTACT) REFERENCES UTILISATEUR(MAC))";
	            String messages = "CREATE TABLE MESSAGE (ID integer PRIMARY KEY, DATE text, TYPE integer, DATA blob, STATUS integer, SENT integer, CONTACT text)";

	            Statement stmt = conn.createStatement();
	            //stmt.execute(utilisateurs);
	            stmt.execute(messages);

	            conn.close();
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
		}
	}

}