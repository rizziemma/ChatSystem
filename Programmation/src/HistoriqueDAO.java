package src;

import java.util.List;

import java.sql.*;

//https://www.sqlitetutorial.net/sqlite-java/

public class HistoriqueDAO {
	private String url = "jdbc:sqlite:data/storage.db";
	public HistoriqueDAO() {
        
	}

	public void updateHistorique(Historique h) {

	}

	public List<Historique> getHistoriques() {
		return null;

	}

	public Historique getHistoriqueByUtilisateur(Utilisateur u) {
		try {          
            // create a connection to the database
            Connection conn = DriverManager.getConnection(url);
            
            //System.out.println("Connection to SQLite has been established.");
            //transaction
            
            
            //fermeture
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return null;
		

	}
}