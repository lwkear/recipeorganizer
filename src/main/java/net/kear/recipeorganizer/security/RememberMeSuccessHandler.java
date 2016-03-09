package net.kear.recipeorganizer.security;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

public class RememberMeSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private AuthCookie authCookie;
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	private UserService userService;	

	private RequestCache requestCache = new HttpSessionRequestCache();
	
	public RememberMeSuccessHandler() {
		super();
		setUseReferer(true);		
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		logger.info("onAuthenticationSuccess: name=" + authentication.getName());
		
		authCookie.setCookie(request, response, authentication.getName());
		
		User user = userService.findUserByEmail(authentication.getName());

		Calendar todaysDt = Calendar.getInstance();
		todaysDt.setTimeInMillis(new Date().getTime());
		user.setLastLogin(new Date(todaysDt.getTime().getTime()));
		userService.updateUser(user);
		
		boolean foundUser = false;
		boolean foundSession = false;
		
		//rememberMe does not automatically add the user to the sessionRegistry so must do so manually
		List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
		if (allPrincipals != null && allPrincipals.size() > 0) {
			for (Object obj : allPrincipals) {
				UserDetails principal = (UserDetails) obj;
				if (principal.getUsername() == user.getEmail()) {
					foundUser = true;
					List<SessionInformation> sessions = sessionRegistry.getAllSessions(obj, false);
					if (!sessions.isEmpty())
						foundSession = true;
				}
			}
		}
		
		if (!foundUser || !foundSession)
			sessionRegistry.registerNewSession(request.getSession().getId(), authentication.getPrincipal());
		
		String servePath = request.getServletPath();
		logger.debug("servePath:" + servePath);
		
		String redirectUrl = null;
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		if (savedRequest != null) {
			redirectUrl = savedRequest.getRedirectUrl();
			logger.debug("redirectUrl:" + redirectUrl);
		}

		if (redirectUrl != null)
			getRedirectStrategy().sendRedirect(request, response, redirectUrl);
		else
		if (!servePath.isEmpty() && servePath.length() > 1)
			getRedirectStrategy().sendRedirect(request, response, servePath);
		else
			super.onAuthenticationSuccess(request, response, authentication);
	}
}

