package net.kear.recipeorganizer.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AuthCookie authCookie;
	@Autowired
	private UserService userService;	
	
	public LoginSuccessHandler() {
		super();
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		logger.info("onAuthenticationSuccess: name=" + authentication.getName());
		
		authCookie.setCookie(request, response, authentication.getName());

		//try {
			User user = userService.findUserByEmail(authentication.getName());
			user.setLastLogin(new Date());
			userService.updateUser(user);
		//} catch (Exception ex) {
			//do nothing - the exception is probably caused by the db being down 
		//}
		
		String uri = request.getRequestURI();
		logger.debug("AuthenticationSuccess uri: " + uri);
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
