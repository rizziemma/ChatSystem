package src.model;

import java.io.Serializable;

public class DatagramUDP implements Serializable{
	private static final long serialVersionUID = 1L;
	public String type;
	public Utilisateur payload;
	public DatagramUDP(String s,Utilisateur u) {
		type = s;
		payload=u;
	}
}
