package net.kear.recipeorganizer.util;

import java.io.Serializable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class AuthCookie implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Log logger = LogFactory.getLog(getClass());
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	private boolean anonymous = false;
	private Cookie cookie = null;
	
	public AuthCookie() {}
	
	public void retrieveCookie(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;

		Assert.notNull(this.request, "httpServletRequest is null");
		Assert.notNull(this.response, "httpServletResponse is null");
		
		findCookie();
	}
	
	public void setCookie(String value) {

		if (cookie != null) {
			cookie.setValue(value);
			cookie.setPath(request.getContextPath());
		}
		else {
			cookie = new Cookie("authUser", value);
			cookie.setPath(request.getContextPath());
		}
		
		response.addCookie(cookie);
	}
	
	public boolean isCookieAnonymous() {
		return this.anonymous;
	}

	private void findCookie() {

		this.cookie = null;
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (int i=0;i<cookies.length;i++) {
				Cookie cookie = cookies[i];
				logger.info("findCookie: cookieName = " + cookie.getName() + " cookieValue = " + cookie.getValue());
				if (cookie.getName().equalsIgnoreCase("authUser")) {
					this.cookie = cookie;
					if (cookie.getValue().equalsIgnoreCase("anonymousUser"))
						this.anonymous = true;
					break;
				}
			}
		}
	}
}


/*
boolean anonymous = false;
boolean foundCookie = false;
Cookie cookie = null;
String authValue = null;

Cookie[] cookies = request.getCookies();
if (cookies != null && cookies.length > 0) {
	for (int i=0;i<cookies.length;i++) {
		cookie = cookies[i];
		logger.info("onInvalidSession: cookieName = " + cookie.getName() + " cookieValue = " + cookie.getValue());
		if (cookie.getName().equalsIgnoreCase("authUser")) {
			foundCookie = true;
			authValue = cookie.getValue();
			if (authValue.equalsIgnoreCase("anonymousUser"))
				anonymous = true;
		}
	}
}

logger.debug("onInvalidSession: original destinationURL = " + destinationUrl); 
logger.debug("onInvalidSession: contextPath = " + request.getContextPath());
logger.debug("onInvalidSession: servletPath = " + request.getServletPath());
logger.debug("onInvalidSession: servletContextPath = " + request.getServletContext());

if (foundCookie)
	logger.debug("onInvalidSession: cookieDomain = " + cookie.getDomain());

String url = destinationUrl;

if (anonymous) {
	url = request.getRequestURI();
	//the URL needs to have the context removed
	redirectStrategy.setContextRelative(true);
	logger.debug("onInvalidSession: user is anonymous - setting requestURI = " + url);			
}
else {
	
	logger.debug("onInvalidSession: setting cookie.authUser = anonymous");
	//reset default value
	redirectStrategy.setContextRelative(false);
	//replace authenticated user with anonymous
	if (foundCookie) {
		cookie.setValue("anonymousUser");
		//cookie.setDomain(request.getContextPath());
		cookie.setPath(request.getContextPath());
	}
	else {
		cookie = new Cookie("authUser", "anonymousUser");
		cookie.setHttpOnly(true);response.addCookie(cookie);
		//cookie.setDomain(request.getContextPath());
		cookie.setPath(request.getContextPath());
	}
	
	response.addCookie(cookie);
}		
*/