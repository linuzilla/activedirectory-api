package ncu.cc.activedirectory.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.sql.Timestamp;


/**
 * The persistent class for the apiusers database table.
 * 
 */
@Entity
@Table(name="apiusers")
@NamedQuery(name="Apiuser.findAll", query="SELECT a FROM Apiuser a")
public class Apiuser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=64)
	private String id;

	@Column(name="allowed_from", nullable=false, length=512)
	private String allowedFrom;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_at")
	private Date createdAt;

	@Column(nullable=false, length=255)
	private String descr;

	@Column(length=255)
	private String password;

	@Column(nullable=false, length=512)
	private String roleset;

	@Column(name="updated_at", nullable=false)
	private Timestamp updatedAt;

	public Apiuser() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAllowedFrom() {
		return this.allowedFrom;
	}

	public void setAllowedFrom(String allowedFrom) {
		this.allowedFrom = allowedFrom;
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getDescr() {
		return this.descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoleset() {
		return this.roleset;
	}

	public void setRoleset(String roleset) {
		this.roleset = roleset;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}