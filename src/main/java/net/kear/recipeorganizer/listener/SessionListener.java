package net.kear.recipeorganizer.listener;

import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionListener implements HttpSessionListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());

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
		
		logger.debug("sessionCreated: " + sCreate);
		logger.debug("sessionCreated: " + sLast);
		logger.debug("sessionCreated: " + sInactive); 
		logger.debug("sessionCreated: " + sID);
		logger.debug("sessionCreated: " + sPrincipal);
		
		/*HttpSession session = event.getSession();
		String sessionId = session.getId();
		
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
		SessionRegistry sessionRegistry = (SessionRegistry) ctx.getBean("sessionRegistry");
				
		List<Object> allPrincipals = null;
		if (sessionRegistry != null)
			allPrincipals = sessionRegistry.getAllPrincipals();
		
		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {
			Authentication auth = context.getAuthentication();
			if (auth != null) {
				Object principal = auth.getPrincipal();
				if (principal != null) {
					if (principal.toString().equals(CookieUtil.ANNON_USER)) {
						logger.debug("sessionCreated: bypassing " + principal);
						return;
					}
					
					if (allPrincipals != null && allPrincipals.size() > 0) {
						if (allPrincipals.contains(principal)) {
							List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, true);
							for (SessionInformation sess : sessions) {
								String sessId = sess.getSessionId();
								sessionRegistry.removeSessionInformation(sessId);
							}
						}
					}
					sessionRegistry.registerNewSession(sessionId, principal);
					logger.debug("sessionCreated: registerNewSession called for " + principal);
				}
			}
		}*/
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
		
		logger.debug("sessionDestroyed: " + sCreate);
		logger.debug("sessionDestroyed: " + sLast);
		logger.debug("sessionDestroyed: " + sInactive); 
		logger.debug("sessionDestroyed: " + sID);
		logger.debug("sessionDestroyed: " + sPrincipal);

		/*logger.debug("Checking SecurityContext...");
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
			logger.debug("SecurityContext is null");*/
	}
}


/*
logger.debug("Checking event.session...");
HttpSession session = event.getSession();

ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
SessionRegistry sessionRegistry = (SessionRegistry) ctx.getBean("sessionRegistry");

//session.setMaxInactiveInterval(30);

Date createTime = new Date(session.getCreationTime());
Date lastAccess = new Date(session.getLastAccessedTime());
int maxInactive = session.getMaxInactiveInterval();
String sessID = session.getId();

Enumeration<String> attrNames = session.getAttributeNames();
while (attrNames.hasMoreElements())
	logger.debug("sessionCreated: attrNames " + attrNames.nextElement());

String sCreate = "Session created on: " + createTime;
String sLast = "Session last accessed on: " + lastAccess;
String sInactive = "Session expires after: " + maxInactive + " seconds";
String sID = "Session ID: " + sessID;

logger.debug("sessionCreated: " + sCreate);
logger.debug("sessionCreated: " + sLast);
logger.debug("sessionCreated: " + sInactive); 
logger.debug("sessionCreated: " + sID);

List<Object> allPrinc = null;
if (sessionRegistry != null) {
	allPrinc = sessionRegistry.getAllPrincipals();
	logger.debug("sessionRegistry: " + allPrinc.toString());
}
else
	logger.debug("sessionRegistry is null");

logger.debug("Checking SecurityContext...");
SecurityContext context = SecurityContextHolder.getContext();
if (context != null) {

	Authentication auth = context.getAuthentication();
	if (auth != null) {
		
		logger.debug("AuthenticationToken is anonymous: " + ((auth instanceof AnonymousAuthenticationToken) ? "true" : "false"));

		Object principal = auth.getPrincipal();
		logger.debug("Principal: " + principal);

		if (principal != null && allPrinc != null && allPrinc.size() > 0) {
			if (allPrinc.contains(principal))
				logger.debug("getAllPrincipals() contains prinicpal");
			
			List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, true);
			for (SessionInformation sess : sessions) {
				String sessionId = sess.getSessionId();
				logger.debug("sessionRegistry.sessId: " + sessionId);
			}
		}
		
		String name = auth.getName();
		if (name != null) {
			logger.debug("PrincipalName: " + name);
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
*/