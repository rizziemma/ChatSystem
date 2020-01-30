package src.application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.Observable;
import java.util.Properties;

import src.model.Datagram;
import src.model.Datatype;
import src.model.FichierEnTransit;
import src.model.Utilisateur;
import src.resources.Property;

//https://www.sqlitetutorial.net/sqlite-java/

public class HistoriqueDAO extends Observable{

	
	public class UpdateFeed{
		private  Utilisateur utilisateur;
		private  Datagram message;
		public UpdateFeed(Utilisateur u, Datagram m) {
			this.utilisateur = u;
			this.message = m;
		}
		public Utilisateur getUtilisateur() {
			return this.utilisateur;
		}
		public Datagram getMessage() {
			return this.message;
		}
	}
	
	public class UpdateUsers{
		public UpdateUsers() {
		}
		
	}
		
	private static HistoriqueDAO instance;
	Connection conn;
	
	private HistoriqueDAO() {
        try (InputStream input = new FileInputStream(Property.PathToAppFiles+"config.properties")) {
             Properties prop = new Properties();
             prop.load(input);
            conn = DriverManager.getConnection(Property.SQLiteDriver+prop.getProperty("pathToLocalBase"));
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
	
	public static HistoriqueDAO getInstance(){
        if(instance == null){
            instance = new HistoriqueDAO();
        }
        return instance;
    }
	
	
	
	public void nouveauDatagramme(Utilisateur u, Datagram d) {
		String sql = "INSERT INTO MESSAGE(DATE,TYPE,DATA,STATUS,SENT,CONTACT) VALUES(?,?,?,?,?,?);";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss"); 
		try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dateFormat.format(d.getDate()));
            pstmt.setInt(2, d.getType().ordinal());
            if(d.getType().equals(Datatype.FICHIER)) {
            	pstmt.setObject(3, ((FichierEnTransit)d.getData()).nom);
            }else {
            	pstmt.setObject(3, d.getData());
			}
            pstmt.setInt(4, Datagram.status_type.ARCHIVED.ordinal());
            if(d.getSent()) {
            	pstmt.setInt(5, 1);
            }else {
            	pstmt.setInt(5, 0);
            }
            pstmt.setObject(6, u.getAddrMAC());       
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		d.setStatus(Datagram.status_type.ARCHIVED);
		this.setChanged();
		this.notifyObservers(new UpdateFeed(u, d));
	}
	
	/*
	public void vuConversation(Utilisateur u, boolean vuSent) {
		String sql = "UPDATE MESSAGE SET STATUS = ? WHERE (CONTACT = ? AND STATUS = ? AND SENT = ?)";
		 try {
           PreparedStatement pstmt = conn.prepareStatement(sql);
           pstmt.setInt(1, Datagram.status_type.READ.ordinal());
           pstmt.setString(2, new String(u.getAddrMAC(), StandardCharsets.UTF_8));
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
*/
	public void updateUser(Utilisateur u) {
		String sql = "REPLACE INTO UTILISATEUR (MAC,PSEUDO,ONLINE) VALUES (?,?,?)";
		 try {
          PreparedStatement pstmt = conn.prepareStatement(sql);
          
          pstmt.setObject(1, u.getAddrMAC()); 
          pstmt.setString(2, u.getPseudo());
          if(u.getOnline()) {
        	  pstmt.setInt(3, 1);
          }else {
        	  pstmt.setInt(3, 0);
          }
          
            
          pstmt.executeUpdate();
          

		 } catch (SQLException e) {
			 System.out.println(e.getMessage());
		 }
			this.setChanged();
			this.notifyObservers(new UpdateUsers());

	}
	
	public ArrayList<Datagram> getDatagrams(Utilisateur u) { 
		String sql = "SELECT * FROM MESSAGE WHERE (CONTACT = ? AND (TYPE = ? OR TYPE = ?)) ORDER BY datetime(DATE) DESC";
		PreparedStatement stmt;
        ArrayList<Datagram> l = new ArrayList<Datagram>();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");         
        try {
			stmt = conn.prepareStatement(sql);
			stmt.setObject(1, u.getAddrMAC());
			stmt.setInt(2, Datatype.FICHIER.ordinal());
			stmt.setInt(3, Datatype.MESSAGE.ordinal());
			ResultSet rs    = stmt.executeQuery();
	        while (rs.next()) {
	        	Datagram d = new Datagram();
	        	d.setDate(formatter.parse(rs.getString("DATE")));
	        	
	        	d.setSent(rs.getInt("SENT")==1);
	        	d.setStatus(Datagram.status_type.values()[rs.getInt("STATUS")]);
	        	d.setType(Datatype.values()[rs.getInt("TYPE")]);
	        	if(d.getType().equals(Datatype.FICHIER)) {
	            	d.setData(new FichierEnTransit(new byte[0], (String)rs.getObject("DATA")));
	            }else {
	            	d.setData(rs.getObject("DATA"));
				}
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
	
	
	public ArrayList<Utilisateur> getOffline(){
		String sql = "SELECT * FROM UTILISATEUR WHERE (ONLINE=0)";
		PreparedStatement stmt;
        ArrayList<Utilisateur> l = new ArrayList<Utilisateur>();
        try {
			stmt = conn.prepareStatement(sql);
			ResultSet rs    = stmt.executeQuery();
	        while (rs.next()) {
	        	Utilisateur u = new Utilisateur();
	        	u.setAddrMAC((byte[]) rs.getObject("MAC"));
	        	u.setPseudo(rs.getString("PSEUDO"));
	        	u.setOnline(rs.getInt("ONLINE")==1);
	        	l.add(u);
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
        return l;
	
	}

	
	
}
