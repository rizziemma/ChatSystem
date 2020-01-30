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
import java.io.Reader;
import java.net.Socket;
import java.util.Properties;

import src.model.Datagram;
import src.model.Datatype;
import src.model.FichierEnTransit;
import src.model.Utilisateur;
import src.resources.Property;

public class GestionnaireConversation extends Thread {
	private Socket sock;
	private boolean isRunning = true;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	private Utilisateur user;

	public GestionnaireConversation(Socket sock,Utilisateur u) {
		this.setName("Conv " + u.getPseudo());
		this.sock = sock;
		this.user = u;
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


	@Override
	public void run() {
		while(this.isRunning) {
			try {
				Datagram Data = (Datagram) in.readObject(); //NOUVEAU MESSAGE RECU
				Data.setSent(false);
				switch(Data.getType()) {
				case MESSAGE:
					traiterMessage(Data);
					break;
				case UTILISATEUR:
					traiterUtilisateur(Data);
					break;
					/*case VU:
					traiterVu();
					break;*/
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
		InputStream input = null;
		try {
			input = new FileInputStream(Property.PathToAppFiles+"config.properties");
		} 
		catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		Properties prop = new Properties();
		try {
			prop.load(input);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File f = new File(prop.getProperty("pathToDowloads") + fet.nom);
		int i = 0;
		while(f.exists()) {
			f= new File(prop.getProperty("pathToDowloads") + fet.nom + "(" + i + ")");
		}
		/*try {
			f.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
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
		HistoriqueDAO.getInstance().nouveauDatagramme(this.user, data);
		ChatSystem.popup("/resources/file_received.png","Fichier reçu",fet.nom);
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
		Datagram data = new Datagram(Datatype.FICHIER,(new FichierEnTransit(barray,f.getName())));
		sendDatagram(data);
		HistoriqueDAO.getInstance().nouveauDatagramme(user,data);
	}


	private void traiterUtilisateur(Datagram data) {
		Utilisateur u = (Utilisateur)data.getData();
		ChatSystem.addUtilisateur(u);
		ChatSystem.popup("/resources/new_user.png","Nouveau Status",u.getPseudo());
	}


	private void traiterMessage(Datagram data) {
		System.out.println("message reçu : " + (String)data.getData());
		data.setSent(false);
		//h.addMessage(data);
		HistoriqueDAO.getInstance().nouveauDatagramme(user,data);
	}

	private void sendDatagram(Datagram data) {
		data.setSent(true);
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
		Datagram data = new Datagram(Datatype.MESSAGE, m);
		sendDatagram(data);

		HistoriqueDAO.getInstance().nouveauDatagramme(user,data);
	}

	public void envoyerUtilisateur(Utilisateur u) {
		Datagram data = new Datagram(Datatype.UTILISATEUR, u);
		try {
			out.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*

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
	 */
	public void fin() { // arrete le thread proprement (fin du thread dans le Run())

		this.isRunning = false;
		try {
			in.close();
		} catch (IOException e1) {
			//e1.printStackTrace();
		}
		try {
			sock.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		//in = null;	
	}

}
