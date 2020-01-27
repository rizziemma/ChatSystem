package src.application;

import java.net.InetAddress;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import src.model.Utilisateur;
public class ChatSystem extends Application {

	public static ArrayList<Utilisateur> tableUtilisateur;
	public static Utilisateur self;
	public static Listener List;
	public static ListenerBroadcast ListBR;
	public static ArrayList<Conversation> convs;
	private static Stage stage;
	private static boolean withServer;


	public static boolean isWithServer() {
		return withServer;
	}

	public static void setWithServer(boolean s) {
		withServer = s;
	}

	public static void addUtilisateur(Utilisateur nouvel_utilisateur) {
		boolean found = false;
		for (Utilisateur user : tableUtilisateur) {
			if (user.getAddrMAC().equals(nouvel_utilisateur.getAddrMAC())) {
				found = true;
				user.setPseudo(nouvel_utilisateur.getPseudo());
				user.setStatus(nouvel_utilisateur.getStatus());
				user.setAddrIP(nouvel_utilisateur.getAddrIP());
				user.setOnline(nouvel_utilisateur.getOnline());
				System.out.println("Update utilisateur : [dans la table utilisateur] \n" + nouvel_utilisateur.toExtendedString());
				break;
			}
		}
		if(!found) {
			tableUtilisateur.add(nouvel_utilisateur);
			System.out.println("Nouvel utilisateur : [dans la table utilisateur] \n" + nouvel_utilisateur.toExtendedString());
		}
		
	}

	public static Utilisateur getUserByIp(InetAddress IP) {
		Utilisateur ret=self;
		for (Utilisateur u : tableUtilisateur) {
			if(u.getAddrIP().equals(IP)) {
				ret = u;
				break;
			}
		}
		return ret;
	}
	public static Utilisateur getUserByPseudo(String s) {
		Utilisateur ret=null;
		for (Utilisateur u : tableUtilisateur) {
			if(u.getPseudo().equals(s)) {
				ret = u;
				break;
			}
		}
		return ret;
	}

	public static Conversation getConv(Utilisateur u) {
		Conversation result = null;
		for (Conversation c : ChatSystem.convs) {
			if (c.getUser().getAddrMAC().equals(u.getAddrMAC())) {
				result = c;
			}
		}
		if (result == null) {
			result = new Conversation(u);
		}
		return result;
	}

	public static void addAllUsers(ArrayList<Utilisateur> table) {
		tableUtilisateur.addAll(table);

	}

	public static String printTableUtilisateur() {
		String str = new String("Table Utilisateurs: \n");
		for (Utilisateur user : tableUtilisateur) {
			str = str + user.toString() + "\n";
		}
		return str;
	}


	@Override
	public void start(Stage primaryStage) {
		try {
			javafx.scene.Parent root = FXMLLoader.load(getClass().getResource("/resources/login.fxml"));
			Scene scene = new Scene (root);
			primaryStage.setTitle("PaChat System");
			primaryStage.setScene(scene);
			primaryStage.setOnCloseRequest(windowEvent -> this.stop());
			primaryStage.show();
			this.stage = primaryStage;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void popup(String imgURL, String titre, String contenu){
		Platform.runLater(() -> {
			Image img = new Image(imgURL);
			org.controlsfx.control.Notifications.create().owner(stage)
			.title(titre).text(contenu)
			.graphic(new ImageView(img)).position(Pos.BOTTOM_LEFT).show();
		});
	}
	
	public static void logout () {
		/* Notifications notifications=Notifications.createLogOutPacket(this.self,null);
        this.sendPacket(notifications);
        System.out.println("Logout send");
        //Deletes all user data, resetting the app as if it was launched for the first time
        INSTANCE = new Controller();
		 */
	}

	public void stop() {
		for(Conversation c:convs) {
			c.fin();
		}
		ListBR.fin();
		List.fin();
		//stop threads
	}

	public static void main(String[] args) {
		//init les variables
		tableUtilisateur = new ArrayList<Utilisateur>();
		self = new Utilisateur();

		Initialiseur.initApp();

		//lancement affichage
		launch(args);

		//TEST
		/*while(tableUtilisateur.isEmpty()) {}
		Conversation conv = new Conversation(tableUtilisateur.get(0));
		conv.nouveauMessage("Ceci est un test");
		conv.nouveauMessage("TEST2");
		conv.nouveauMessage("TEST3");
		conv.nouveauMessage("TEST4");
		conv.nouveauMessage("TEST5");
		conv.nouveauMessage("TEST6");


		ListBR.interrupt();
		List.fin();
		System.out.println(printTableUtilisateur());
		 */

	}

}
