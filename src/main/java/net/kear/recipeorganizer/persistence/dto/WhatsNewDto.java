package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class WhatsNewDto implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private Date releaseDate;
	private List<String> descriptions;
	
	public WhatsNewDto() {}

	public WhatsNewDto(Date releaseDate, List<String> descriptions) {
		super();
		this.releaseDate = releaseDate;
		this.descriptions = descriptions;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public List<String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((descriptions == null) ? 0 : descriptions.hashCode());
		result = prime * result + ((releaseDate == null) ? 0 : releaseDate.hashCode());
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
		WhatsNewDto other = (WhatsNewDto) obj;
		if (descriptions == null) {
			if (other.descriptions != null)
				return false;
		} else if (!descriptions.equals(other.descriptions))
			return false;
		if (releaseDate == null) {
			if (other.releaseDate != null)
				return false;
		} else if (!releaseDate.equals(other.releaseDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WhatsNewDto [releaseDate=" + releaseDate 
				+ ", descriptions=" + descriptions + "]";
	}
}
