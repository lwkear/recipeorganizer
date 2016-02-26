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
@Table(name = "RECIPE_MADE")
public class RecipeMade implements Serializable {

	private static final long serialVersionUID = 1L;	

	@EmbeddedId
	private RecipeMadeKey id = new RecipeMadeKey();
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="MM/dd/yyyy")
	@Column(name = "LAST_MADE")
	private Date lastMade;
	
	@Column(name = "MADE_COUNT")
	int madeCount;
	
	public RecipeMade() {}
	
	public RecipeMade(RecipeMadeKey id, Date lastMade, int madeCount) {
		super();
		this.id = id;
		this.lastMade = lastMade;
		this.madeCount = madeCount;
	}

	public RecipeMadeKey getId() {
		return id;
	}

	public void setId(RecipeMadeKey id) {
		this.id = id;
	}
	
	public Date getLastMade() {
		return lastMade;
	}

	public void setLastMade(Date lastMade) {
		this.lastMade = lastMade;
	}

	public int getMadeCount() {
		return madeCount;
	}

	public void setMadeCount(int madeCount) {
		this.madeCount = madeCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastMade == null) ? 0 : lastMade.hashCode());
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
		RecipeMade other = (RecipeMade) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastMade == null) {
			if (other.lastMade != null)
				return false;
		} else if (!lastMade.equals(other.lastMade))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RecipeMade [id=" + id 
				+ ", lastMade=" + lastMade 
				+ ", madeCount=" + madeCount + "]";
	}	
}
