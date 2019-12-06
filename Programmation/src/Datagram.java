package src;

import resources.Datatype;

public class Datagram {
	private Datatype type;
	private Object data;
	
	public Datagram(Datatype type, Object data) {
		this.type = type;
		this .data = data;
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
}
