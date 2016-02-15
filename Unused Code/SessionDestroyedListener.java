package net.kear.recipeorganizer.listener;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;

public class SessionDestroyedListener implements ApplicationListener<HttpSessionDestroyedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(SessionDestroyedListener.class);

	@Override
	public void onApplicationEvent(HttpSessionDestroyedEvent event) {

		logger.debug("Checking event.session...");
		HttpSession session = event.getSession();
		Date createTime = new Date(session.getCreationTime());
		Date lastAccess = new Date(session.getLastAccessedTime());
		int maxInactive = session.getMaxInactiveInterval();
		String sessID = session.getId();
		String principal = (String) session.getAttribute("authUser");
		
		String sCreate = "Session created on: " + createTime;
		String sLast = "Session last accessed on: " + lastAccess;
		String sInactive = "Session expires after: " + maxInactive + " seconds";
		String sID = "Session ID: " + sessID;
		String sPrincipal = "Session principal: " + principal;
		
		logger.debug("sessionDestroyedListener: " + sCreate);
		logger.debug("sessionDestroyedListener: " + sLast);
		logger.debug("sessionDestroyedListener: " + sInactive); 
		logger.debug("sessionDestroyedListener: " + sID);
		logger.debug("sessionDestroyedListener: " + sPrincipal);

		logger.debug("Checking SecurityContext...");
		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {

			Authentication auth = context.getAuthentication();
			if (auth != null) {
				
				logger.debug("AuthenticationToken is anonymous: " + ((auth instanceof AnonymousAuthenticationToken) ? "true" : "false"));

				principal = auth.getName();
				if (principal != null) {
					logger.debug("Principal: " + principal);
				}
			
				Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
				if (!authorities.isEmpty()) {
				
					for (GrantedAuthority authority : authorities) {
						logger.debug("Authority contains: " + authority.getAuthority());
					}
				}
			}
			else
				logger.debug("Authentication is null");
		}
		else
			logger.debug("SecurityContext is null");
	}
	
}
