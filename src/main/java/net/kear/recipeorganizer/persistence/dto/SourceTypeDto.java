package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

import net.kear.recipeorganizer.enums.SourceType;

public class SourceTypeDto implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private SourceType type;
	private String displayName;
	
	public SourceTypeDto() {}

	public SourceTypeDto(SourceType type, String displayName) {
		super();
		this.type = type;
		this.displayName = displayName;
	}

	public SourceType getType() {
		return type;
	}

	public void setType(SourceType type) {
		this.type = type;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		SourceTypeDto other = (SourceTypeDto) obj;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IngredientReviewDto [type=" + type
				+ ", displayName=" + displayName 
				+ "]";
	}
}
