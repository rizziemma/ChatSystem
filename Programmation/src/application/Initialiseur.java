package src.application;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
import java.util.Enumeration;
import java.util.Properties;

import src.model.DatagramUDP;
import src.model.Utilisateur;
import src.resources.Property;

public class Initialiseur {

	static DatagramSocket UDPsocket = null;
	static int portBRServer = 42069;
	static int portBRClient = 42070;
	public static void initApp() {
		initFolders();
		initBaseLocale();
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
		ChatSystem.self= new Utilisateur("not chosen yet",lanIp,mac,"",false);
		System.out.println("Self Initialised: \n" + ChatSystem.self.toExtendedString());
		ChatSystem.ListBR=demandeTableUtilisateur(mac, lanIp);
		ChatSystem.convs = new ArrayList<Conversation>();




	}

	public static void deconnexion() {
		//diffusion du message de fin de session en UDP broadcast
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
		ChatSystem.self.setOnline(false);
		try {
			OOS.writeObject(new DatagramUDP("Fin User",ChatSystem.self));
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
		System.out.println("Deconnexion en local");
		//fermeture des convesations en cours 
		for(Conversation c:ChatSystem.convs) {
			c.fin();
		}
		
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
			OOS.writeObject(new DatagramUDP("User",ChatSystem.self));
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
			OOS.writeObject(new DatagramUDP("User",new Utilisateur("TBD", Ip, mac,"Status", false)));
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
		return listenerBR;
	}


	public static boolean changerPseudo(String pseudo) {
		boolean libre = true;
		for (Utilisateur user : ChatSystem.tableUtilisateur) {
			if (pseudo == user.getPseudo()) {
				libre = false;
			}
		}
		if(libre) { 
			ChatSystem.self.setPseudo(pseudo);   
			ChatSystem.self.setOnline(true);
			notifyNewPseudo();
		}
		return libre;
	}

	private static void initBaseLocale() {
		//Si bdd pas initialisée
		if (!(new File(Property.PathToAppFiles+Property.BaseLocale)).exists()) {
			try {          
				Connection conn = DriverManager.getConnection(Property.SQLiteDriver+Property.PathToAppFiles+Property.BaseLocale);
				String utilisateurs = "CREATE TABLE UTILISATEUR (MAC text PRIMARY KEY, PSEUDO text, ONLINE integer)";
				//String messages = "CREATE TABLE MESSAGE (ID integer PRIMARY KEY, DATE text, TYPE integer, DATA blob, STATUS integer, SENT integer, CONTACT text, FOREIGN KEY (CONTACT) REFERENCES UTILISATEUR(MAC))";
				String messages = "CREATE TABLE MESSAGE (ID integer PRIMARY KEY, DATE text, TYPE integer, DATA blob, STATUS integer, SENT integer, CONTACT text)";
				String index = "CREATE UNIQUE INDEX idx_mac_addr ON UTILISATEUR(MAC)";
				Statement stmt = conn.createStatement();
				stmt.execute(utilisateurs);
				stmt.execute(messages);
				stmt.execute(index);

				conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				//e.printStackTrace();
			}
		}
	}

	private static void initFolders() {
		File dir = new File(Property.PathToAppFiles);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File bdd = new File(Property.PathToAppFiles+"data");
		if(!bdd.exists()) {
			bdd.mkdir();
		}
		File dl = new File(Property.PathToAppFiles+Property.Downloads);
		if(!dl.exists()) {
			dl.mkdir();
		}
		File propfile = new File(Property.PathToAppFiles+"config.properties");
		if(!propfile.exists()) {
			try {
				propfile.createNewFile();
				Properties prop = new Properties();
				OutputStream output = new FileOutputStream(Property.PathToAppFiles+"config.properties");
				prop.setProperty("TCPServerSocketPort", "12345");
				prop.setProperty("portBRServer", "42069");
				prop.setProperty("portBRClient", "42070");

				prop.store(output, null);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}