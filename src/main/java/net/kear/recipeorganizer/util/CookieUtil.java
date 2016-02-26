package net.kear.recipeorganizer.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CookieUtil {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
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
		cookie.setPath(request.getContextPath());
		cookie.setMaxAge(60 * 60 * 24 * 365 * 10);
		response.addCookie(cookie);
	}
	
	public void deleteRememberMe(HttpServletRequest request, HttpServletResponse response) {
		String cookieName = "remember-me";
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		cookie.setPath(StringUtils.hasLength(request.getContextPath()) ? request.getContextPath() : "/");
		response.addCookie(cookie);
	}
	
}
