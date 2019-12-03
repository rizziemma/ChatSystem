package src;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="MESSAGE")
public class Message {

	@Id 
	@GeneratedValue
	@Column(name = "ID")
	private Integer idMessage;

	@Column(name = "DATE")
	private Date date;

	@Column(name = "DATA")
	private String data;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "SENT")
	private Boolean sent;

}