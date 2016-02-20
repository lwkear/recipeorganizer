package net.kear.recipeorganizer.util;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public UserInfo() {}

	public Authentication getAuthentication() {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null)
			return context.getAuthentication();
		else
			return null;
	}
		
	public Object getPrincipal() {
		Authentication auth = getAuthentication();
		if (auth != null)
			return auth.getPrincipal();
		else
			return null;
	}
	
	public String getName() {
		Object principal = getPrincipal();
		if (principal != null) {
			if (principal instanceof String) {
				return (String)principal;
			}
			if (principal instanceof Authentication) {
				Authentication auth = (Authentication)principal;
				return auth.getName();
			}
			if (principal instanceof UserDetails) {
				UserDetails user = (UserDetails)principal;
				return user.getUsername();
			}
		}

		return null;
	}
	
	public boolean isUserAnonymous() {
		Object principal = getPrincipal();
		if (principal != null) {
			if (principal instanceof UserDetails)
				return false;
		}
		return true;		
	}

	public boolean isUserRole(String role) {
		String currRole = getRole();
		return (role.equalsIgnoreCase(currRole) ? true : false);		
	}
	
	public UserDetails getUserDetails() {
		Object principal = getPrincipal();
		if (principal != null) {
			if (principal instanceof UserDetails)
				return (UserDetails)principal;
		}
		return null;	
	}
	
	public String getRole() {
		String role = "";
		Authentication auth = getAuthentication();
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		if (!authorities.isEmpty()) {
			for (GrantedAuthority authority : authorities) {
				role = authority.getAuthority();
			}
		}
		return role;
	}
}
