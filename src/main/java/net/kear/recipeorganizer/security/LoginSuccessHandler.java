package net.kear.recipeorganizer.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.util.CookieUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CookieUtil cookieUtil;
	@Autowired
	private UserService userService;	
	@Autowired
	private LoginAttemptService loginAttemptService;
	
	private RequestCache requestCache = new HttpSessionRequestCache();
	
	public LoginSuccessHandler() {
		super();
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		logger.info("onAuthenticationSuccess: name=" + authentication.getName());
		
		cookieUtil.setAuthCookie(request, response, authentication.getName());

		User user = userService.findUserByEmail(authentication.getName());
		
		boolean newUser = user.getLastLogin() == null ? true : false;
		userService.setLastLogin(user);
		
		if (user.isLocked()) {
			loginAttemptService.loginSucceeded(user.getEmail());
			user.setLocked(0);
		}
		userService.updateUser(user);

		String servePath = request.getServletPath();
		logger.debug("servePath:" + servePath);
		
		String redirectUrl = null;
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		if (savedRequest != null) {
			redirectUrl = savedRequest.getRedirectUrl();
			logger.debug("redirectUrl:" + redirectUrl);
		}
		
		if (newUser)
			getRedirectStrategy().sendRedirect(request, response, "/user/newMember");
		else
		if (redirectUrl != null)
			getRedirectStrategy().sendRedirect(request, response, redirectUrl);
		else
		if (!servePath.isEmpty() && servePath.indexOf("login") == -1)
			getRedirectStrategy().sendRedirect(request, response, servePath);
		else
			super.onAuthenticationSuccess(request, response, authentication);
	}
}
