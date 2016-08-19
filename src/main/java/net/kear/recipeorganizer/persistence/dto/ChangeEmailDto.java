package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

import javax.validation.GroupSequence;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.validation.EmailMatch;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@EmailMatch(groups=ChangeEmailDto.ValidMatch.class)
public class ChangeEmailDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//Hibernate validation groups
	public interface ValidSize {}
	public interface ValidMatch {}
	
	//Hibernate validation sequence
	@GroupSequence({Default.class,ValidSize.class,ValidMatch.class})
	public interface ChangeEmailDtoSequence {}
	
	@NotBlank
	@Email
	@Size(max=50, groups=ValidSize.class)	//50
	private String email;

	@NotBlank
	@Email
	@Size(max=50, groups=ValidSize.class)	//50
	private String confirmEmail;

	private String currentEmail;
	private long userId;
	
	public ChangeEmailDto() {}
	
	public ChangeEmailDto(User user) {
		this.currentEmail = user.getEmail();
		this.userId = user.getId();
	}
	
	public ChangeEmailDto(ChangeEmailDto user) {
		this.email = user.email;
	}
	
	public ChangeEmailDto(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getConfirmEmail() {
		return confirmEmail;
	}

	public void setConfirmEmail(String confirmEmail) {
		this.confirmEmail = confirmEmail;
	}

	public String getCurrentEmail() {
		return currentEmail;
	}

	public void setCurrentEmail(String currentEmail) {
		this.currentEmail = currentEmail;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Override
    public int hashCode() {
        return email.hashCode();
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
        final ChangeEmailDto user = (ChangeEmailDto) obj;
        if (!email.equals(user.email)) {
            return false;
        }
        return true;
    }

	@Override
	public String toString() {
		return "ChangeEmailDto [userId=" + userId + 
				", currentEmail=" + currentEmail +
				", email=" + email +
				", confirmEmail=" + confirmEmail + "]";
	}		
}