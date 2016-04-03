package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

public class ShareRecipeDto implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private long userId;
	private long recipeId;
	private long recipientId;
	private String recipientName;
	private String recipientEmail;
	private String emailMsg;
	private String recipeName;
	
	public ShareRecipeDto()  {}
	
	public ShareRecipeDto(long userId, long recipeId, long recipientId, String recipientName, String recipientEmail, String emailMsg, String recipeName) {
		super();
		this.userId = userId;
		this.recipeId = recipeId;
		this.recipientId = recipientId;
		this.recipientName = recipientName;
		this.recipientEmail = recipientEmail;
		this.emailMsg = emailMsg;
		this.recipeName = recipeName;
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

	public long getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(long recipientId) {
		this.recipientId = recipientId;
	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public String getRecipientEmail() {
		return recipientEmail;
	}

	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	public String getEmailMsg() {
		return emailMsg;
	}

	public void setEmailMsg(String emailMsg) {
		this.emailMsg = emailMsg;
	}

	public String getRecipeName() {
		return recipeName;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (recipeId ^ (recipeId >>> 32));
		result = prime * result + ((recipientEmail == null) ? 0 : recipientEmail.hashCode());
		result = prime * result + ((recipientName == null) ? 0 : recipientName.hashCode());
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
		ShareRecipeDto other = (ShareRecipeDto) obj;
		if (recipeId != other.recipeId)
			return false;
		if (recipientEmail == null) {
			if (other.recipientEmail != null)
				return false;
		} else if (!recipientEmail.equals(other.recipientEmail))
			return false;
		if (recipientName == null) {
			if (other.recipientName != null)
				return false;
		} else if (!recipientName.equals(other.recipientName))
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ShareRecipeDto [userId=" + userId 
				+ ", recipeId=" + recipeId 
				+ ", recipientId=" + recipientId 
				+ ", recipientName=" + recipientName 
				+ ", recipientEmail=" + recipientEmail
				+ ", emailMsg=" + emailMsg 
				+ ", recipeName=" + recipeName + "]";
	}	
}
