package src.application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import src.model.Utilisateur;
import src.resources.Property;

public class UtilisateurDAO {

	
	//create table UTILISATEUR (ID int NOT NULL AUTO_INCREMENT PRIMARY KEY, MAC varchar(20), PSEUDO varchar(32),IP blob);
	//ALTER TABLE UTILISATEUR ADD UNIQUE (MAC)
	
	private static UtilisateurDAO instance;
	Connection conn;
	
	private UtilisateurDAO() {
		try (InputStream input = new FileInputStream(Property.PathToAppFiles+"config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
           conn = DriverManager.getConnection(Property.SQLiteDriver+prop.getProperty("urlServer"),prop.getProperty("login"),prop.getProperty("pw"));
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       } catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conn = null;
		instance = null;
	}
	
	public static UtilisateurDAO getInstance(){
        if(instance == null){
            instance = new UtilisateurDAO();
        }
        return instance;
    }
	
	public void putIn() {
		String sql = "INSERT INTO UTILISATEUR (PSEUDO, MAC, IP) VALUES (?,?,?) ON DUPLICATE KEY UPDATE PSEUDO = VALUES(PSEUDO), IP = VALUES(IP)";
		 try {
			 PreparedStatement pstmt = conn.prepareStatement(sql);
          
			 pstmt.setString(1, ChatSystem.self.getPseudo());
			 pstmt.setString(2, new String(ChatSystem.self.getAddrMAC(), StandardCharsets.UTF_8)); 
			 pstmt.setObject(3, ChatSystem.self.getAddrIP());
			 pstmt.executeUpdate();
         

		 } catch (SQLException e) {
			 System.out.println(e.getMessage());
		 }
	}
	
	public void putOut() {
		String sql = "DELETE FROM UTILISATEUR WHERE MAC=?";
		 try {
			 PreparedStatement pstmt = conn.prepareStatement(sql);
         
			 pstmt.setString(1, new String(ChatSystem.self.getAddrMAC(), StandardCharsets.UTF_8)); 
			 pstmt.executeUpdate();
        

		 } catch (SQLException e) {
			 System.out.println(e.getMessage());
		 }
	}
	
	//String pseudo, InetAddress addrIP, byte[] addrMAC, String status, Boolean online
	
	public ArrayList<Utilisateur> getOnlineUsers(){
		String sql = "SELECT * FROM UTILISATEUR";
		PreparedStatement stmt;
        ArrayList<Utilisateur> l = new ArrayList<Utilisateur>();
        try {
			stmt = conn.prepareStatement(sql);
			ResultSet rs    = stmt.executeQuery();
	        while (rs.next()) {
	        	Utilisateur u = new Utilisateur(rs.getString("PSEUDO"), (InetAddress)(rs.getObject("IP")), rs.getString("MAC").getBytes(StandardCharsets.UTF_8), "", true );
	        	l.add(u);
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return l;
		
	}

}
