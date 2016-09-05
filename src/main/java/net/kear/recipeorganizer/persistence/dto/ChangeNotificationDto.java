package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

import net.kear.recipeorganizer.persistence.model.User;

public class ChangeNotificationDto implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	private long userId;
	private boolean emailAdmin;
	private boolean emailRecipe;
	private boolean emailMessage;
	
	public ChangeNotificationDto() {}
	
	public ChangeNotificationDto(User user) {
		this.userId = user.getId();
		this.emailAdmin = user.isEmailAdmin();
		this.emailRecipe = user.isEmailRecipe();
		this.emailMessage = user.isEmailMessage();
	}

	public ChangeNotificationDto(long userId, boolean emailAdmin, boolean emailRecipe, boolean emailMessage) {
		super();
		this.userId = userId;
		this.emailAdmin = emailAdmin;
		this.emailRecipe = emailRecipe;
		this.emailMessage = emailMessage;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public boolean isEmailAdmin() {
		return emailAdmin;
	}

	public void setEmailAdmin(boolean emailAdmin) {
		this.emailAdmin = emailAdmin;
	}

	public boolean isEmailRecipe() {
		return emailRecipe;
	}

	public void setEmailRecipe(boolean emailRecipe) {
		this.emailRecipe = emailRecipe;
	}

	public boolean isEmailMessage() {
		return emailMessage;
	}

	public void setEmailMessage(boolean emailMessage) {
		this.emailMessage = emailMessage;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (emailAdmin ? 1231 : 1237);
		result = prime * result + (emailRecipe ? 1231 : 1237);
		result = prime * result + (emailMessage ? 1231 : 1237);
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
		ChangeNotificationDto other = (ChangeNotificationDto) obj;
		if (emailAdmin != other.emailAdmin)
			return false;
		if (emailRecipe != other.emailRecipe)
			return false;
		if (emailMessage != other.emailMessage)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NotificationDto [userId=" + userId 
				+ ", emailAdmin=" + emailAdmin 
				+ ", emailRecipe=" + emailRecipe
				+ ", emailMessage=" + emailMessage + "]";
	}
}
