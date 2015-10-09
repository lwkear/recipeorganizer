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
import javax.validation.constraints.Size;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;
	
	public UserProfile() {}
	
	public UserProfile(UserProfile user) {
		this.id = user.id;
		this.city = user.city;
		this.state = user.state;
		this.age = user.age;
		this.interests = user.interests;
	}
	
	public UserProfile(String city, String state, int age, String interests) {
		super();
		this.city = city;
		this.state = state;
		this.age = age;
		this.interests = interests;
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

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
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