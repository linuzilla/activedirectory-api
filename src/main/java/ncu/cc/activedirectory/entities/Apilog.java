package ncu.cc.activedirectory.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the apilogs database table.
 * 
 */
@Entity
@Table(name="apilogs")
@NamedQuery(name="Apilog.findAll", query="SELECT a FROM Apilog a")
public class Apilog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer id;

	@Column(nullable=false, length=64)
	private String account;

	@Column(length=1)
	private String evnet;

	@Column(name="perform_by", nullable=false, length=64)
	private String performBy;

	@Column(name="updated_at", nullable=false)
	private Timestamp updatedAt;

	public Apilog() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getEvnet() {
		return this.evnet;
	}

	public void setEvnet(String evnet) {
		this.evnet = evnet;
	}

	public String getPerformBy() {
		return this.performBy;
	}

	public void setPerformBy(String performBy) {
		this.performBy = performBy;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}