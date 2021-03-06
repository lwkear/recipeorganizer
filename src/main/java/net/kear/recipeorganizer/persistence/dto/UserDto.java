package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

import javax.validation.GroupSequence;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import net.kear.recipeorganizer.validation.EmailMatch;
import net.kear.recipeorganizer.validation.PasswordMatch;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@PasswordMatch(groups=UserDto.ValidMatch.class)
@EmailMatch(groups=UserDto.ValidMatch.class)
public class UserDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//Hibernate validation groups
	public interface ValidSize {}
	public interface ValidMatch {}
	
	//Hibernate validation sequence
	@GroupSequence({Default.class,ValidSize.class,ValidMatch.class})
	public interface UserDtoSequence {}
	
	@NotBlank
	@Size(max=50, groups=ValidSize.class)	//50
	private String firstName;

	@NotBlank
	@Size(min=1,max=50, groups=ValidSize.class)	//50
	private String lastName;

	@NotBlank
	@Email
	@Size(max=50, groups=ValidSize.class)	//50
	private String email;

	@NotBlank
	@Email
	@Size(max=50, groups=ValidSize.class)	//50
	private String confirmEmail;

	@NotBlank
	@Size(min=6,max=20, groups=ValidSize.class)		//6,20
	private String password;
	
	@NotBlank
	@Size(min=6,max=20, groups=ValidSize.class)		//6,20
	private String confirmPassword;
	
	private boolean submitRecipes;
	private boolean emailAdmin;
	private boolean emailRecipe;
	private boolean emailMessage;
	private boolean invited = false;
	
	public UserDto() {}
	
	public UserDto(UserDto user) {
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.email = user.email;
		this.password = user.password;
		this.submitRecipes = user.submitRecipes;
		this.emailAdmin = user.emailAdmin;
		this.emailRecipe = user.emailRecipe;
		this.emailMessage = user.emailMessage;
		this.invited = user.invited;
	}
	
	public UserDto(String firstName, String lastName, String email, String password, boolean submitRecipes, boolean emailAdmin, boolean emailRecipe, 
			boolean emailMessage, boolean invited) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.submitRecipes = submitRecipes;
		this.emailAdmin = emailAdmin;
		this.emailRecipe = emailRecipe;
		this.emailMessage = emailMessage;
		this.invited = invited;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public boolean isSubmitRecipes() {
		return submitRecipes;
	}

	public void setSubmitRecipes(boolean submitRecipes) {
		this.submitRecipes = submitRecipes;
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

	public boolean isInvited() {
		return invited;
	}

	public void setInvited(boolean invited) {
		this.invited = invited;
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
        final UserDto user = (UserDto) obj;
        if (!email.equals(user.email)) {
            return false;
        }
        return true;
    }	
	
	@Override
	public String toString() {
		return "UserDto [firstName=" + firstName 
				+ ", lastName=" + lastName 
				+ ", email=" + email 
				+ ", password=" + password 
				+ ", submitRecipes=" + submitRecipes 
				+ ", emailAdmin=" + emailAdmin 
				+ ", emailRecipe=" + emailRecipe
				+ ", emailMessage=" + emailMessage
				+ ", invited=" + invited
				+ "]";
	}
}