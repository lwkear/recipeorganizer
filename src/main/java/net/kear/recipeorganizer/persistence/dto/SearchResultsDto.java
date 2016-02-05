package net.kear.recipeorganizer.persistence.dto;

public class SearchResultsDto {

	private long id;
	private long userId;
	private String name;
	private String description;
	private String photo;
	private boolean allowShare;
	private boolean approved;
	
	public SearchResultsDto() {};
	
	public SearchResultsDto(Long id, Long userId, String name, String description, String photo, boolean allowShare, boolean approved) {
		this.id = id;
		this.userId = userId;
		this.name = name;
		this.description = description;
		this.photo = photo;
		this.allowShare = allowShare;
		this.approved = approved;
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

	public boolean getAllowShare() {
		return allowShare;
	}

	public void setAllowShare(boolean allowShare) {
		this.allowShare = allowShare;
	}
	
	public boolean getApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}
}
