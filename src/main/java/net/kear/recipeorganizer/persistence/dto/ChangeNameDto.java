package net.kear.recipeorganizer.persistence.dto;

import java.io.Serializable;

import javax.validation.GroupSequence;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import net.kear.recipeorganizer.persistence.model.User;

import org.hibernate.validator.constraints.NotBlank;

public class ChangeNameDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//Hibernate validation groups
	public interface ValidSize {}
	
	//Hibernate validation sequence
	@GroupSequence({Default.class,ValidSize.class})
	public interface ChangeNameDtoSequence {}
	
	@NotBlank
	@Size(max=50, groups=ValidSize.class)	//50
	private String firstName;

	@NotBlank
	@Size(max=50, groups=ValidSize.class)	//50
	private String lastName;

	private String currentFirstName;
	private String currentLastName;
	private long userId;
	
	public ChangeNameDto() {}
	
	public ChangeNameDto(ChangeNameDto user) {
		this.firstName = user.firstName;
		this.lastName = user.lastName;
	}

	public ChangeNameDto(User user) {
		this.currentFirstName = user.getFirstName();
		this.currentLastName = user.getLastName();
		this.userId = user.getId();
	}
	
	public ChangeNameDto(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
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

	public String getCurrentFirstName() {
		return currentFirstName;
	}

	public void setCurrentFirstName(String currentFirstName) {
		this.currentFirstName = currentFirstName;
	}

	public String getCurrentLastName() {
		return currentLastName;
	}

	public void setCurrentLastName(String currentLastName) {
		this.currentLastName = currentLastName;
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
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
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
		ChangeNameDto other = (ChangeNameDto) obj;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ChangeNameDto [userId=" + userId +
				", firstName=" + firstName + 
				", lastName=" + lastName +
				", currentFirstName=" + currentFirstName +
				", currentLastName=" + currentLastName + "]";
	}
}