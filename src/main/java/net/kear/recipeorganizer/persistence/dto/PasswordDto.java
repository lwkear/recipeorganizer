package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//import net.kear.recipeorganizer.validation.PasswordMatch;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

//@PasswordMatch
public class PasswordDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@NotNull
	@NotBlank
	@NotEmpty
	@Size(min=6)
	private String newPassword;
	
	@NotNull
	@NotBlank
	@NotEmpty
	@Size(min=6)
	private String confirmPassword;
	
	public PasswordDto() {}
	
	public PasswordDto(PasswordDto password) {
		this.newPassword = password.newPassword;
		this.confirmPassword = password.confirmPassword;
	}
	
	public PasswordDto(String newPassword, String confirmPassword) {
		super();
		this.newPassword = newPassword;
		this.confirmPassword = confirmPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String password) {
		this.newPassword = password;
	}
	
	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String password) {
		this.confirmPassword = password;
	}

    @Override
    public int hashCode() {
        return newPassword.hashCode();
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
	
}