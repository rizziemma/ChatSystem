package src;

import java.io.Serializable;

public class Notification implements Serializable {
	private static final long serialVersionUID = 1L;
	private String type;
	private Utilisateur contact;
	private Message message;

}