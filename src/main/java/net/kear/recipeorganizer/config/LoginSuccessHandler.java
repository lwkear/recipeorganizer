package net.kear.recipeorganizer.config;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.kear.recipeorganizer.util.AuthCookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

//@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private AuthCookie authCookie;
	
	public LoginSuccessHandler() {
		super();
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		
		String principal = "";
		
		logger.debug("Checking Authentication...");
		if (authentication != null) {
			
			logger.debug("AuthenticationToken is anonymous: " + ((authentication instanceof AnonymousAuthenticationToken) ? "true" : "false"));
				
			principal = authentication.getName();
			if (principal != null) {
				logger.debug("Principal: " + principal);
			}
		
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			if (!authorities.isEmpty()) {
			
				for (GrantedAuthority auth : authorities) {
					logger.debug("Authority contains: " + auth.getAuthority());
				}
			}
		}
		else
			logger.debug("Authentication is null");
		
		/*logger.debug("Setting session.authUser = " + principal);
		HttpSession sess = request.getSession();
		sess.setMaxInactiveInterval(30);
		sess.setAttribute("authUser", principal);*/
		
		logger.debug("Setting cookie.authUser = " + principal);
		/*Cookie cookie = new Cookie("authUser", principal);
		cookie.setHttpOnly(true);
		cookie.setPath(request.getContextPath());
		response.addCookie(cookie);*/
		
		authCookie.retrieveCookie(request, response);
		authCookie.setCookie(principal);
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
}

/*
Baeldung exanple:

public class CustomAuthenticationSuccessHandler implements
AuthenticationSuccessHandler {

private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

@Override
public void onAuthenticationSuccess(HttpServletRequest request,
	HttpServletResponse response, Authentication authentication) throws IOException,
	ServletException {
HttpSession session = request.getSession();

//Set some session variables
User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  
session.setAttribute("uname", authUser.getUsername());  
session.setAttribute("authorities", authentication.getAuthorities()); 

//Set target URL to redirect
String targetUrl = determineTargetUrl(authentication); 
redirectStrategy.sendRedirect(request, response, targetUrl);
}

protected String determineTargetUrl(Authentication authentication) {
Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
if (authorities.contains("ROLE_ADMIN")) {
	return "/admin.htm";
} else if (authorities.contains("ROLE_USER")) {
	return "/user.htm";
} else {
    throw new IllegalStateException();
}
}

public RedirectStrategy getRedirectStrategy() {
return redirectStrategy;
}

public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
this.redirectStrategy = redirectStrategy;
}
}*/