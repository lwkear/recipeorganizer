package net.kear.recipeorganizer.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	public AuthenticationFailureHandler() {
		super();
	}

	public AuthenticationFailureHandler(String defaultFailureUrl) {
		super(defaultFailureUrl);
	}
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
			throws IOException, ServletException {

		setDefaultFailureUrl("/user/loginError"); 
		
		String className = ex.getClass().getSimpleName();
		switch (className) {
			case "AccountExpiredException":
			case "CredentialsExpiredException":
			case "DisabledException":
			case "LockedException":
			case "BadCredentialsException":
			case "RememberMeAuthenticationException":
			case "UsernameNotFoundException":
				super.onAuthenticationFailure(request, response, ex);
				return;
		}
		
		throw ex;
		
		//if (exception.getClass().isAssignableFrom(BadCredentialsException.class)) {//
		
		//*** see AuthenticationException javadoc
		//AccountStatusException - Base class for authentication exceptions which are caused by a particular user account status (locked, disabled etc).
		//	    AccountExpiredException, CredentialsExpiredException, DisabledException, LockedException
		//AuthenticationCredentialsNotFoundException -  Thrown if an authentication request is rejected because there is no Authentication object in the SecurityContext.
		//AuthenticationServiceException - Thrown if an authentication request could not be processed due to a system problem. This might be thrown if a backend authentication repository is unavailable, for example.
		//BadCredentialsException - Thrown if an authentication request is rejected because the credentials are invalid. For this exception to be thrown, it means the account is neither locked nor disabled.
		//InsufficientAuthenticationException - Thrown if an authentication request is rejected because the credentials are not sufficiently trusted.
		//ProviderNotFoundException - Thrown by ProviderManager if no AuthenticationProvider could be found that supports the presented Authentication object.
		//RememberMeAuthenticationException - This exception is thrown when an Authentication exception occurs while using the remember-me authentication.
		//SessionAuthenticationException - Thrown by an SessionAuthenticationStrategy to indicate that an authentication object is not valid for the current session, typically because the same user has exceeded the number of sessions they are allowed to have concurrently.
		//UsernameNotFoundException - Thrown if an UserDetailsService implementation cannot locate a User by its username.
    }
}
