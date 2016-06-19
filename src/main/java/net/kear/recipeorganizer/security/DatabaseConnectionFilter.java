package net.kear.recipeorganizer.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.service.DatabaseStatusService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

public class DatabaseConnectionFilter extends GenericFilterBean {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String targetUrl = "/";
	private DatabaseStatusService databaseStatusService;
	private RememberMeServices rememberMeServices;
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Autowired
	public DatabaseConnectionFilter(DatabaseStatusService databaseStatusService) {
		this.databaseStatusService = databaseStatusService;
	}
	
	public void setRememberMeServices(RememberMeServices rememberMeServices) {
		Assert.notNull(rememberMeServices, "rememberMeServices cannot be null");
		this.rememberMeServices = rememberMeServices;
	}
	
	public void setTargetUrl(String targetUrl) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(targetUrl),
				"target must start with '/' or with 'http(s)'");
		this.targetUrl = targetUrl;
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String uri = request.getRequestURI();
		logger.debug("Checking request URI against targetUrl: " + uri + " vs. " + targetUrl);		
		if (!uri.endsWith(targetUrl)) {
			try {
				databaseStatusService.checkStatus();
			} catch (Exception ex) {
				logger.debug("getConnection exception: ", ex);
				
				databaseStatusService.setDatabaseStatus(false);
				
				//must cancel the rememberMe cookie if it exists;
				//otherwise displaying the error page will fail when the filter tries to authenticate the cookie 
				rememberMeServices.loginFail(request, response);
				
				connectionFail(request, response);
				return;
			}
			
			databaseStatusService.setDatabaseStatus(true);
		}
		
		chain.doFilter(request, response);
	}
	
	public final void connectionFail(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		logger.debug("Database connection attempt was unsuccessful.");
		onconnectionFail(request, response);
	}

	protected void onconnectionFail(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		redirectStrategy.sendRedirect(request, response, targetUrl);
	}	
}
