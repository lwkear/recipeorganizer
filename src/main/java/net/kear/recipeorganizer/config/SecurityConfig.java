package net.kear.recipeorganizer.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.SessionManagementFilter;

import net.kear.recipeorganizer.security.UserSecurityService;
import net.kear.recipeorganizer.util.AuthCookie;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses=UserSecurityService.class)
/*@ComponentScan(basePackageClasses= {UserSecurityService.class,AuthCookie.class})*/
@EnableGlobalMethodSecurity( prePostEnabled = true )
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	DataSource dataSource;	
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsService userDetailsService) throws Exception {
		auth
		.userDetailsService(userDetailsService)
		.passwordEncoder(encoder());
	}
	
	/*@Bean
	public AuthCookie authCookie() {
		return new AuthCookie();
	}*/

	@Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl roleHier = new RoleHierarchyImpl();
		roleHier.setHierarchy("ADMIN > EDITOR and EDITOR > AUTHOR and AUTHOR > GUEST and GUEST > ROLE_ANONYMOUS");
		return roleHier;		
	}
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setCreateTableOnStartup(false);
		db.setDataSource(dataSource);
		return db;
	}
	
	@Bean
	public SessionRegistry sessionRegistry(){
		return new SessionRegistryImpl();
	}
	
	@Bean
	public ConcurrentSessionControlAuthenticationStrategy concurrentSessionControlStrategy() {
		ConcurrentSessionControlAuthenticationStrategy concurrentSessionControlStrategy  = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
		concurrentSessionControlStrategy.setMaximumSessions(1);
		concurrentSessionControlStrategy.setExceptionIfMaximumExceeded(true);
		return concurrentSessionControlStrategy;
	}
	
	@Bean
	public ConcurrentSessionFilter concurrentSessionFilter() {
		ConcurrentSessionFilter sessionFilter = new ConcurrentSessionFilter(sessionRegistry(), "/errors/expiredSession");
		return sessionFilter;
	}
	
	@Bean
	public SecurityContextPersistenceFilter securityContextPersistenceFilter() {
		SecurityContextPersistenceFilter securityContextPersistenceFilter = new SecurityContextPersistenceFilter(httpSessionSecurityContextRepository());
		return securityContextPersistenceFilter;
	}
	
	@Bean
	public HttpSessionSecurityContextRepository httpSessionSecurityContextRepository() {
		return new HttpSessionSecurityContextRepository();
	}
	
	@Bean
	public SessionManagementFilter sessionManagementFilter() {
		SessionManagementFilter sessionManagementFilter = new SessionManagementFilter(httpSessionSecurityContextRepository(), concurrentSessionControlStrategy());
		sessionManagementFilter.setInvalidSessionStrategy(new RedirectInvalidSession("/errors/invalidSession"));
		sessionManagementFilter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/errors/402"));
		return sessionManagementFilter;
	}
	
	@Bean
	public LoginSuccessHandler loginSuccessHandler() {
		return new LoginSuccessHandler();
	}

	@Bean
	public RememberMeSuccessHandler rememberMeSuccessHandler() {
		return new RememberMeSuccessHandler();
	}
	
	@Bean
	public CustomLogoutSuccessHandler logoutSuccessHandler() {
		return new CustomLogoutSuccessHandler();
	}

	@Override
	public void configure(WebSecurity web) throws Exception
	{
		web
		.ignoring()
			.antMatchers("/resources/**")
			//.antMatchers("/", "/home", "/about", "/login**", "/thankyou", "/user/signup**", "/errors/**", "/getSessionTimeout", "/setSessionTimeout")
			;
	}
	
    //Note: the regexMatcher is required because signup uses an AJAX call to check if the email is already registered and
	//antMatcher does not handle the /?{} parameter to the url
	//Note: use hasAuthority instead of hasRole, otherwise the role is prepended with ROLE_
	//TODO: SECURITY: look into putting URLs with their roles into a DB table?
	//TODO: SECURITY: consider securing every URL in the app to reduce unauth access
	@SuppressWarnings("unchecked")
	@Override
	protected void configure(HttpSecurity http) throws Exception {
    	http
    	//.headers()
    	//	.and()
    	//.anonymous()
    	//	.and()
    	.authorizeRequests()
			.antMatchers("/", "/home", "/about", "/login**", "/thankyou", "/user/signup**", "/errors/**", "/getSessionTimeout", "/setSessionTimeout").permitAll() 
			//.antMatchers("/**", "/login**", "/user/signup**", "/errors/**").permitAll()
			.regexMatchers("/user/signup/.*").permitAll()
			.antMatchers("/recipe/listRecipes*").hasAuthority("GUEST")
			.antMatchers("/recipe/addRecipe*").hasAuthority("AUTHOR")
			.antMatchers("/admin/**").hasAuthority("ADMIN")
			.anyRequest().authenticated()
			//.anyRequest().permitAll()	//comment out to test if above configs are causing a problem
			.expressionHandler(secExpressionHandler())
			.and()
		.formLogin()
			.loginPage("/login")
			.failureUrl("/login?err=1")
			.permitAll()
			.successHandler(loginSuccessHandler())
			.and()
		.logout()
			.logoutSuccessUrl("/thankyou")
			.deleteCookies( "JSESSIONID" )
			.invalidateHttpSession(false)
			.logoutSuccessHandler(logoutSuccessHandler())
			.and()
		.exceptionHandling()
			.accessDeniedPage("/errors/403")
			.and()
		.rememberMe()
			.authenticationSuccessHandler(rememberMeSuccessHandler())
			.key("recipeOrganizer")
			.tokenRepository(persistentTokenRepository())
			.tokenValiditySeconds(180)		//.tokenValiditySeconds(1209600)
			.rememberMeParameter("rememberMe")
		;

    	/*http
    	.sessionManagement()
    		.sessionAuthenticationErrorUrl("/errors/402")
    		.invalidSessionUrl("/errors/invalidSession")
    		.maximumSessions(1)
    		.maxSessionsPreventsLogin(true)
    		.expiredUrl("/errors/expiredSession")
    		//.and()
   		//.sessionFixation().changeSessionId()
   		;*/
    		
    		//.migrateSession();	//not necessary - no HTTP/HTTPS issues
    	
    	http.removeConfigurer(SessionManagementConfigurer.class);
    	http.addFilter(sessionManagementFilter());
		http.addFilter(concurrentSessionFilter());
		
	}

	private SecurityExpressionHandler<FilterInvocation> secExpressionHandler() {
        DefaultWebSecurityExpressionHandler defaultHandler = new DefaultWebSecurityExpressionHandler();
        defaultHandler.setRoleHierarchy(roleHierarchy());
        return defaultHandler;
    }
}


