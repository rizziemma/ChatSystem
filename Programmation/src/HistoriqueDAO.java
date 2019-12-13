package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import resources.Properties;
import src.model.Datagram;
import src.model.Historique;

//https://www.sqlitetutorial.net/sqlite-java/

public class HistoriqueDAO {
	Connection conn;
	public HistoriqueDAO() {
        try {
            conn = DriverManager.getConnection(Properties.SQLiteDriver+Properties.BaseLocalePath);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    
	}
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void nouveauDatagramme(Historique h, Datagram d) {
		String sql = "INSERT INTO MESSAGE(DATE,TYPE,DATA,STATUS,SENT,CONTACT) VALUES(?,?,?,?,?,?)";
		 try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, d.getDate().toString());
            pstmt.setInt(2, d.getType().ordinal());
            pstmt.setBytes(3, (byte[])d.getData());
            pstmt.setInt(4, d.getStatus().ordinal());
            if(d.getSent()) {
            	pstmt.setInt(5, 1);
            }else {
            	pstmt.setInt(5, 0);
            }
            pstmt.setString(6, h.getContact().getAddrMAC().toString());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		 d.setStatus("archived");
	}
	
	public void vuConversation(Historique h) {
		String sql = "UPDATE MESSAGE SET (STATUS = ?) WHERE (CONTACT = ? and STATUS = ?)";
		 try {
           PreparedStatement pstmt = conn.prepareStatement(sql);
           pstmt.setInt(1, 4);
           pstmt.setString(2, h.getContact().getAddrMAC().toString());
           pstmt.setInt(3, 3);
           pstmt.executeUpdate();
           
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
	}

	public List<Historique> getHistoriques() {
		return null;

	}

	public ArrayList<Datagram> getDatagrams10(Historique h) { 
	return null;
	}
	
	public ArrayList<Datagram> getDatagramsByDate(Historique h, String date) {      
        String sql = "SELECT (DATE, TYPE, DATA, STATUS, SENT) FROM MESSAGE WHERE";
        Statement stmt;
        ArrayList<Datagram> l = ;
		try {
			stmt = conn.createStatement();
			ResultSet rs    = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	System.out.println(rs.getInt("id") +  "\t" + 
	                               rs.getString("name") + "\t" +
	                               rs.getDouble("capacity"));
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return h;
		

	}
}