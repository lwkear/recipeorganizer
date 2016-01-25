package net.kear.recipeorganizer.security;

import java.io.Serializable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class AuthCookie implements Serializable {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final long serialVersionUID = 1L;
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	private boolean anonymous = false;
	private String value = "";
	private Cookie cookie = null;
	
	public AuthCookie() {}
	
	public void setCookie(HttpServletRequest request, HttpServletResponse response, String value) {
		this.request = request;
		this.response = response;
		this.value = value;

		Assert.notNull(this.request, "httpServletRequest is null");
		Assert.notNull(this.response, "httpServletResponse is null");
		
		findCookie();
		setCookie();
	}
	
	private void findCookie() {

		this.cookie = null;
		anonymous = false;
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (int i=0;i<cookies.length;i++) {
				Cookie cookie = cookies[i];
		
				logger.debug("cookieName = " + cookie.getName() + " cookieValue = " + cookie.getValue());
				
				if (cookie.getName().equals("authUser")) {
					this.cookie = cookie;
					if (cookie.getValue().equals("anonymousUser"))
						anonymous = true;
					break;
				}
			}
		}
	}

	private void setCookie() {
		
		logger.debug("Setting cookie to " + value);

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
	
	public boolean isCurrentCookieAnonymous(HttpServletRequest request) {
		this.request = request;
		findCookie();
		return anonymous;
	}
	
	public boolean cookieExists(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (int i=0;i<cookies.length;i++) {
				Cookie cookie = cookies[i];
				if (cookie.getName().equals("authUser"))
					return true;
			}
		}
		
		return false;
	}
}
