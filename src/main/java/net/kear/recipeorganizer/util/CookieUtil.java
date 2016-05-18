package net.kear.recipeorganizer.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;

@Component
public class CookieUtil {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String authCookieName = "authUser"; 
	public static final String ANNON_USER = "anonymousUser";
	private static final String cookiePath = "/";
	
	public CookieUtil() {}
	
	public Cookie findUserCookie(HttpServletRequest request, String name, Long userId) {

		String cookieName = name + String.valueOf(userId);
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (int i=0;i<cookies.length;i++) {
				Cookie cookie = cookies[i];
		
				logger.debug("cookieName = " + cookie.getName() + " cookieValue = " + cookie.getValue());
				
				if (cookie.getName().equalsIgnoreCase(cookieName)) {
					return cookie;
				}
			}
		}

		return null;		
	}
	
	public void setUserCookie(HttpServletRequest request, HttpServletResponse response, String name, Long userId, String value) {
		String cookieName = name + String.valueOf(userId);
		Cookie cookie = new Cookie(cookieName, value);
		//cookie.setPath(request.getContextPath());
		cookie.setPath(cookiePath);
		cookie.setMaxAge(60 * 60 * 24 * 365 * 10);
		response.addCookie(cookie);
	}

	public void deleteRememberMe(HttpServletRequest request, HttpServletResponse response) {
		String cookieName = "remember-me";
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		//cookie.setPath(StringUtils.hasLength(request.getContextPath()) ? request.getContextPath() : "/");
		cookie.setPath(cookiePath);
		response.addCookie(cookie);
	}

	public void setAuthCookie(HttpServletRequest request, HttpServletResponse response, String value) {
		Cookie authCookie = findAuthCookie(request);
		setAuthCookie(request, response, authCookie, value);
	}
	
	public boolean isCookieAnonymous(HttpServletRequest request) {
		Cookie authCookie = findAuthCookie(request);
		if (authCookie == null)
			return false;
		return (authCookie.getValue().equals(ANNON_USER));
	}
	
	public boolean authCookieExists(HttpServletRequest request) {
		Cookie authCookie = findAuthCookie(request);
		if (authCookie == null)
			return false;
		return (authCookie.getName().equals(authCookieName));
	}
	
	private Cookie findAuthCookie(HttpServletRequest request) {
	
		Cookie authCookie = null;
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (int i=0;i<cookies.length;i++) {
				Cookie cookie = cookies[i];
		
				logger.debug("cookieName = " + cookie.getName() + " cookieValue = " + cookie.getValue());
				
				if (cookie.getName().equals(authCookieName)) {
					authCookie = cookie;
					break;
				}
			}
		}
		
		return authCookie;
	}
	
	private void setAuthCookie(HttpServletRequest request, HttpServletResponse response, Cookie cookie, String value) {
		
		logger.debug("Setting cookie to " + value);
		logger.debug("contextPath: " + request.getContextPath());

		if (cookie != null) {
			cookie.setValue(value);
			//cookie.setPath(request.getContextPath());
			cookie.setPath(cookiePath);
			logger.debug("Cookie != null");
		}
		else {
			cookie = new Cookie(authCookieName, value);
			//cookie.setPath(request.getContextPath());
			cookie.setPath(cookiePath);
			logger.debug("Cookie is null");
		}
		
		response.addCookie(cookie);
	}
}