/*Object principal = authentication.getPrincipal();
logger.debug("Session principal: " + principal.toString());

HttpSession session = request.getSession(false);
if (session != null) {
	String sessID = session.getId();
	logger.debug("Session ID: " + sessID);
}
else
	logger.debug("Session is null");

List<Object> allPrinc = sessionRegistry.getAllPrincipals();

if (principal != null && allPrinc.size() > 0) {
	if (allPrinc.contains(principal))
		logger.debug("getAllPrincipals() contains prinicpal");
}

for (Object obj : allPrinc) {
	final List<SessionInformation> sessions = sessionRegistry.getAllSessions(obj, true);

	for (SessionInformation sess : sessions) {
		Object princ = sess.getPrincipal();
		String sessId = sess.getSessionId();
		Date sessDate = sess.getLastRequest();
		
		logger.debug("sessionRegistry.princ: " + princ);
		logger.debug("sessionRegistry.sessId: " + sessId);
		logger.debug("sessionRegistry.sessDate: " + sessDate.toString());
		
		if (principal.equals(princ))
			logger.debug("found prinicpal match");
	}
}

		String uri = request.getRequestURI();
		StringBuffer url = request.getRequestURL();
		String contextPath = request.getContextPath();
		String pathInfo = request.getPathInfo();
		String pathTrans = request.getPathTranslated();
		String uri = request.getRequestURI();
		StringBuffer url = request.getRequestURL();
		String contextPath = request.getContextPath();
		String pathInfo = request.getPathInfo();
		String pathTrans = request.getPathTranslated();

		Enumeration<String> hdrNames = request.getHeaderNames();
		while (hdrNames.hasMoreElements())
			logger.debug("getHome: hdrNames " + hdrNames.nextElement());

		Enumeration<String> hdrNames = request.getHeaderNames();
		while (hdrNames.hasMoreElements())
			logger.debug("getHome: hdrNames " + hdrNames.nextElement());


ConcurrentSessionControlAuthenticationStrategy

2016-02-27 07:06:31,445 DEBUG: org.springframework.security.web.savedrequest.HttpSessionRequestCache - DefaultSavedRequest added to Session: DefaultSavedRequest[http://localhost:8080/recipeorganizer/recipe/viewRecipe/1502]
2016-02-27 07:06:31,445 DEBUG: org.springframework.security.web.access.ExceptionTranslationFilter - Calling Authentication entry point.
2016-02-27 07:06:31,476 DEBUG: org.springframework.security.web.savedrequest.DefaultSavedRequest - pathInfo: both null (property equals)
2016-02-27 07:06:31,476 DEBUG: org.springframework.security.web.savedrequest.DefaultSavedRequest - queryString: both null (property equals)
2016-02-27 07:06:31,476 DEBUG: org.springframework.security.web.savedrequest.DefaultSavedRequest - requestURI: arg1=/recipeorganizer/recipe/viewRecipe/1502; arg2=/recipeorganizer/user/login (property not equals)
2016-02-27 07:06:31,491 DEBUG: org.springframework.security.web.savedrequest.HttpSessionRequestCache - saved request doesn't match

2016-02-27 07:06:33,447 DEBUG: org.springframework.security.web.access.intercept.FilterSecurityInterceptor - Secure object: FilterInvocation: URL: /getSessionTimeout; Attributes: [authenticated]
2016-02-27 07:06:33,447 DEBUG: org.springframework.security.web.access.intercept.FilterSecurityInterceptor - Previously Authenticated: org.springframework.security.authentication.AnonymousAuthenticationToken@90514580: Principal: anonymousUser; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@43458: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: 08156CE143EFE14A6BEFCE04A28A5EF6; Granted Authorities: ROLE_ANONYMOUS
2016-02-27 07:06:33,448 DEBUG: org.springframework.security.access.vote.AffirmativeBased - Voter: org.springframework.security.web.access.expression.WebExpressionVoter@778b3121, returned: -1
2016-02-27 07:06:33,456 DEBUG: org.springframework.security.web.access.ExceptionTranslationFilter - Access is denied (user is anonymous); redirecting to authentication entry point

2016-02-27 07:33:39,755 DEBUG: org.springframework.security.web.util.matcher.AntPathRequestMatcher - Checking match of request : '/user/login'; against '/resources/**'
2016-02-27 07:33:39,755 DEBUG: org.springframework.security.web.FilterChainProxy - /user/login at position 1 of 15 in additional filter chain; firing Filter: 'WebAsyncManagerIntegrationFilter'
2016-02-27 07:33:39,755 DEBUG: org.springframework.security.web.FilterChainProxy - /user/login at position 2 of 15 in additional filter chain; firing Filter: 'SecurityContextPersistenceFilter'
2016-02-27 07:33:39,755 DEBUG: org.springframework.security.web.context.HttpSessionSecurityContextRepository - HttpSession returned null object for SPRING_SECURITY_CONTEXT
2016-02-27 07:33:39,755 DEBUG: org.springframework.security.web.context.HttpSessionSecurityContextRepository - No SecurityContext was available from the HttpSession: org.apache.catalina.session.StandardSessionFacade@1cf02b20. A new one will be created.
2016-02-27 07:33:39,755 DEBUG: org.springframework.security.web.FilterChainProxy - /user/login at position 3 of 15 in additional filter chain; firing Filter: 'HeaderWriterFilter'
2016-02-27 07:33:39,755 DEBUG: org.springframework.security.web.header.writers.HstsHeaderWriter - Not injecting HSTS header since it did not match the requestMatcher org.springframework.security.web.header.writers.HstsHeaderWriter$SecureRequestMatcher@3a3aa715
2016-02-27 07:33:39,755 DEBUG: org.springframework.security.web.FilterChainProxy - /user/login at position 4 of 15 in additional filter chain; firing Filter: 'CharacterEncodingFilter'
2016-02-27 07:33:39,755 DEBUG: org.springframework.security.web.FilterChainProxy - /user/login at position 5 of 15 in additional filter chain; firing Filter: 'CsrfFilter'
2016-02-27 07:33:39,755 DEBUG: org.springframework.security.web.FilterChainProxy - /user/login at position 6 of 15 in additional filter chain; firing Filter: 'LogoutFilter'
2016-02-27 07:33:39,755 DEBUG: org.springframework.security.web.util.matcher.AntPathRequestMatcher - Checking match of request : '/user/login'; against '/logout'
2016-02-27 07:33:39,755 DEBUG: org.springframework.security.web.FilterChainProxy - /user/login at position 7 of 15 in additional filter chain; firing Filter: 'UsernamePasswordAuthenticationFilter'
2016-02-27 07:33:39,755 DEBUG: org.springframework.security.web.util.matcher.AntPathRequestMatcher - Checking match of request : '/user/login'; against '/user/login'
2016-02-27 07:33:39,755 DEBUG: org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter - Request is to process authentication
2016-02-27 07:33:39,755 DEBUG: org.springframework.security.authentication.ProviderManager - Authentication attempt using org.springframework.security.authentication.dao.DaoAuthenticationProvider
2016-02-27 07:33:39,912 DEBUG: org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy - Delegating to org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy@5a8bee5b
2016-02-27 07:33:39,912 DEBUG: org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter - Authentication request failed: org.springframework.security.web.authentication.session.SessionAuthenticationException: Maximum sessions of 1 for this principal exceeded
2016-02-27 07:33:39,912 DEBUG: org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter - Updated SecurityContextHolder to contain null Authentication
2016-02-27 07:33:39,912 DEBUG: org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter - Delegating to authentication failure handler net.kear.recipeorganizer.security.AuthenticationFailureHandler@60f1e1ea
2016-02-27 07:33:39,912 DEBUG: org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices - Interactive login attempt was unsuccessful.
2016-02-27 07:33:39,912 DEBUG: org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices - Cancelling cookie
2016-02-27 07:33:39,912 INFO : net.kear.recipeorganizer.security.AuthenticationFailureHandler - onAuthenticationFailure
2016-02-27 07:33:39,912 DEBUG: net.kear.recipeorganizer.security.AuthenticationFailureHandler - onAuthenticationFailure exception class: class org.springframework.security.web.authentication.session.SessionAuthenticationException
2016-02-27 07:33:39,943 DEBUG: net.kear.recipeorganizer.security.AuthenticationFailureHandler - onAuthenticationFailure msg: SessionAuthenticationException: Maximum sessions of 1 for this principal exceeded
2016-02-27 07:33:39,959 DEBUG: net.kear.recipeorganizer.security.AuthenticationFailureHandler - Redirecting to /user/loginError
2016-02-27 07:33:39,959 DEBUG: org.springframework.security.web.DefaultRedirectStrategy - Redirecting to '/recipeorganizer/user/loginError'

*/



