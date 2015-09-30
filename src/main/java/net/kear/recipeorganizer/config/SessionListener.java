package net.kear.recipeorganizer.config;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SessionListener implements HttpSessionListener {

	private static final Logger logger = LoggerFactory.getLogger(SessionListener.class);
	
	@Override
	public void sessionCreated(HttpSessionEvent event) {

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
		
		logger.info("sessionCreated: " + sCreate);
		logger.info("sessionCreated: " + sLast);
		logger.info("sessionCreated: " + sInactive); 
		logger.info("sessionCreated: " + sID);
		logger.info("sessionCreated: " + sPrincipal);
		
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

		/*logger.debug("Setting authUser attribute...");
		session.setAttribute("authUser", principal);*/
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		
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
		
		logger.info("sessionDestroyed: " + sCreate);
		logger.info("sessionDestroyed: " + sLast);
		logger.info("sessionDestroyed: " + sInactive); 
		logger.info("sessionDestroyed: " + sID);
		logger.info("sessionDestroyed: " + sPrincipal);

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


//SessionManagementFilter
//AnonymousAuthenticationFilter
//SecurityContextPersistenceFilter
//HeaderWriterFilter
//SimpleRedirectInvalidSessionStrategy
//DefaultRedirectStrategy
//SimpleUrlAuthenticationFailureHandler
//PersistentTokenBasedRememberMeServices
//AuthenticationTrustResolver
//AnonymousAuthenticationFilter
//HttpSessionEventPublisher
//HttpServletRequest
//SimpleUrlLogoutSuccessHandler
