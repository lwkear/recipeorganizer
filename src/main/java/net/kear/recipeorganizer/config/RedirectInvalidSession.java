package net.kear.recipeorganizer.config;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.kear.recipeorganizer.util.AuthCookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.session.SimpleRedirectInvalidSessionStrategy;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class RedirectInvalidSession implements InvalidSessionStrategy {
	private final Log logger = LogFactory.getLog(getClass());
	private final String destinationUrl;
	//private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	private boolean createNewSession = true;

	/*@Autowired*/
	private AuthCookie authCookie = new AuthCookie();
	
	public RedirectInvalidSession() {
		this.destinationUrl = "";
	}
	
	public RedirectInvalidSession(String invalidSessionUrl) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl), "url must start with '/' or with 'http(s)'");
		this.destinationUrl = invalidSessionUrl;
	}

	@Override
	public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		//boolean anonymous = false;
		
		//Cookie cookie = null;

		/*logger.debug("Checking request.session...");
		HttpSession session = request.getSession(false);
		if (session != null) {
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
			
			logger.info("onInvalidSession: " + sCreate);
			logger.info("onInvalidSession: " + sLast);
			logger.info("onInvalidSession: " + sInactive); 
			logger.info("onInvalidSession: " + sID);
			logger.info("onInvalidSession: " + sPrincipal);
		}
		else {
			logger.info("onInvalidSession: request.session is null");
			String sessID = request.getRequestedSessionId();
			boolean valid = request.isRequestedSessionIdValid();
			String remoteUser = request.getRemoteUser();
			//String attr = (String) request.getAttribute("authUser");
			//Cookie[] cookies = request.getCookies();
			
			String sID = "RequestedSessionID: " + sessID;
			logger.info("onInvalidSession: " + sID);
			logger.info("onInvalidSession: RequestedSessionIdValid = " + valid);
			logger.info("onInvalidSession: RemoteUser = " + remoteUser);
			//logger.info("onInvalidSession: authUser = " + attr);
		}*/
	
		/*logger.debug("Checking SecurityContext...");
		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {

			Authentication auth = context.getAuthentication();
			if (auth != null) {

				String principal = auth.getName();
				if (principal != null) {
					if (principal.equalsIgnoreCase("anonymoususer")) {
						logger.debug("Principal = anonymousUser");
						anonymous = true;
					}
				}
			
				Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
				if (!authorities.isEmpty()) {
				
					anonymous = authorities.contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
					logger.debug("Authority contains ROLE_ANONYMOUS: " + anonymous);
					
				}
			}
		}*/
		
		/*if (session != null) {
			String attr = (String) session.getAttribute("authUser");
			logger.debug("Session contains attribute: " + attr);
			
			if (attr.equalsIgnoreCase("anonymousUser"))
				anonymous = true;
		}*/

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
		
		/*if (anonymous) {
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
		}*/
		
		authCookie.retrieveCookie(request, response);
		if (authCookie.isCookieAnonymous()) {
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
			authCookie.setCookie("anonymousUser");			
		}

		//logger.debug("Starting new session (if required) and redirecting to '" + destinationUrl + "'");
		logger.debug("Starting new session (if required) and redirecting to '" + url + "'");
		
		if (createNewSession) {
			request.getSession();
		}
		
		redirectStrategy.sendRedirect(request, response, url);
	}

	/**
	 * Determines whether a new session should be created before redirecting (to avoid
	 * possible looping issues where the same session ID is sent with the redirected
	 * request). Alternatively, ensure that the configured URL does not pass through the
	 * {@code SessionManagementFilter}.
	 *
	 * @param createNewSession defaults to {@code true}.
	 */
	public void setCreateNewSession(boolean createNewSession) {
		this.createNewSession = createNewSession;
	}
}
