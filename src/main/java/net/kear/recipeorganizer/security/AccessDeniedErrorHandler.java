package net.kear.recipeorganizer.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.util.UserInfo;

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
		
		String role = "";
		Authentication auth = userInfo.getAuthentication();
		if (auth != null) {
			role = userInfo.getRole();
		}		
		String uri = request.getRequestURI();

		setErrorPage("/accessDenied");
		
		if (auth != null && auth.isAuthenticated() && !uri.isEmpty()) {
			if (!userInfo.isUserAnonymous() && 
				role.equalsIgnoreCase(Role.TYPE_GUEST) && 
				(uri.equals("/recipeorganizer/recipe/recipeList") || uri.equals("/recipeorganizer/recipe"))) { 
					setErrorPage("/user/changeAccount");
			}
		}
		
		super.handle(request, response, ex);
	}
}
