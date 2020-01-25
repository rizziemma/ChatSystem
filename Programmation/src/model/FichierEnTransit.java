package src.model;

import java.io.Serializable;

public class FichierEnTransit implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String nom;
	public byte [] barray;
	
	public FichierEnTransit(byte[] b, String s) {
		this .nom = s;
		this .barray = b;
	}
}
