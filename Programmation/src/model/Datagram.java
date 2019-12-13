package src.model;

import java.util.Date;

public class Datagram {
	public static enum status_type {NEW,RECEIVED,SENT,ARCHIVED,READ};
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
}
