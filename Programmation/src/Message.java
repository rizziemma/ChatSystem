package src;

import java.util.Date;

import javax.persistence.*;


public class Message {
	private Date date;

	private String text;

	private String status;

	private Boolean sent;

	public Message(String text, String status, Boolean sent) {
		super();
		this.date = new Date();
		this.text = text;
		this.status = status;
		this.sent = sent;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getSent() {
		return sent;
	}

	public void setSent(Boolean sent) {
		this.sent = sent;
	}

	@Override
	public String toString() {
		return "Message [date=" + date + ", text=" + text + ", status=" + status + ", sent=" + sent + "]";
	}


}