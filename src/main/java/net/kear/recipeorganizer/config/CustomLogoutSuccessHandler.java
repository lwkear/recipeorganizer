package net.kear.recipeorganizer.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.util.AuthCookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

	private final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private AuthCookie authCookie;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		
		boolean foundCookie = false;
		Cookie cookie = null;
	
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (int i=0;i<cookies.length;i++) {
				cookie = cookies[i];
				logger.info("onInvalidSession: cookieName = " + cookie.getName() + " cookieValue = " + cookie.getValue());
				if (cookie.getName().equalsIgnoreCase("authUser")) {
					foundCookie = true;
				}
			}
		}

		logger.debug("onLogoutSuccess: contextPath = " + request.getContextPath());
		logger.debug("onLogoutSuccess: servletPath = " + request.getServletPath());
		logger.debug("onLogoutSuccess: servletContextPath = " + request.getServletContext());
		
		if (foundCookie)
			logger.debug("onLogoutSuccess: cookieDomain = " + cookie.getDomain());
		
		/*logger.debug("onLogoutSuccess: setting cookie.authUser = anonymous");
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
		
		response.addCookie(cookie);*/

		authCookie.retrieveCookie(request, response);
		authCookie.setCookie("anonymousUser");
		
		super.onLogoutSuccess(request, response, authentication);
	}	
}
