package net.kear.recipeorganizer.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.util.CookieUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class RedirectInvalidSession implements InvalidSessionStrategy {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final String destinationUrl;
	private DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private boolean createNewSession = true;
	//Note: autowire does not work because this bean is loaded in BeanPostProcessor for session management (see SecurityConfig.java)
	private static final CookieUtil cookieUtil = new CookieUtil();
	
	public RedirectInvalidSession() {
		this.destinationUrl = "";
	}
	
	public RedirectInvalidSession(String invalidSessionUrl) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl), "url must start with '/' or with 'http(s)'");
		this.destinationUrl = invalidSessionUrl;
	}

	@Override
	public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		logger.debug("onInvalidSessionDetected");

		String url = destinationUrl;
		
		//reset context default value
		redirectStrategy.setContextRelative(false);
		
		if (cookieUtil.isCookieAnonymous(request)) {
			url = request.getRequestURI();
			//the URL needs to have the context removed
			redirectStrategy.setContextRelative(true);
		}

		//always revert to anonymous user
		cookieUtil.setAuthCookie(request, response, CookieUtil.ANNON_USER);
		
		logger.debug("Starting new session (if required) and redirecting to '" + url + "'");
		
		if (createNewSession)
			request.getSession();
		
		redirectStrategy.sendRedirect(request, response, url);
	}

	public void setCreateNewSession(boolean createNewSession) {
		this.createNewSession = createNewSession;
	}
}
