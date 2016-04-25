package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

public class CategoryDto implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private long id;
	private String name;
	private String displayName;
	
	public CategoryDto() {}

	public CategoryDto(long id, String name, String displayName) {
		super();
		this.id = id;
		this.name = name;
		this.displayName = displayName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		result = prime * result + (int) (id ^ (id >>> 32));
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
		CategoryDto other = (CategoryDto) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IngredientReviewDto [id=" + id
				+ ", name=" + name
				+ ", displayName=" + displayName 
				+ "]";
	}
}
