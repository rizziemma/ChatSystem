package src.application;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import src.model.Datagram;
import src.model.Datatype;
import src.model.FichierEnTransit;
import src.model.Historique;
import src.model.Utilisateur;
import src.resources.Properties;

public class GestionnaireConversation extends Thread {
	private Socket sock;
	private boolean isRunning = true;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	private Historique h;

	public GestionnaireConversation(Socket sock,Historique hist) {
		this.sock = sock;
		this.h = hist;
		try {
			this.out = new ObjectOutputStream(sock.getOutputStream());
			InputStream IS = sock.getInputStream();
			BufferedInputStream BIS = new BufferedInputStream(IS);
			this.in = new ObjectInputStream(BIS);


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		this.start();
	}


	public void run() {
		while(this.isRunning) {
			try {
				Datagram Data = (Datagram) in.readObject(); //NOUVEAU MESSAGE RECU
				switch(Data.getType()) {
				case MESSAGE:
					traiterMessage(Data);
					break;
				case UTILISATEUR:
					traiterUtilisateur(Data);
					break;
				case VU:
					traiterVu();
					break;
				case FICHIER:
					traiterFichier(Data);
					break;
				default:
					System.out.println("Datagram not recognised");
					break;
				}

			} catch (IOException | ClassNotFoundException e) {
				//e.printStackTrace();
			}
		}
		//this.sock = null;

	}

	private void traiterFichier(Datagram data) {
		FileOutputStream fos = null;
		FichierEnTransit fet = (FichierEnTransit)data.getData();
		File f = new File(Properties.PathToAppFiles+Properties.Downloads + fet.nom);
		int i = 0;
		while(f.exists()) {
			f= new File(Properties.PathToAppFiles+Properties.Downloads + fet.nom + "(" + i + ")");
		}
		try {
			fos = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fos.write(fet.barray);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ChatSystem.popup("file_received.png","Fichier reçu",fet.nom);
	}

	public void envoyerFicher(File f) {
		 FileInputStream fis = null;
		 BufferedInputStream bis = null;
		byte [] barray = new byte[(int)f.length()];
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bis = new BufferedInputStream(fis);
		try {
			bis.read(barray,0,barray.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Datagram data = new Datagram(Datatype.FICHIER,(Object) (new FichierEnTransit(barray,f.getName())));
		sendDatagram(data);
	}
	
	
	private void traiterUtilisateur(Datagram data) {
		Utilisateur u = (Utilisateur)data.getData();
		ChatSystem.addUtilisateur(u);
		ChatSystem.popup("new_user.png","Nouveau Status",u.getPseudo());
	}


	private void traiterMessage(Datagram data) {
		System.out.println("message reçu : " + (String)data.getData());
		h.addMessage(data);
		HistoriqueDAO DAO = HistoriqueDAO.getInstance();
		DAO.nouveauDatagramme(h,data);
	}

	private void sendDatagram(Datagram data) {
		try {
			out.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void envoyerMessage(String m) {
		System.out.println("envoie du message : " +  m);
		Datagram data = new Datagram(Datatype.MESSAGE, (Object)m);
		sendDatagram(data);
	}

	public void envoyerUtilisateur(Utilisateur u) {
		Datagram data = new Datagram(Datatype.UTILISATEUR, (Object)u);
		try {
			out.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void traiterVu() {
		//TODO envoyer a observer +dao +local (ordre important)
	}


	public void envoyerVu() {
		Datagram data = new Datagram(Datatype.VU,null);
		try {
			out.writeObject(data);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		//TODO notifier la BDD locale du VU envoyé
	}

	public void fin() { // arrete le thread proprement (fin du thread dans le Run())
		
		this.isRunning = false;
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//in = null;		
	}
}
