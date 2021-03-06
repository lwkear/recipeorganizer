package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

import javax.validation.GroupSequence;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.validation.PasswordNotDuplicate;
import net.kear.recipeorganizer.validation.PasswordMatch;

import org.hibernate.validator.constraints.NotBlank;

@PasswordNotDuplicate(groups=ChangePasswordDto.NotDuplicate.class)
@PasswordMatch(groups=ChangePasswordDto.ValidMatch.class)
public class ChangePasswordDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	//Hibernate validation groups
	public interface ValidSize {}
	public interface ValidMatch {}
	public interface NotDuplicate {}	
	
	//Hibernate validation sequence
	@GroupSequence({Default.class,ValidSize.class,ValidMatch.class,NotDuplicate.class})
	public interface ChangePasswordDtoSequence {}

	@NotBlank
	@Size(min=6, max=20)		//20
	private String currentPassword;

	@NotBlank
	@Size(min=6, max=20)		//20
	private String password;
	
	@NotBlank
	@Size(min=6, max=20)		//20
	private String confirmPassword;

	private long userId;
	
	public ChangePasswordDto() {}
	
	public ChangePasswordDto(User user) {
		this.userId = user.getId();
	}
	
	public ChangePasswordDto(ChangePasswordDto password) {
		this.currentPassword = password.currentPassword;
		this.password = password.password;
		this.confirmPassword = password.confirmPassword;
	}
	
	public ChangePasswordDto(String currentPassword, String password, String confirmPassword) {
		super();
		this.currentPassword = currentPassword;
		this.password = password;
		this.confirmPassword = confirmPassword;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String password) {
		this.currentPassword = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String password) {
		this.confirmPassword = password;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((confirmPassword == null) ? 0 : confirmPassword.hashCode());
		result = prime * result + ((currentPassword == null) ? 0 : currentPassword.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
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
		ChangePasswordDto other = (ChangePasswordDto) obj;
		if (confirmPassword == null) {
			if (other.confirmPassword != null)
				return false;
		} else if (!confirmPassword.equals(other.confirmPassword))
			return false;
		if (currentPassword == null) {
			if (other.currentPassword != null)
				return false;
		} else if (!currentPassword.equals(other.currentPassword))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ChangePasswordDto [currentPassword=" + currentPassword 
				+ ", password=" + password 
				+ ", confirmPassword=" + confirmPassword + "]";
	}
}