package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "USER_PROFILE")
public class UserProfile implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_PROFILE_SEQ")
	@SequenceGenerator(name = "USER_PROFILE_SEQ", sequenceName = "USER_PROFILE_SEQ", allocationSize = 1)
	private long id;

	@Column(name = "CITY")
	@Size(max=75)	//75
	private String city;

	@Column(name = "STATE")
	@Size(max=30)	//30
	private String state;

	@Column(name = "AGE")
	private int age;

	@Column(name = "INTERESTS")
	@Size(max=500)	//500
	private String interests;

	@Column(name = "AVATAR")
	private String avatar;
	
	@Transient
	private boolean submitRecipes;

	//NOTE: the @JsonIgnore annotation prevents a recursive stack overflow when serializing a User object (see "public User getUser" function in AdminController)
	//	the serializer parses through the <user> object first; when it gets to the <userProfile> object in User, it tries to serialize not just the UserProfile data
	//	members but also its <user> element, which includes, of course, a <userProfile> element and hence the recursive loop
	@OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    @JsonIgnore
    private User user;
	
	public UserProfile() {}
	
	public UserProfile(UserProfile user) {
		this.id = user.id;
		this.city = user.city;
		this.state = user.state;
		this.age = user.age;
		this.interests = user.interests;
		this.avatar = user.avatar;
	}
	
	public UserProfile(String city, String state, int age, String interests, String avatar) {
		super();
		this.city = city;
		this.state = state;
		this.age = age;
		this.interests = interests;
		this.avatar = avatar;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public boolean getSubmitRecipes() {
		return submitRecipes;
	}

	public void setSubmitRecipes(boolean submitRecipes) {
		this.submitRecipes = submitRecipes;
	}
	
    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((interests == null) ? 0 : interests.hashCode());
		result = prime * result + ((avatar == null) ? 0 : avatar.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		UserProfile other = (UserProfile) obj;
		if (age != other.age)
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (id != other.id)
			return false;
		if (interests == null) {
			if (other.interests != null)
				return false;
		} else if (!interests.equals(other.interests))
			return false;
		if (avatar == null) {
			if (other.avatar != null)
				return false;
		} else if (!avatar.equals(other.avatar))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserProfile [id=" + id 
				+ ", city=" + city 
				+ ", state=" + state 
				+ ", age=" + age 
				+ ", interests=" + interests 
				+ ", avatar=" + avatar + "]";
	}
}