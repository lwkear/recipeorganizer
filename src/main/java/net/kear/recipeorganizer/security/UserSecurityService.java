package net.kear.recipeorganizer.security;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.UserService;

@Service
@Transactional
public class UserSecurityService implements UserDetailsService {

	private UserService userService;

    @Autowired
	public UserSecurityService(UserService userService) {
		this.userService = userService;
	}
    
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userService.findUserByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("Could not find user " + username);
		}
		
		return new CustomUserDetails(user);
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