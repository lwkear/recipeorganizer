package net.kear.recipeorganizer.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Privilege;
import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.persistence.model.Users;
import net.kear.recipeorganizer.persistence.service.UsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserSecurityService implements UserDetailsService {

	//@Autowired
	private UsersService usersService;

    @Autowired
	public UserSecurityService(UsersService usersService) {
		this.usersService = usersService;
	}
    
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Users user = usersService.findUserByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("Could not find user " + username);
		}
		
		return new CustomUserDetails(user);
	}

	private final static class CustomUserDetails extends Users implements UserDetails {
		
		//Note: super(user) calls the parameterized constructor!  super() will call the empty constructor
		private CustomUserDetails(Users user) {
			super(user);
		}
		
		private CustomUserDetails() {}
		
		public final Collection<? extends GrantedAuthority> getAuthorities() {
			
			String roleName = "GUEST";
		
			Role userRole = getRole();
			if (userRole != null)
				roleName = userRole.getName();				
			
			return AuthorityUtils.createAuthorityList(roleName);
		}

		@Override
		public String getUsername() {
			return getEmail();
		}

		public boolean isAccountNonExpired() {
			return true;
		}

		public boolean isAccountNonLocked() {
			return true;
		}

		public boolean isCredentialsNonExpired() {
			return true;
		}

		/* implemented in Users class
		public String getPassword() {
		}		
		public boolean isEnabled() {
		}
		*/

		/*
		public final Collection<? extends GrantedAuthority> getAuthorities() {
			
			List<Role> roles = new ArrayList<Role>();
			Role userRole = getRole();
			roles.add(userRole);
			
	        return getGrantedAuthorities(getPrivileges(roles));
	    }

	    private final List<String> getPrivileges(final Collection<Role> roles) {
	        final List<String> privileges = new ArrayList<String>();
	        final List<Privilege> collection = new ArrayList<Privilege>();
	        for (final Role role : roles) {
	            collection.addAll(role.getPrivileges());
	        }
	        for (final Privilege item : collection) {
	            privileges.add(item.getName());
	        }
	        return privileges;
	    }

	    private final List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
	        final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	        for (final String privilege : privileges) {
	            authorities.add(new SimpleGrantedAuthority(privilege));
	        }
	        return authorities;
	    }
	    */		
		
		private static final long serialVersionUID = 1L;
	}
   
}
