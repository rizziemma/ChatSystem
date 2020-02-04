package src.affichage;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import src.application.ChatSystem;
import src.application.Initialiseur;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {


    @FXML
    private Button test;

    @FXML
    private Button serverButton;

    @FXML
    private TextField userTextField;

    public LoginController(){}

    private void login(Event e) throws IOException{
    	//CHOIX PSEUDO
        if (userTextField.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Entrez un pseudo.");
            alert.showAndWait();

           
        } else if (Initialiseur.changerPseudo(userTextField.getText())){
            Parent chat_parent = FXMLLoader.load(getClass().getResource("/resources/chat.fxml"));
            Scene chat_scene = new Scene(chat_parent);
            chat_scene.getStylesheets().add("/resources/chatstyle.css");
            Stage app_stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            app_stage.setScene(chat_scene);
            app_stage.show();
        } else {
        	Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Ce pseudo n'est pas disponible");
            alert.showAndWait();
        }
        
    }

    @FXML
    public void handleLoginClick(MouseEvent event) throws IOException{
        login(event);
    }

    @FXML
    public void connectViaEnter (KeyEvent event) throws IOException{
        if(event.getCode().equals(KeyCode.ENTER)){
            login(event);
        }
    }

    @FXML
    public void handleHoverConnect(){
        test.setStyle("-fx-background-color: #ffe680;");
    }

    @FXML
    public void handleHoverConnectDone(){
        test.setStyle("-fx-background-color:  #ffd11a;");
    }

    @FXML
    public void servon (){
    	//switch avec ou sans serveur -> pas nécessaire ?
    	
        if (serverButton.getText().equals("Connexion serveur")){
            //eteint
            ChatSystem.setRemote(false);
            serverButton.setText("Connexion locale");
        }else {
            //allumé
        	/*
            if(controller.getUrlServer().equals("")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Vous devez configurer le serveur de présence en entrant" +
                        "son adresse IP dans le fichier ~/Clavardage/.configServer. " +
                        "Redémarrez pour que les changements prennent effet");
                alert.showAndWait();
            } else {*/
            ChatSystem.setRemote(true);
            serverButton.setText("Connexion serveur");
            
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
