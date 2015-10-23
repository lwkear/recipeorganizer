package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
@Table(name = "USERS")
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", nullable = false, unique = true, length = 11)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USERS_SEQ")
	@SequenceGenerator(name = "USERS_SEQ", sequenceName = "USERS_SEQ", allocationSize = 1)
	private long id;

	@Column(name = "FIRSTNAME")
	private String firstName;

	@Column(name = "LASTNAME")
	private String lastName;

	@Column(name = "EMAIL", nullable = false)
	private String email;

	@Column(name = "PASSWORD", nullable = false)
	private String password;
	
	@Column(name = "ENABLED")
	private int enabled;

	@Column(name = "TOKEN_EXPIRED")
	private int tokenExpired;

	@Column(name = "LOCKED")
	private int locked;
	
	@Column(name = "EXPIRED")
	private int expired;

	@ManyToOne(fetch=FetchType.EAGER)
    @JoinTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID") , 
    	inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID") )
    private Role role;

    @OneToOne(mappedBy = "user", orphanRemoval=true, optional = true, fetch = FetchType.LAZY)
    private UserProfile userProfile;
	
	public User() {}
	
	public User(User user) {
		this.id = user.id;
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.email = user.email;
		this.password = user.password;
		this.enabled = user.enabled;
		this.tokenExpired = user.tokenExpired;
		this.role = user.role;
	}
	
	public User(String firstName, String lastName, String email,
			String password, int enabled, int tokenExpired, Role role) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.enabled = enabled;
		this.tokenExpired = tokenExpired;
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
	
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return (enabled == 1 ? true : false);
	}

	public int getTokenExpired() {
		return tokenExpired;
	}

	public void setTokenExpired(int tokenExpired) {
		this.tokenExpired = tokenExpired;
	}
	
	public boolean isTokenExpired() {
		return (tokenExpired == 1 ? true : false);
	}
	
	public int getLocked() {
		return locked;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}
	
	public boolean isAccountNonLocked() {
		return (locked == 1 ? false : true);
	}

	public int getExpired() {
		return expired;
	}

	public void setExpired(int expired) {
		this.expired = expired;
	}
	
	public boolean isAccountNonExpired() {
		return (expired == 1 ? false : true);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }
    
    public UserProfile getUserProfile() {
    	return userProfile;
    }
    
    public void setUserProfile(UserProfile userProfile) {
    	this.userProfile = userProfile;
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
        final User user = (User) obj;
        if (!email.equals(user.email)) {
            return false;
        }
        return true;
    }	
	
	@Override
	public String toString() {
		return "UserDto [id=" + id + ", firstName=" + firstName + ", lastName="
				+ lastName + ", email=" + email + ", password=" + password
				+ ", enabled=" + enabled + ", " + ", tokenExpired=" + tokenExpired + "]";
	}
}