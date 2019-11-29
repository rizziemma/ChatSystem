package src;
import java.util.Date;

public class Utilisateur {

	private String pseudo;
	private String addrIP;
	private String addrMAC;
	private String status;
	private Date derniereConnexion;
	
	
	public Utilisateur() {
		super();
	}
	
	public Utilisateur(String pseudo, String addrIP, String addrMAC, String status,
			Date derniereConnexion) {
		super();
		this.pseudo = pseudo;
		this.addrIP = addrIP;
		this.addrMAC = addrMAC;
		this.status = status;
		this.derniereConnexion = derniereConnexion;
	}
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public String getAddrIP() {
		return addrIP;
	}
	public void setAddrIP(String addrIP) {
		this.addrIP = addrIP;
	}
	public String getAddrMAC() {
		return addrMAC;
	}
	public void setAddrMAC(String addrMAC) {
		this.addrMAC = addrMAC;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getDerniereConnexion() {
		return derniereConnexion;
	}
	public void setDerniereConnexion(Date derniereConnexion) {
		this.derniereConnexion = derniereConnexion;
	}


	

}