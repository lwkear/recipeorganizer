package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

import javax.validation.constraints.Size;

//import net.kear.recipeorganizer.validation.PasswordMatch;

import org.hibernate.validator.constraints.NotBlank;

//TODO: VALIDATION: @PasswordMatch
public class PasswordDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Size(min=6, max=20)
	private String currentPassword;

	@NotBlank
	@Size(min=6, max=20)
	private String password;
	
	@NotBlank
	@Size(min=6, max=20)
	private String confirmPassword;
	
	public PasswordDto() {}
	
	public PasswordDto(PasswordDto password) {
		this.currentPassword = password.currentPassword;
		this.password = password.password;
		this.confirmPassword = password.confirmPassword;
	}
	
	public PasswordDto(String currentPassword, String password, String confirmPassword) {
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

    @Override
    public int hashCode() {
        return password.hashCode();
    }	

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

	@Override
	public String toString() {
		return "PasswordDto [currentPassword=" + currentPassword + ", password=" + password + ", confirmPassword=" + confirmPassword + "]";
	}
}