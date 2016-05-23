package net.kear.recipeorganizer.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.util.UserInfo;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

public class AccessDeniedErrorHandler extends AccessDeniedHandlerImpl {

	@Autowired
	private MessageSource messages;
	@Autowired
	private UserInfo userInfo;
	
	public AccessDeniedErrorHandler() {}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException, ServletException {
		logger.debug("handle");
		
		String role = "";
		Authentication auth = userInfo.getAuthentication();
		if (auth != null) {
			role = userInfo.getRole();
		}		
		String uri = request.getRequestURI();

		setErrorPage("/accessDenied");
		
		if (auth != null && auth.isAuthenticated() && !StringUtils.isEmpty(uri)) {
			if (!userInfo.isUserAnonymous() && 
				role.equalsIgnoreCase(Role.TYPE_GUEST) && 
				(StringUtils.endsWith(uri, "/recipe/recipeList") || StringUtils.endsWith(uri, "/recipe"))) { 
					setErrorPage("/user/changeAccount");
			}
			if (userInfo.isUserAnonymous() &&
				(StringUtils.endsWith(uri, "/recipe/recipeList") || StringUtils.endsWith(uri, "/recipe"))) {
					setErrorPage("/user/join");
			}
		}
		
		super.handle(request, response, ex);
	}
}
//ExceptionTranslationFilter
//DefaultRedirectStrategy
//FilterSecurityInterceptor