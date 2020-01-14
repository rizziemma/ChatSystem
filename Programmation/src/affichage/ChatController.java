package src.affichage;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import src.application.ChatSystem;
import src.application.HistoriqueDAO;
import src.application.Initialiseur;
import src.model.Datagram;
import src.model.Utilisateur;

public class ChatController implements Initializable, PropertyChangeListener {


    private Utilisateur activeUser = null;
    private HistoriqueDAO dao = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userListView.setStyle("-fx-background-insets: 0 ;");
        userListView.setStyle("-fx-control-inner-background: #242A31;");
        userListView.setFixedCellSize(50);
        username.setText(ChatSystem.self.getPseudo());
        dao = HistoriqueDAO.getInstance();
     
        
        messageFeed.setCellFactory(lv -> new ListCell<Datagram>() {
            @Override
            protected void updateItem(Datagram d, boolean empty) {
                super.updateItem(d, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(d.toString());
                    if (d.getSent()) {
                        setStyle("-fx-background-color: #33CEFF");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        hideDistant();
        updateView();
        dao.addObserver(this);
    }

    private void hideDistant() {
        distantUser.setOpacity(0);
        textArea.setOpacity(0);
        closeDiscussionButton.setOpacity(0);
        fileButton.setOpacity(0);
        sendButton.setOpacity(0);
    }


    public void updateView(){
        Platform.runLater(() -> {
            userListView.getItems().clear();
            ArrayList<Utilisateur> online = ChatSystem.tableUtilisateur;
            ArrayList<Utilisateur> offline = new ArrayList<Utilisateur>() ;

           for (Utilisateur current : dao.getContacts()){
        	   Boolean off = true;
        	   for(Utilisateur u : online) {
        		   if(u.getAddrMAC().equals(current.getAddrMAC())) {
        			   off = false;
        			   break;
        		   }
        	   }
        	   if(off) {
        		   offline.add(current);
        	   }
            }
         
            userListView.getItems().addAll(online);
            userListView.getItems().addAll(offline);

            if(activeUser != null) {
                boolean tmp = false ;
                for (Utilisateur u : ChatSystem.tableUtilisateur) {
                    if (u.getAddrIP() == activeUser.getAddrIP()) {
                        tmp = true;
                    }
                }

                if (!tmp) {
                    closeDiscussion();
                }
            }

            String s = online.size()+ "";
            usersOnline.setText(s);
        });
    }

   
    
    private void updateFeed(){
        Platform.runLater(() -> {
            if (activeUser != null) {
                messageFeed.getItems().clear();
                messageFeed.getItems().addAll(dao.getDatagrams(activeUser));
                messageFeed.scrollTo(messageFeed.getItems().size() - 1);
            }
        });
    }
    

    @FXML
    private Label username;

    @FXML
    private Label distantUser;

    @FXML
    private ListView<Utilisateur> userListView;

    @FXML
    private Button closeDiscussionButton;

    @FXML
    private Button fileButton;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea textArea;

    @FXML
    private ListView<Datagram> messageFeed;

    @FXML
    private Label usersOnline;

    @FXML
    public void logOut (MouseEvent event) throws IOException {
        ChatSystem.logout();
        //call controller method Delete all data
        Parent chat_parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/login.fxml")));
        Scene chat_scene = new Scene(chat_parent);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        app_stage.setScene(chat_scene);
        app_stage.show();
    }


    @FXML
    public void About () {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À-propos");
        alert.setHeaderText(null);
        alert.setContentText("Cette application a été développée par Emma Rizzi et Patrick Rousseau dans le cadre du projet de Chat System de 4ème année, INSA Toulouse.");
        alert.showAndWait();
    }


    @FXML
    public void changeUsername () {
        TextInputDialog dialog = new TextInputDialog(username.getText());
        dialog.setTitle("Changer de pseudo");
        dialog.setHeaderText("Moification de votre pseudo");
        dialog.setContentText("Entrez un nouveau pseudo : ");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()){
            if(result.get().equals("")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Le pseudo ne peut pas être vide");
                alert.showAndWait();
            }
            
            else if (Initialiseur.changerPseudo(result.get())) {
            	//changement pseudo ok
            	username.setText(ChatSystem.self.getPseudo());
            } else {
            	Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Ce pseudo n'est pas disponible");
                alert.showAndWait();
            }
            
        }
    }

    @FXML
    public void userClicked () {
        if (!userListView.getItems().isEmpty() && (userListView.getSelectionModel().getSelectedItem() != null)){
            if(activeUser.getOnline()){
                textArea.setDisable(false);
                fileButton.setDisable(false);
                sendButton.setDisable(false);
                fileButton.setOpacity(1);
                sendButton.setOpacity(1);
            } else {
                textArea.setDisable(true);
                fileButton.setDisable(true);
                sendButton.setDisable(true);
                fileButton.setOpacity(0);
                sendButton.setOpacity(0);
            }
            distantUser.setOpacity(1);
            textArea.setOpacity(1);
            closeDiscussionButton.setOpacity(1);
            distantUser.setText(userListView.getSelectionModel().getSelectedItem().toString());
            updateFeed();
        }
    }

    @FXML
    public void closeDiscussion () {
        hideDistant();
        messageFeed.getItems().clear();
        textArea.setDisable(true);

    }    

    @FXML
    public void sendByClick () {
        send();
    }

    @FXML
    public void sendByEnter (KeyEvent event){
        if (event.getCode().equals(KeyCode.ENTER)) {
            event.consume();
            send();
        }
    }

    private void send(){
        ChatSystem.getConv(activeUser).envoyerMessage(textArea.getText());
        textArea.clear();
        updateFeed();
    }

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if(e.getPropertyName().equals(HistoriqueDAO.Actions.UpdateFeed.name())){
			this.updateFeed();
		}else if(e.getPropertyName().equals(HistoriqueDAO.Actions.UpdateUsers.name())) {
			this.updateView();
		}
	}
	
    //ENVOI D'UN FICHIER

    @FXML
    public void sendFile(MouseEvent event){
    	/*
        System.out.println("hi");
        FileChooser choice = new FileChooser();
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = choice.showOpenDialog(app_stage);
        if(selectedFile != null){
            chat.models.File toSend = new chat.models.File(controller.getSelf(),
                    activeUser, null, selectedFile.getName());
            byte [] byte_file  = new byte [(int)selectedFile.length()];

            FileInputStream fis;
            try {
                fis = new FileInputStream(selectedFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                if(bis.read(byte_file,0,byte_file.length) == 1){
                    System.out.println("Couldn't write into byte_file");
                }
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            toSend.setContent(byte_file);
            controller.sendPacket(toSend);
            System.out.println("File sent");
        }
	*/
    }
    
}
