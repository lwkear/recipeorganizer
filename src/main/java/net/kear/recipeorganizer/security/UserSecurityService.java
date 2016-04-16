package net.kear.recipeorganizer.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import net.kear.recipeorganizer.persistence.model.User;

public interface UserSecurityService extends UserDetailsService {
	
	public void reauthenticateUser(User user);
	public UserDetails getUserDetails(User user);
	public boolean isUserLoggedIn(User user);
	public void expireUserSession(User user);
}
