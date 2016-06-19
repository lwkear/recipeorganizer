package net.kear.recipeorganizer.security;

import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.util.Assert;

public final class DatabaseConnectionConfigurer<H extends HttpSecurityBuilder<H>>
	extends AbstractHttpConfigurer<DatabaseConnectionConfigurer<H>, H> {

	DatabaseConnectionFilter dbConnectionFilter;
	
	public DatabaseConnectionConfigurer(DatabaseConnectionFilter dbConnectionFilter) {
		Assert.notNull(dbConnectionFilter, "dbConnectionFilter cannot be null");
		this.dbConnectionFilter = dbConnectionFilter; 
	}
	
	public void init(H http) throws Exception {}
	
	public void configure(H http) throws Exception {
		dbConnectionFilter.setRememberMeServices(http.getSharedObject(RememberMeServices.class));
		http.addFilterBefore(dbConnectionFilter, WebAsyncManagerIntegrationFilter.class);	
	}
}