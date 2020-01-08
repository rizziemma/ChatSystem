package src.affichage;


import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import src.application.ChatSystem;
import src.application.HistoriqueDAO;
import src.model.Utilisateur;

import java.io.*;
import java.net.URL;
import java.util.*;

public class ChatController implements Initializable {


    private Utilisateur activeUser = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userListView.setStyle("-fx-background-insets: 0 ;");
        userListView.setStyle("-fx-control-inner-background: #242A31;");
        userListView.setFixedCellSize(50);
        username.setText(ChatSystem.self.getPseudo());
        hideDistant();
        updateView();

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
            ArrayList<String> online = new ArrayList<>();
            ArrayList<String> offline = new ArrayList<>();

            for (Utilisateur u : ChatSystem.tableUtilisateur){
                if (!u.equals(ChatSystem.self)) {
                    if(u.getStatus().equals("OFFLINE")){
                        offline.add(u.getPseudo() + " - Hors ligne");
                    } else {
                        online.add(u.getPseudo());
                    }
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
                HistoriqueDAO dao = HistoriqueDAO.getInstance();
                //format string attendu pour affichage
                //messageFeed.getItems().addAll(dao.getDatagrams10(activeUser));

                messageFeed.scrollTo(messageFeed.getItems().size() - 1);
            }
        });
    }

    @FXML
    private Label username;

    @FXML
    private Label distantUser;

    @FXML
    private ListView<String> userListView;

    @FXML
    private Button closeDiscussionButton;

    @FXML
    private Button fileButton;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea textArea;

    @FXML
    private ListView<String> messageFeed;

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
            //TRAITEMENT PSEUDO
            /* 
            else if (!controller.usernameInList(result.get())) {
                username.setText(result.get());
                controller.getSelf().setPseudo(result.get());

                for (User u : controller.getList()){
                    if(u.getAddress() == controller.getSelf().getAddress()){
                        u.setPseudo(result.get());
                    }
                }
                controller.sendPacket(Notifications.createNewPseudoPacket(controller.getSelf(),null));
                controller.setUsername(result.get());
                onChanged((ListChangeListener.Change) null);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Ce pseudo n'est pas disponible");
                alert.showAndWait();
            }*/
        }
    }

    @FXML
    public void userClicked () {
        if (!userListView.getItems().isEmpty() && (userListView.getSelectionModel().getSelectedItem() != null)){
            activeUser = ChatSystem.getUserByPseudo(((String) userListView.getSelectionModel().getSelectedItem())
                    .replace(" - Hors ligne",""));
            if(activeUser.getStatus().equals("ONLINE")){
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
            distantUser.setText((String) userListView.getSelectionModel().getSelectedItem());
            updateFeed();
        }
    }

    @FXML
    public void closeDiscussion () {
        hideDistant();
        messageFeed.getItems().clear();
        textArea.setDisable(true);

    }
 //EVENT A GERER
    /*
    @Override
    public void onChanged(ListChangeListener.Change c) {
        updateView();
    }

    @Override
    public void onChanged(MapChangeListener.Change change) {
        System.out.println("coucou");
        updateFeed();
    }
    */

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
        //Prendre le texte et l'envoyer
        System.out.println(textArea.getText());
        
        //get historique by activeUser + nouveau message (textArea.getText())
        
        textArea.clear();
        updateFeed();
    }
    
    //ENVOI D'UN FICHIER
/*
    @FXML
    public void sendFile(MouseEvent event){
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

    }
    */

}
