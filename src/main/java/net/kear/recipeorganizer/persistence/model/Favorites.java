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
@Table(name = "FAVORITES")
public class Favorites implements Serializable {

	private static final long serialVersionUID = 1L;	

	@EmbeddedId
	private FavoritesKey id;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@Column(name = "DATE_ADDED", insertable=false, updatable=false)
	private Date dateAdded;
	
	public Favorites() {}
	
	public Favorites(FavoritesKey id, Date dateAdded) {
		super();
		this.id = id;
		this.dateAdded = dateAdded;
	}

	public FavoritesKey getId() {
		return id;
	}

	public void setId(FavoritesKey id) {
		this.id = id;
	}
	
	/*public void setId(long userId, long recipeId) {
		this.id.setUserId(userId);
		this.id.setRecipeId(recipeId);
	}*/	
	
	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateAdded == null) ? 0 : dateAdded.hashCode());
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
		Favorites other = (Favorites) obj;
		if (dateAdded == null) {
			if (other.dateAdded != null)
				return false;
		} else if (!dateAdded.equals(other.dateAdded))
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
		return "Favorites [id=" + id + ", dateAdded=" + dateAdded + "]";
	}
}
