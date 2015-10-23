package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

import javax.validation.GroupSequence;
import javax.validation.constraints.Min;
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
	@Size(min=6,max=20, groups=ValidSize.class)
	private String password;
	
	@NotBlank
	@Size(min=6,max=20, groups=ValidSize.class)
	private String confirmPassword;
	
	public UserDto() {}
	
	public UserDto(UserDto user) {
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.email = user.email;
		this.password = user.password;
	}
	
	public UserDto(String firstName, String lastName, String email,
			String password) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
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
		return "UserDto [firstName=" + firstName + ", lastName="
				+ lastName + ", email=" + email + ", password=" + password + "]";
	}
}