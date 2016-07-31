package net.kear.recipeorganizer.persistence.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.CascadeType;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "USERS")
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final int PASSWORD_EXPIRATION = 60 * 24 * 365;
	
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
	
	@Column(name = "ACCOUNT_EXPIRED")
	private int accountExpired;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@Column(name = "DATE_ADDED", insertable=false, updatable=false)
	private Date dateAdded;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@Column(name = "DATE_UPDATED", insertable=false, updatable=false)
	private Date dateUpdated;
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@Column(name = "LAST_LOGIN")
	private Date lastLogin;

	@Column(name = "PASSWORD_EXPIRED")
	private int passwordExpired;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@Column(name = "PASSWORD_EXPIRY_DATE")
	private Date passwordExpiryDate;
	
	@Column(name = "INVITED")
	private boolean invited;
	
	@Transient
	private boolean loggedIn = false;
	
	@Transient
	private long numRecipes = 0;
	
	@Transient
	private long newMsgCount;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID") , 
    	inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID") )
    private Role role;

    @OneToOne(mappedBy = "user", cascade=CascadeType.ALL, orphanRemoval=true, optional = true, fetch = FetchType.LAZY)
    private UserProfile userProfile;
	
	public User() {}
	
	public User(long id, String firstName, String lastName, String email, String password, int enabled, int tokenExpired, int locked, int accountExpired, Date dateAdded, 
				Date lastLogin, int passwordExpired, Date passwordExpiryDate, boolean invited, boolean loggedIn, long numRecipes, long newMsgCount, Role role, UserProfile userProfile) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.enabled = enabled;
		this.tokenExpired = tokenExpired;
		this.locked = locked;
		this.accountExpired = accountExpired;
		this.dateAdded = dateAdded;
		this.lastLogin = lastLogin;
		this.passwordExpired = passwordExpired;
		this.invited = invited;
		this.loggedIn = loggedIn;
		this.numRecipes = numRecipes;
		this.newMsgCount = newMsgCount;
		this.role = role;
		this.userProfile = userProfile;
		this.passwordExpiryDate = passwordExpiryDate;
	}
	
	public User(User user) {
		this.id = user.id;
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.email = user.email;
		this.password = user.password;
		this.enabled = user.enabled;
		this.tokenExpired = user.tokenExpired;
		this.locked = user.locked;
		this.accountExpired = user.accountExpired;
		this.dateAdded = user.dateAdded;
		this.lastLogin = user.lastLogin;
		this.passwordExpired = user.passwordExpired;
		this.passwordExpiryDate = user.passwordExpiryDate;
		this.invited = user.invited;
		this.loggedIn = user.loggedIn;
		this.numRecipes = user.numRecipes;
		this.newMsgCount = user.newMsgCount;
		this.role = user.role;
		this.userProfile = user.userProfile;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
	
	public boolean isLocked() {
		return (locked == 1 ? true : false);
	}

	public int getAccountExpired() {
		return accountExpired;
	}

	public void setAccountExpired(int accountExpired) {
		this.accountExpired = accountExpired;
	}
	
	public boolean isAccountExpired() {
		return (accountExpired == 1 ? true : false);
	}

	public Date getPasswordExpiryDate() {
		return passwordExpiryDate;
	}

	public void setPasswordExpiryDate(Date passwordExpiryDate) {
		this.passwordExpiryDate = passwordExpiryDate;
	}

	public void setPasswordExpiryDate() {
		this.passwordExpiryDate = calculateExpiryDate(PASSWORD_EXPIRATION);
	}

	public boolean isInvited() {
		return invited;
	}

	public void setInvited(boolean invited) {
		this.invited = invited;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public int getPasswordExpired() {
		return passwordExpired;
	}

	public void setPasswordExpired(int passwordExpired) {
		this.passwordExpired = passwordExpired;
	}
	
	public boolean isPasswordExpired() {
		return (passwordExpired == 1 ? true : false);
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}
	
	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public boolean getLoggedIn() {
		return loggedIn;
	}
	
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public long getNumRecipes() {
		return numRecipes;
	}

	public void setNumRecipes(long numRecipes) {
		this.numRecipes = numRecipes;
	}
	
    public long getNewMsgCount() {
		return newMsgCount;
	}

	public void setNewMsgCount(long newMsgCount) {
		this.newMsgCount = newMsgCount;
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
 
    public Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
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
		return "User [id=" + id 
				+ ", firstName=" + firstName 
				+ ", lastName=" + lastName 
				+ ", email=" + email 
				+ ", password=" + password 
				+ ", enabled=" + enabled 
				+ ", tokenExpired=" + tokenExpired 
				+ ", locked=" + locked 
				+ ", accountExpired=" + accountExpired 
				+ ", dateAdded=" + dateAdded 
				+ ", lastLogin=" + lastLogin 
				+ ", passwordExpired=" + passwordExpired
				+ ", passwordExpiryDate=" +  passwordExpiryDate
				+ ", invited=" + invited
				+ ", loggedIn=" + loggedIn 
				+ ", numRecipes=" + numRecipes 
				+ ", role=" + role 
				+ ", userProfile=" + userProfile + "]";
	}	
}