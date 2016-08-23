package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

//import javax.persistence.EnumType;
//import javax.persistence.Enumerated;

import java.util.Date;

import net.kear.recipeorganizer.enums.ApprovalStatus;

public class RecipeDisplayDto implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private long id;
	private long userId;
	private String name;
	private String description;
	private String photo;
	private long views;
	private Date submitted;
	private boolean allowShare;
	private ApprovalStatus status;
	
	public RecipeDisplayDto() {};
	
	public RecipeDisplayDto(Long id, Long userId, String name, String description, String photo, Long views, Date submitted, boolean allowShare, 
			ApprovalStatus status) {
		this.id = id;
		this.userId = userId;
		this.name = name;
		this.description = description;
		this.photo = photo;
		this.views = views;
		this.submitted = submitted;
		this.allowShare = allowShare;
		this.status = status;
	};	

	public long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}	

	public long getViews() {
		return views;
	}

	public void setViews(long views) {
		this.views = views;
	}

	public Date getSubmitted() {
		return submitted;
	}

	public void setSubmitted(Date submitted) {
		this.submitted = submitted;
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
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (userId ^ (userId >>> 32));
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
		RecipeDisplayDto other = (RecipeDisplayDto) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RescipeDisplayDto [id=" + id 
				+ ", userId=" + userId 
				+ ", name=" + name 
				+ ", description=" + description 
				+ ", photo=" + photo
				+ ", views=" + views
				+ ", submitted=" + submitted
				+ ", allowShare=" + allowShare 
				+ ", status=" + status + "]";
	}
}
