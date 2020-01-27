package src.model;

import java.io.Serializable;
import java.net.InetAddress;


public class Utilisateur implements Serializable {

	private static final long serialVersionUID = 1L;

	private String pseudo;

	private InetAddress addrIP;

	private byte[] addrMAC;

	private String status;
	
	private Boolean online;

	public Utilisateur() {
		super();
	}

	public Utilisateur(String pseudo, InetAddress addrIP, byte[] addrMAC, String status, Boolean online) {
		super();
		this.pseudo = pseudo;
		this.addrIP = addrIP;
		this.addrMAC = new byte[6];
		this.addrMAC[0] = addrMAC[0];
		this.addrMAC[1] = addrMAC[1];
		this.addrMAC[2] = addrMAC[2];
		this.addrMAC[3] = addrMAC[3];
		this.addrMAC[4] = addrMAC[4];
		this.addrMAC[5] = addrMAC[5];
		this.status = status;
		this.online = online;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public InetAddress getAddrIP() {
		return addrIP;
	}

	public void setAddrIP(InetAddress addrIP) {
		this.addrIP = addrIP;
	}

	public byte[] getAddrMAC() {
		return addrMAC;
	}

	public void setAddrMAC(byte[] mac) {
		this.addrMAC[0] = mac[0];
		this.addrMAC[1] = mac[1];
		this.addrMAC[2] = mac[2];
		this.addrMAC[3] = mac[3];
		this.addrMAC[4] = mac[4];
		this.addrMAC[5] = mac[5];
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getOnline() {
		return online;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}

	
	public String toExtendedString() {
		return "Utilisateur [pseudo=" + pseudo + ", addrIP=" + addrIP.toString() + ", addrMAC=" + String.format("%2x", addrMAC[0])
				+ ":" + String.format("%2x", addrMAC[1]) + ":" + String.format("%2x", addrMAC[2]) + ":"
				+ String.format("%2x", addrMAC[3]) + ":" + String.format("%2x", addrMAC[4]) + ":"
				+ String.format("%2x", addrMAC[5]) + ", status=" + status +", online= "+ this.online + "]";
		
	}
	
	@Override
	public String toString() {
		if (this.online){
			return this.pseudo;
		}else {
			return this.pseudo + " - hors ligne";
		}
	}

}
