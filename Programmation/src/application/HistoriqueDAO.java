package src.application;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import src.model.Datagram;
import src.model.Datatype;
import src.model.Historique;
import src.model.Utilisateur;
import src.resources.Properties;

//https://www.sqlitetutorial.net/sqlite-java/

public class HistoriqueDAO{
	
	public enum Actions{
		UpdateFeed,
		UpdateUsers
	}
	
	private PropertyChangeSupport property_support = new PropertyChangeSupport(this);
	
	private static HistoriqueDAO instance;
	Connection conn;
	
	private HistoriqueDAO() {
        try {
            conn = DriverManager.getConnection(Properties.SQLiteDriver+Properties.PathToAppFiles+Properties.BaseLocale);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	
	public void addObserver(PropertyChangeListener listener) {
		this.property_support.addPropertyChangeListener(Actions.UpdateFeed.name(), listener);
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
	
	public static HistoriqueDAO getInstance(){
        if(instance == null){
            instance = new HistoriqueDAO();
        }
        return instance;
    }
	
	
	
	public void nouveauDatagramme(Historique h, Datagram d) {
		String sql = "INSERT INTO MESSAGE(DATE,TYPE,DATA,STATUS,SENT,CONTACT) VALUES(?,?,?,?,?,?);";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss"); 
		try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dateFormat.format(d.getDate()));
            pstmt.setInt(2, d.getType().ordinal());
            pstmt.setObject(3, d.getData());
            pstmt.setInt(4, Datagram.status_type.ARCHIVED.ordinal());
            if(d.getSent()) {
            	pstmt.setInt(5, 1);
            }else {
            	pstmt.setInt(5, 0);
            }
            pstmt.setString(6, new String(h.getContact().getAddrMAC(), StandardCharsets.UTF_8));       
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		d.setStatus(Datagram.status_type.ARCHIVED);
		this.property_support.firePropertyChange(Actions.UpdateFeed.name(), null, null);
	}
	
	
	public void vuConversation(Historique h, boolean vuSent) {
		String sql = "UPDATE MESSAGE SET STATUS = ? WHERE (CONTACT = ? AND STATUS = ? AND SENT = ?)";
		 try {
           PreparedStatement pstmt = conn.prepareStatement(sql);
           pstmt.setInt(1, Datagram.status_type.READ.ordinal());
           pstmt.setString(2, new String(h.getContact().getAddrMAC(), StandardCharsets.UTF_8));
           pstmt.setInt(3, Datagram.status_type.ARCHIVED.ordinal());
           if(vuSent) {
        	   pstmt.setInt(4, 0);
           }else {
        	   pstmt.setInt(4, 1);
           }
           pstmt.executeUpdate();
           
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
	}

	public void updateUser(Utilisateur u) {
		String sql1 = "UPDATE UTILISATEUR SET PSEUDO=? WHERE MAC=?";
		String sql2 = "INSERT INTO UTILISATEUR (PSEUDO, MAC) VALUES(?,?) WHERE NOT EXISTS (SELECT MAC FROM UTILISATEUR WHERE MAC=?) ";
		 try {
          PreparedStatement pstmt = conn.prepareStatement(sql1);
          String mac = new String(u.getAddrMAC(), StandardCharsets.UTF_8);
          pstmt.setString(1, u.getPseudo());
          pstmt.setString(2, mac);   
          
          pstmt.executeUpdate();
          
          pstmt = conn.prepareStatement(sql2);
          pstmt.setString(1, u.getPseudo());
          pstmt.setString(2, mac);
          pstmt.setString(3, mac);
          
          pstmt.executeUpdate();

		 } catch (SQLException e) {
			 System.out.println(e.getMessage());
		 }
		 this.property_support.firePropertyChange(Actions.UpdateUsers.name(), null, null);

	}
	
	public ArrayList<Datagram> getDatagrams(Utilisateur u) { 
		String sql = "SELECT * FROM MESSAGE WHERE (CONTACT = ?) ORDER BY datetime(DATE) DESC";
		PreparedStatement stmt;
        ArrayList<Datagram> l = new ArrayList<Datagram>();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");         
        try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, new String(u.getAddrMAC(), StandardCharsets.UTF_8));
			ResultSet rs    = stmt.executeQuery();
	        while (rs.next()) {
	        	Datagram d = new Datagram();
	        	d.setDate(formatter.parse(rs.getString("DATE")));
	        	d.setData(rs.getObject("DATA"));
	        	d.setSent(rs.getInt("SENT")==1);
	        	d.setStatus(Datagram.status_type.values()[rs.getInt("STATUS")]);
	        	d.setType(Datatype.values()[rs.getInt("STATUS")]);
	        	l.add(d);
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return l;
	}
	
	public ArrayList<Utilisateur> getContacts(){
		String sql = "SELECT * FROM UTILISATEUR";
		PreparedStatement stmt;
        ArrayList<Utilisateur> l = new ArrayList<Utilisateur>();
        try {
			stmt = conn.prepareStatement(sql);
			ResultSet rs    = stmt.executeQuery();
	        while (rs.next()) {
	        	Utilisateur u = new Utilisateur();
	        	u.setAddrMAC(rs.getString("CONTACT").getBytes(StandardCharsets.UTF_8));
	        	u.setPseudo(rs.getString("PSEUDO"));
	        	l.add(u);
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
        return l;
	
	}

	
	
}