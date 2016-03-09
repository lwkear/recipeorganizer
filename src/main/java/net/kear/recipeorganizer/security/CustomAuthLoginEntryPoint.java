package net.kear.recipeorganizer.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.RedirectUrlBuilder;

public class CustomAuthLoginEntryPoint extends LoginUrlAuthenticationEntryPoint {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private PortResolver portResolver = new PortResolverImpl();
	private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
	private RequestCache requestCache = new HttpSessionRequestCache();
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private String joinPageUrl;

	public CustomAuthLoginEntryPoint(String loginFormUrl) {
		super(loginFormUrl);
	}
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		logger.debug("commence");
		
		String redirectUrl = null;
		
		if (!StringUtils.isBlank(joinPageUrl)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth == null || trustResolver.isAnonymous(auth)) {
				SavedRequest savedRequest = requestCache.getRequest(request, response);
				if (savedRequest != null)
					redirectUrl = savedRequest.getRedirectUrl();
				
				if (!StringUtils.isBlank(redirectUrl) && redirectUrl.indexOf("viewRecipe") > 0) {
					String joinPageUrl = buildRedirectUrlToJoinPage(request);
					logger.debug("Redirecting to '" + joinPageUrl + "'");
					redirectStrategy.sendRedirect(request, response, joinPageUrl);
					return;
				}				
			}
		}
		
		super.commence(request, response, authException);
	}

	protected String buildRedirectUrlToJoinPage(HttpServletRequest request) {
	
		int serverPort = portResolver.getServerPort(request);
		String scheme = request.getScheme();
	
		RedirectUrlBuilder urlBuilder = new RedirectUrlBuilder();
	
		urlBuilder.setScheme(scheme);
		urlBuilder.setServerName(request.getServerName());
		urlBuilder.setPort(serverPort);
		urlBuilder.setContextPath(request.getContextPath());
		urlBuilder.setPathInfo(joinPageUrl);
		
		return urlBuilder.getUrl();
	}

	public void setJoinPage(String joinPageUrl) {
		this.joinPageUrl = joinPageUrl;
	}
}
