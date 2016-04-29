package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

import net.kear.recipeorganizer.enums.ApprovalStatus;
import net.kear.recipeorganizer.enums.SourceType;

public class SearchResultsDto extends Object implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private int rank;
	private long id;
	private long userId;
	private String name;
	private String description;
	private String photo;
	private boolean allowShare;
	private ApprovalStatus status;
	private long catId;
	private String source;
	private SourceType sourceType;
	
	public SearchResultsDto() {};
	
	public SearchResultsDto(int rank, Long id, Long userId, String name, String description, String photo, boolean allowShare, 
			ApprovalStatus status, long catId, String source, SourceType sourceType) {
		this.rank = rank;
		this.id = id;
		this.userId = userId;
		this.name = name;
		this.description = description;
		this.photo = photo;
		this.allowShare = allowShare;
		this.status = status;
		this.catId = catId;
		this.source = source;
		this.sourceType = sourceType;
	};	

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

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
	
	public ApprovalStatus getStatus() {
		return status;
	}

	public void setStatus(ApprovalStatus status) {
		this.status = status;
	}

	public long getCatId() {
		return catId;
	}
	
	public void setCatId(Long catId) {
		this.catId = catId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}	

	public SourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
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
		SearchResultsDto other = (SearchResultsDto) obj;
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
		return "SearchResultsDto [rank=" + rank
				+ ", id=" + id 
				+ ", userId=" + userId 
				+ ", name=" + name 
				+ ", description=" + description 
				+ ", photo=" + photo 
				+ ", allowShare=" + allowShare 
				+ ", status=" + status 
				+ ", catId=" + catId 
				+ ", source=" + source
				+ ", sourceType=" + sourceType + "]";
	}
}
