package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;
//import java.util.Collection;



import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.kear.recipeorganizer.validation.PasswordMatch;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "USERS")
@PasswordMatch
public class Users implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USERS_SEQ")
	@SequenceGenerator(name = "USERS_SEQ", sequenceName = "USERS_SEQ", allocationSize = 1)
	private long id;

	@Column(name = "FIRSTNAME")
	@NotNull
	@NotBlank
	@NotEmpty
	@Size(max=50)	//50
	private String firstName;

	@Column(name = "LASTNAME")
	@Size(max=50)	//50
	private String lastName;

	@Column(name = "EMAIL", nullable = false)
	@NotNull
	@NotBlank
	@NotEmpty
	@Size(max=50)	//50
	@Email
	private String email;

	@Column(name = "PASSWORD", nullable = false)
	@NotNull
	@NotBlank
	@NotEmpty
	@Size(min=6)
	private String password;
	
	@Transient
	@NotNull
	@NotBlank
	@NotEmpty
	@Size(min=6)
	private String confirmPassword;
	
	@Column(name = "ENABLED")
	private int enabled;

	@Column(name = "TOKEN_EXPIRED")
	private int tokenExpired;

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

    //@ManyToMany
    //@JoinTable(name = "USERS_ROLES", joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID") , inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID") )
    //private Collection<Role> roles;
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinTable(name = "USERS_ROLES", joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID") , 
    	inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID") )
    private Role role;
	
	public Users() {}
	
	public Users(Users user) {
		this.id = user.id;
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.email = user.email;
		this.password = user.password;
		this.enabled = user.enabled;
		this.tokenExpired = user.tokenExpired;
		this.city = user.city;
		this.state = user.state;
		this.age = user.age;
		this.interests = user.interests;
		this.role = user.role;
	}
	
	public Users(String firstName, String lastName, String email,
			String password, int enabled, int tokenExpired, String city, String state, int age,
			String interests, Role role) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.enabled = enabled;
		this.tokenExpired = tokenExpired;
		this.city = city;
		this.state = state;
		this.age = age;
		this.interests = interests;
		this.role = role;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public int getEnabled() {
		return enabled;
	}
	
	public boolean isEnabled() {
		return (enabled == 1 ? true : false);
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public int getTokenExpired() {
		return tokenExpired;
	}

	public boolean isTokenExpired() {
		return (tokenExpired == 1 ? true : false);
	}
	
	public void setTokenExpired(int tokenExpired) {
		this.tokenExpired = tokenExpired;
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

	/*public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(final Collection<Role> roles) {
        this.roles = roles;
    }*/
    
    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
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

    @Override
    public int hashCode() {
        return email.hashCode();
    }	

    /*@Override
    public boolean equals(Object user) {
        if (user instanceof Users) {
            return email.equals(((Users) user).email);
        }
        return false;
    }*/    

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
        final Users user = (Users) obj;
        if (!email.equals(user.email)) {
            return false;
        }
        return true;
    }	
	
	@Override
	public String toString() {
		return "Users [id=" + id + ", firstName=" + firstName + ", lastName="
				+ lastName + ", email=" + email + ", password=" + password
				+ ", enabled=" + enabled + ", " + ", tokenExpired=" + tokenExpired + ", "
				+ "city=" + city + ", state=" + state + ", age=" + age 
				+ ", interests=" + interests + "]";
	}

	
}