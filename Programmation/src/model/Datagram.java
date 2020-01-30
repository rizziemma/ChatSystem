package src.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Datagram implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static enum status_type {NEW,ARCHIVED,READ};
	private Datatype type;
	private Object data;
	private status_type status; 
	private Boolean sent;
	private Date date;
	
	public Datagram(Datatype type, Object data) {
		this.type = type;
		this .data = data;
		setDate(new Date());
		setSent(true);
		setStatus(status_type.NEW);
	}
	
	public Datagram() {
	}

	public Datatype getType() {
		return type;
	}
	public void setType(Datatype type) {
		this.type = type;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	public status_type getStatus() {
		return status;
	}

	public void setStatus(status_type status) {
		this.status = status;
	}

	public Boolean getSent() {
		return sent;
	}

	public void setSent(Boolean sent) {
		this.sent = sent;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm");  
		if (this.type.equals(Datatype.MESSAGE)) {
			return formatter.format(date)+ " : " + (String)this.data;
		}else if(this.type.equals(Datatype.FICHIER)){
			return "FICHIER - " + formatter.format(date) + " : " +((FichierEnTransit)this.data).nom;
		}else {
			return /*this.date.toString()*/ "OTHER";
		}
	}
}
