package src.model;

public class FichierEnTransit {
	public String nom;
	public byte [] barray;
	
	public FichierEnTransit(byte[] b, String s) {
		this .nom = s;
		this .barray = b;
	}
}
