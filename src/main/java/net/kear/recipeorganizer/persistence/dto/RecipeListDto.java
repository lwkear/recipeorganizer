package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;
import java.util.Date;

import net.kear.recipeorganizer.enums.ApprovalStatus;

public class RecipeListDto implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private long id;
	private long userId;
	private String name;
	private String description;
	private Date submitted;
	private String firstName;
	private String lastName;
	private String category;
	private String sourcetype;
	private boolean allowShare;
	private ApprovalStatus status;

	public RecipeListDto() {}
	
	public RecipeListDto(long id, long userId, String name, String description, Date submitted, String firstName, String lastName, String category, String sourcetype,
			boolean allowShare, ApprovalStatus status) {
		this.id = id;
		this.userId = userId;
		this.name = name;
		this.description = description;
		this.submitted = submitted;
		this.firstName = firstName;
		this.lastName = lastName;
		this.category = category;
		this.sourcetype = sourcetype;
		this.allowShare = allowShare;
		this.status = status;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSourcetype() {
		return sourcetype;
	}

	public void setSourcetype(String sourcetype) {
		this.sourcetype = sourcetype;
	}

	public Date getSubmitted() {
		return submitted;
	}

	public void setSubmitted(Date submitted) {
		this.submitted = submitted;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}	

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public boolean getAllowShare() {
		return allowShare;
	}

	public void setAllowShare(boolean allowShare) {
		this.allowShare = allowShare;
	}
	
	public ApprovalStatus getStatus() {
		return status;
	}

	public void setStatus(ApprovalStatus status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		RecipeListDto other = (RecipeListDto) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RecipeListDto [id=" + id 
				+ ", userId=" + userId
				+ ", name=" + name 
				+ ", description=" + description 
				+ ", submitted=" + submitted 
				+ ", firstName=" + firstName 
				+ ", lastName=" + lastName 
				+ ", category=" + category 
				+ ", sourcetype=" + sourcetype 
				+ ", allowShare=" + allowShare 
				+ ", status=" + status + "]";
	}
}
