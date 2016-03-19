package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

public class IngredientReviewDto implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private long id;
	private String name;
	private long usage;
	
	public IngredientReviewDto() {}

	public IngredientReviewDto(long id, String name, long usage) {
		super();
		this.id = id;
		this.name = name;
		this.usage = usage;
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

	public long getUsage() {
		return usage;
	}

	public void setUsage(long usage) {
		this.usage = usage;
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
		IngredientReviewDto other = (IngredientReviewDto) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IngredientReviewDto [id=" + id + ", "
				+ "name=" + name 
				+ ", usage=" + usage + "]";
	}
}
