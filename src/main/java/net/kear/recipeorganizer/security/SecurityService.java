package net.kear.recipeorganizer.security;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.UserService;

@Service
@Transactional
public class SecurityService implements UserSecurityService {

	private UserService userService;

    @Autowired
	public SecurityService(UserService userService) {
		this.userService = userService;
	}
    
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userService.findUserByEmail(username);
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
		
		if (bUpdateUser)
			userService.updateUser(user);
		
		return new CustomUserDetails(user);
	}

    public void reauthenticateUser(User user) {
		UserDetails details = loadUserByUsername(user.getEmail());
		Authentication auth= new UsernamePasswordAuthenticationToken(details, user.getPassword(), details.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
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