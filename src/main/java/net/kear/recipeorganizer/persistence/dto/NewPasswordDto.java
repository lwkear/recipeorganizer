package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

import javax.validation.constraints.Size;

//import net.kear.recipeorganizer.validation.PasswordMatch;


import org.hibernate.validator.constraints.NotBlank;

//TODO: VALIDATION: @PasswordMatch
public class NewPasswordDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Size(min=6, max=20)
	private String password;
	
	@NotBlank
	@Size(min=6, max=20)
	private String confirmPassword;

	private long userId;
	
	public NewPasswordDto() {}
	
	public NewPasswordDto(NewPasswordDto password) {
		this.password = password.password;
		this.confirmPassword = password.confirmPassword;
	}
	
	public NewPasswordDto(String password, String confirmPassword) {
		super();
		this.password = password;
		this.confirmPassword = confirmPassword;
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
		NewPasswordDto other = (NewPasswordDto) obj;
		if (confirmPassword == null) {
			if (other.confirmPassword != null)
				return false;
		} else if (!confirmPassword.equals(other.confirmPassword))
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
		return "PasswordDto [password=" + password 
				+ ", confirmPassword=" + confirmPassword + "]";
	}
}