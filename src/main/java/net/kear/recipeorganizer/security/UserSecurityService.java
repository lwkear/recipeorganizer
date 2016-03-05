package net.kear.recipeorganizer.security;

import org.springframework.security.core.userdetails.UserDetailsService;

import net.kear.recipeorganizer.persistence.model.User;

public interface UserSecurityService extends UserDetailsService {
	
	public void reauthenticateUser(User user);
}
