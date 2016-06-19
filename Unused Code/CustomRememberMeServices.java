package net.kear.recipeorganizer.security;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

public class CustomRememberMeServices extends TokenBasedRememberMeServices {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	DataSource dataSource;

	public CustomRememberMeServices(String key, UserDetailsService userDetailsService) {
		super(key, userDetailsService);
	}

	@Override
	protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) 
			throws RememberMeAuthenticationException, UsernameNotFoundException, InternalAuthenticationServiceException {

		try {
			dataSource.getConnection();
		} catch (Exception ex) {
			logger.debug("dataSource connection error");
			logger.error(ex.getClass().toString(), ex);
			//throw new InternalAuthenticationServiceException("dataSource connection error", ex);
			throw new RememberMeAuthenticationException("dataSource connection error", ex);
		}
		
		return super.processAutoLoginCookie(cookieTokens, request, response);
	}

	@Override
	protected void onLoginFail(HttpServletRequest request, HttpServletResponse response) {
		super.onLoginFail(request, response);
	}
}
