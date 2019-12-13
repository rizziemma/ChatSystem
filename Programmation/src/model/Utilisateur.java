package src.model;

import java.io.Serializable;
import java.util.Date;


public class Utilisateur implements Serializable {

	private static final long serialVersionUID = 1L;

	private String pseudo;

	private String addrIP;

	private byte[] addrMAC;

	private String status;

	private Date derniereConnexion;

	public Utilisateur() {
		super();
	}

	public Utilisateur(String pseudo, String addrIP, byte[] addrMAC, String status, Date derniereConnexion) {
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

	public byte[] getAddrMAC() {
		return addrMAC;
	}

	public void setAddrMAC(byte[] mac) {
		this.addrMAC = mac;
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

	@Override
	public String toString() {
		return "Utilisateur [pseudo=" + pseudo + ", addrIP=" + addrIP + ", addrMAC=" + String.format("%2x", addrMAC[0])
				+ ":" + String.format("%2x", addrMAC[1]) + ":" + String.format("%2x", addrMAC[2]) + ":"
				+ String.format("%2x", addrMAC[3]) + ":" + String.format("%2x", addrMAC[4]) + ":"
				+ String.format("%2x", addrMAC[5]) + ", status=" + status + ", derniereConnexion=" + derniereConnexion
				+ "]";
	}

}