package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

public class TopicDto implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private int id;
	private String description;
	
	public TopicDto() {}

	public TopicDto(int id, String description) {
		super();
		this.id = id;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		TopicDto other = (TopicDto) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FaqDto [id=" + id + 
				", description=" + description + "]"; 
	}
}
