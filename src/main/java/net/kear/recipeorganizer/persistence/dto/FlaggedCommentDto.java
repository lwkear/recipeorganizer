package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

public class FlaggedCommentDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long id;
	private long userId;
	private long recipeId;
	private String recipeName;
	private String firstName;
	private String lastName;
	private String userComment;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date dateAdded;
	
	public FlaggedCommentDto() {}

	public FlaggedCommentDto(long id, long userId, long recipeId, String recipeName, String firstName, String lastName, String userComment, Date dateAdded) {
		super();
		this.id = id;
		this.userId = userId;
		this.recipeId = recipeId;
		this.recipeName = recipeName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userComment = userComment;
		this.dateAdded = dateAdded;
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

	public long getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(long recipeId) {
		this.recipeId = recipeId;
	}

	public String getRecipeName() {
		return recipeName;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
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

	public String getUserComment() {
		return userComment;
	}

	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (recipeId ^ (recipeId >>> 32));
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
		FlaggedCommentDto other = (FlaggedCommentDto) obj;
		if (id != other.id)
			return false;
		if (recipeId != other.recipeId)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FlaggedCommentDto [id=" + id 
				+ ", userId=" + userId 
				+ ", recipeId=" + recipeId 
				+ ", recipeName=" + recipeName 
				+ ", firstName=" + firstName 
				+ ", lastName=" + lastName
				+ ", userComment=" + userComment 
				+ ", dateAdded=" + dateAdded + "]";
	}
}
