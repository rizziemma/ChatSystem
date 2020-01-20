package src.model;

public class DatagramUDP {
	public String type;
	public Utilisateur payload;
	public DatagramUDP(String s,Utilisateur u) {
		type = s;
		payload=u;
	}
}
