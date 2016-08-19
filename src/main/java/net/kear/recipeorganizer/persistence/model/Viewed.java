package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "VIEWED")
public class Viewed implements Serializable {

	private static final long serialVersionUID = 1L;	

	@EmbeddedId
	private ViewedKey id = new ViewedKey();
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@Column(name = "DATE_VIEWED", insertable=false, updatable=true)
	private Date dateViewed;
	
	public Viewed() {}
	
	public Viewed(ViewedKey id, Date dateViewed) {
		super();
		this.id = id;
		this.dateViewed = dateViewed;
	}

	public ViewedKey getId() {
		return id;
	}

	public void setId(ViewedKey id) {
		this.id = id;
	}
	
	public Date getDateViewed() {
		return dateViewed;
	}

	public void setDateViewed(Date dateViewed) {
		this.dateViewed = dateViewed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateViewed == null) ? 0 : dateViewed.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Viewed other = (Viewed) obj;
		if (dateViewed == null) {
			if (other.dateViewed != null)
				return false;
		} else if (!dateViewed.equals(other.dateViewed))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Viewed [id=" + id 
				+ ", dateViewed=" + dateViewed + "]";
	}
}
