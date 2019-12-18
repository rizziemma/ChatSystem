package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import resources.Properties;
import src.model.Datagram;
import src.model.Datatype;
import src.model.Historique;

//https://www.sqlitetutorial.net/sqlite-java/

public class HistoriqueDAO {
	private static HistoriqueDAO instance;
	Connection conn;
	
	private HistoriqueDAO() {
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
		String sql = "INSERT INTO MESSAGE(DATE,TYPE,DATA,STATUS,SENT,CONTACT) VALUES(?,?,?,?,?,?)";
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
            pstmt.setString(6, h.getContact().getAddrMAC().toString());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		 d.setStatus(Datagram.status_type.ARCHIVED);
	}
	
	public void vuConversation(Historique h, boolean vuSent) {
		String sql = "UPDATE MESSAGE SET STATUS = ? WHERE CONTACT = ? and STATUS = ? and SENT = ?";
		 try {
           PreparedStatement pstmt = conn.prepareStatement(sql);
           pstmt.setInt(1, Datagram.status_type.READ.ordinal());
           pstmt.setString(2, h.getContact().getAddrMAC().toString());
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

	public List<Historique> getHistoriques() {
		return null;

	}

	public ArrayList<Datagram> getDatagrams10(Historique h) { 
		String sql = "SELECT * FROM MESSAGE WHERE (CONTACT = ?) ORDER BY datetime(DATE) DESC LIMIT 10";
		PreparedStatement stmt;
        ArrayList<Datagram> l = new ArrayList<Datagram>();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");         
        try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, h.getContact().getAddrMAC().toString());
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
	
	public ArrayList<Datagram> getDatagramsByDate(Historique h, Date date) {      
        String sql = "SELECT * FROM MESSAGE WHERE (CONTACT = ? and Datetime(DATE)<=?) ORDER BY datetime(DATE) DESC LIMIT 10";
        PreparedStatement stmt;
        ArrayList<Datagram> l = new ArrayList<Datagram>();		
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");     
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");    
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, h.getContact().getAddrMAC().toString());
			stmt.setString(2, dateFormat.format(date));
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
}