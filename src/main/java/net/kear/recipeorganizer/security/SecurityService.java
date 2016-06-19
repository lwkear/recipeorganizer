package net.kear.recipeorganizer.security;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.UserMessageService;
import net.kear.recipeorganizer.persistence.service.UserService;

@Service
@Transactional
public class SecurityService implements UserSecurityService {

	@Autowired
	private LoginAttemptService loginAttemptService;
	@Autowired
	private UserMessageService userMessageService;
	@Autowired
	private SessionRegistry sessionRegistry;
	
	private UserService userService;	
	
    @Autowired
	public SecurityService(UserService userService) {
		this.userService = userService;
	}
    
    @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = null;
		user = userService.findUserByEmail(username);
		
		if (user == null) {
			throw new UsernameNotFoundException("Could not find user " + username);
		}
		
		boolean bUpdateUser = false;
		
		//check if the account is still active
		Date lastActivityDt = user.getLastLogin();
		if (lastActivityDt == null) {
			lastActivityDt = user.getDateUpdated();
			if (lastActivityDt == null)
				lastActivityDt = user.getDateAdded();
		}
		
		Calendar todaysDt = Calendar.getInstance();
		
		if (lastActivityDt != null) {
			Calendar lastLoginCal = Calendar.getInstance();
			lastLoginCal.setTime(lastActivityDt);
			lastLoginCal.add(Calendar.DATE, 365);
			
	        if (todaysDt.getTime().getTime() > lastLoginCal.getTime().getTime()) {
	        	user.setAccountExpired(1);
	        	bUpdateUser = true;
	        }
		}

		//check if the password is still valid
		Date passwordExpirtyDt = user.getPasswordExpiryDate();
		if (passwordExpirtyDt != null) {
	        if (todaysDt.getTime().getTime() > passwordExpirtyDt.getTime()) {
	        	user.setPasswordExpired(1);
	        	bUpdateUser = true;
	        }
		}		

        //if user exceeded the attempts, lock the account;
		//if account was previously locked, unlock it
		if (loginAttemptService.isBlocked(username)) {
        	user.setLocked(1);
        	bUpdateUser = true;
        }
        else
    	if (user.isLocked()) {
        	user.setLocked(0);
        	bUpdateUser = true;
    	}

		if (bUpdateUser)
			userService.updateUser(user);

		long count = userMessageService.getNotViewedCount(user.getId());
		if (count > 0)
			user.setNewMsgCount(count);
		
		return new CustomUserDetails(user);
	}

    public void reauthenticateUser(User user) {
		UserDetails details = loadUserByUsername(user.getEmail());
		Authentication auth= new UsernamePasswordAuthenticationToken(details, user.getPassword(), details.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    public UserDetails getUserDetails(User user) {
    	return new CustomUserDetails(user);
    }

    public boolean isUserLoggedIn(User user) {
    	
		List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
		if (allPrincipals != null && allPrincipals.size() > 0) {
			for (Object obj : allPrincipals) {
				UserDetails principal = (UserDetails) obj;
				if (principal.getUsername().equalsIgnoreCase(user.getEmail())) {
					List<SessionInformation> sessions = sessionRegistry.getAllSessions(obj, false);
					for (SessionInformation sessionInfo : sessions) {
						if (!sessionInfo.isExpired()) 
							return true;
					}
				}
			}
		}		
		
		return false;
    }
    
    public void expireUserSession(User user) {

		List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
		if (allPrincipals != null && allPrincipals.size() > 0) {
			for (Object obj : allPrincipals) {
				UserDetails principal = (UserDetails) obj;
				if (principal.getUsername().equalsIgnoreCase(user.getEmail())) {
					List<SessionInformation> sessions = sessionRegistry.getAllSessions(obj, false);
					for (SessionInformation sessionInfo : sessions) {
						if (!sessionInfo.isExpired()) 
							sessionInfo.expireNow();
					}
				}
			}
		}
    }
    
	private final static class CustomUserDetails extends User implements UserDetails, Serializable {

		private static final long serialVersionUID = 1L;		
		
		//Note: super(user) calls the parameterized constructor!  super() will call the empty constructor
		private CustomUserDetails(User user) {
			super(user);
		}
		
		private CustomUserDetails() {}
		
		public final Collection<? extends GrantedAuthority> getAuthorities() {
			
			String roleName = Role.TYPE_GUEST;
		
			Role userRole = getRole();
			if (userRole != null)
				roleName = userRole.getName();				
			
			return AuthorityUtils.createAuthorityList(roleName);
		}

		@Override
		public String getUsername() {
			return getEmail();
		}

		@Override
		public boolean isAccountNonExpired() {
			return !isAccountExpired();
		}

		@Override
		public boolean isAccountNonLocked() {
			return !isLocked();
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return !isPasswordExpired();
		}
	}
}
