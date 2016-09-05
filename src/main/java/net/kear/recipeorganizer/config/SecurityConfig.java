package net.kear.recipeorganizer.config;

//import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.persistence.service.DatabaseStatusService;
import net.kear.recipeorganizer.security.AccessDeniedErrorHandler;
import net.kear.recipeorganizer.security.AuthenticationFailureHandler;
import net.kear.recipeorganizer.security.CustomAuthLoginEntryPoint;
import net.kear.recipeorganizer.security.CustomHttpSessionSecurityContextRepository;
import net.kear.recipeorganizer.security.CustomLogoutSuccessHandler;
import net.kear.recipeorganizer.security.DatabaseConnectionConfigurer;
import net.kear.recipeorganizer.security.DatabaseConnectionFilter;
import net.kear.recipeorganizer.security.HttpHeadFilter;
import net.kear.recipeorganizer.security.LoginSuccessHandler;
import net.kear.recipeorganizer.security.RedirectInvalidSession;
import net.kear.recipeorganizer.security.RememberMeSuccessHandler;
import net.kear.recipeorganizer.security.UserSecurityService;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses=UserSecurityService.class)
@EnableGlobalMethodSecurity( prePostEnabled = true )
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	//used for persistent rememberMe token
	//@Autowired
	//DataSource dataSource;
	@Autowired 
	CharacterEncodingFilter encodingFilter;
	@Autowired
	private DatabaseStatusService databaseStatusService;	
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, UserSecurityService userSecurityService) throws Exception {
		auth
		.userDetailsService(userSecurityService)
		.passwordEncoder(encoder());
	}

	@Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	public HttpHeadFilter httpHeadFilter() {
		return new HttpHeadFilter();
	}
	
	@Bean
	DatabaseConnectionFilter dbConnectionFilter() throws Exception {
		DatabaseConnectionFilter filter = new DatabaseConnectionFilter(databaseStatusService);
		filter.setTargetUrl("/systemError");
		return filter;
	}
	
	@Bean
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl roleHier = new RoleHierarchyImpl();
		roleHier.setHierarchy("ADMIN > EDITOR and EDITOR > AUTHOR and AUTHOR > GUEST and GUEST > ROLE_ANONYMOUS");
		return roleHier;		
	}
	
	/*@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setCreateTableOnStartup(false);
		db.setDataSource(dataSource);
		return db;
	}*/

	@Bean
	public SessionManagementBeanPostProcessor sessionManagementBeanPostProcessor() {
		return new SessionManagementBeanPostProcessor();
	}

	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return new AuthenticationFailureHandler();
	}
	
	@Bean
	public AccessDeniedErrorHandler accessDeniedHandler() {
		return new AccessDeniedErrorHandler(); 
	}
	
	@Bean
	public LoginSuccessHandler loginSuccessHandler() {
		LoginSuccessHandler handler = new LoginSuccessHandler();
		handler.setDefaultTargetUrl("/user/dashboard");
		return handler;
	}
	
	@Bean
	public RememberMeSuccessHandler rememberMeSuccessHandler() {
		RememberMeSuccessHandler handler = new RememberMeSuccessHandler();
		handler.setDefaultTargetUrl("/user/dashboard");
		return handler;
	}
	
	@Bean
	public CustomLogoutSuccessHandler logoutSuccessHandler() {
		CustomLogoutSuccessHandler handler = new CustomLogoutSuccessHandler();
		return handler;
	}
	
	@Bean
	public CustomAuthLoginEntryPoint customAuthLoginEntryPoint() {
		CustomAuthLoginEntryPoint entryPoint = new CustomAuthLoginEntryPoint("/user/login");
		entryPoint.setJoinPage("/user/join");
		return entryPoint;
	}
	
	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}
	
	@Bean
	public CustomHttpSessionSecurityContextRepository contextRepository() {
		return new CustomHttpSessionSecurityContextRepository();
	}

	//sets the hierarchy of roles
	private SecurityExpressionHandler<FilterInvocation> secExpressionHandler() {
        DefaultWebSecurityExpressionHandler defaultHandler = new DefaultWebSecurityExpressionHandler();
        defaultHandler.setRoleHierarchy(roleHierarchy());
        return defaultHandler;
    }
	
	@Override
	public void configure(WebSecurity web) throws Exception
	{
		web
		.ignoring()
			.antMatchers("/resources/**", "/robots.txt", "/favicon.ico")
		;
	}

	//Note: the regexMatcher is required because signup uses an AJAX call to check if the email is already registered and
	//antMatcher does not handle the /?{} parameter to the url
	//Note: use hasAuthority instead of hasRole, otherwise the role is prepended with ROLE_
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
    	http
    	.securityContext()
    		.securityContextRepository(contextRepository())
    		.and()
    	.headers()
    		.frameOptions().sameOrigin()
    		.and()
    	.addFilterBefore(encodingFilter, CsrfFilter.class)
    	.addFilterAfter(httpHeadFilter(), FilterSecurityInterceptor.class)
    	.authorizeRequests()
			.antMatchers("/", "/home", "/about", "/contact",  "/faq", "/thankyou", "/betatest", "/technical", "/policies", "/sysmaint", "/whatsnew").permitAll()
			.antMatchers("/submitSearch", "/searchResults", "/system*", "/error", "/message", "/getSessionTimeout", "/expiredSession", "/accessDenied").permitAll()
    		.antMatchers("/lookupUser", "/user/login**", "/user/signup**", "/user/resetPassword", "/user/newPassword", "/user/join").permitAll()
    		.antMatchers("/user/fatalError", "/user/tokenError", "/user/optout**", "/user/resendRegistrationToken", "/user/resendPasswordToken").permitAll()
    		.antMatchers("/recipe/photo**").permitAll()
    		.regexMatchers("/confirmRegistration.*", "/confirmPassword.*", "/questions/.*").permitAll()
    		.antMatchers("user/changeAccountLevel", "user/upgradeAccount", "user/newMember", "/recipe/favorites", "/recipe/browseRecipes", "/recipe/categoryRecipes").hasAuthority(Role.TYPE_GUEST)
    		.regexMatchers("/recipe/viewRecipe/.*", "/report/getHtmlRpt/.*", "/report/getPdfRpt/.*", "/recipe/categoryRecipes/.*").hasAuthority(Role.TYPE_GUEST)
    		.antMatchers("/recipe", "/recipe/**", "/recipe/recipeList").hasAuthority(Role.TYPE_AUTHOR)
    		.antMatchers("/admin/approval", "/admin/approveRecipe", "/admin/comments", "/admin/ingredients").hasAuthority(Role.TYPE_EDITOR)
    		.antMatchers("/admin/**").hasAuthority(Role.TYPE_ADMIN)
    		.anyRequest().authenticated()
			.expressionHandler(secExpressionHandler())
			.and()
		.formLogin()
			.loginPage("/user/login")
			.permitAll()
			.failureHandler(authenticationFailureHandler())
			.successHandler(loginSuccessHandler())
			.and()
		.logout()
			.deleteCookies("JSESSIONID")
			.invalidateHttpSession(true)
			.logoutSuccessHandler(logoutSuccessHandler())
			.and()
		.exceptionHandling()
			.accessDeniedHandler(accessDeniedHandler())
			.authenticationEntryPoint(customAuthLoginEntryPoint())
			.and()
		.rememberMe()
			.authenticationSuccessHandler(rememberMeSuccessHandler())
			.key("recipeOrganizerSecretKey")
			.tokenValiditySeconds(60 * 60 * 24 * 14)	//2 weeks
			.rememberMeParameter("rememberMe")
			//.tokenRepository(persistentTokenRepository()) //rememberMe
			.and()
    	.sessionManagement()
    		.invalidSessionUrl("/user/login")
    		//.sessionAuthenticationErrorUrl("/expiredSession")
    		.maximumSessions(1)
    		.sessionRegistry(sessionRegistry())
    		.expiredUrl("/expiredSession")
    		//.maxSessionsPreventsLogin(false)	//when enabled it prevents a user from logging in before the session expires
    	;
    	
    	http.apply(new DatabaseConnectionConfigurer<HttpSecurity>(dbConnectionFilter()));
    }
	
	//replaces the default SimpleRedirectInvalidSessionStrategy and allows for anonymous users to browse w/o getting an invalid session error 
	protected static class SessionManagementBeanPostProcessor implements BeanPostProcessor {

	    @Override
	    public Object postProcessBeforeInitialization(Object bean, String beanName) {
	        if (bean instanceof SessionManagementFilter) {
	            SessionManagementFilter filter = (SessionManagementFilter) bean;
	            filter.setInvalidSessionStrategy(new RedirectInvalidSession("/user/login"));
	        }
	        return bean;
	    }
	    
	    @Override
	    public Object postProcessAfterInitialization(Object bean, String beanName) {
	        return bean;
	    }
	}
}

/*
CsrfFilter

When I login on two different browsers, `ConcurrentSessionFilter` correctly invalidates the session and redirects to the expiredURL.  However, when "/expiredSession" goes through the security filter chain it gets caught in the `SessionManagementFilter` because the session in the request is no longer valid. This redirects the user to the login screen

w/o rememberMe
2016-03-02 08:22:42,890 DEBUG: org.springframework.security.core.session.SessionRegistryImpl - Registering session 3C71392ABA556D50734501B54E404CA7, for principal User [id=3, firstName=Larry, lastName=Kear, email=lwk@outlook.com, password=$2a$10$dWsT1NSw68Shy.1Kxf5pMOtwfYbliTz2qCUQrOEn234QFDJ52HJZq, enabled=1, tokenExpired=0, locked=0, accountExpired=0, dateAdded=2015-09-03, lastLogin=2016-03-02, passwordExpired=0, loggedIn=false, numRecipes=0, role=Role [id=21, name=ADMIN, description=Administrator], userProfile=UserProfile [id=5, city=Evanston, state=Illinois, age=4, interests=Cooking and baking. Learning to stir-fry.  Love baking cookies and cakes.  ATK is a favorite source for recipes.  Chicken is the best.  I'm stuffed from Thanksgiving!!!  Love my pumpkin bundt cake, avatar=2015 Lamium.JPG]]
checked rememberMe
2016-03-02 08:26:33,550 DEBUG: org.springframework.security.core.session.SessionRegistryImpl - Registering session 0D3F1D35EB01ECE5B699562F5CB52BA2, for principal User [id=3, firstName=Larry, lastName=Kear, email=lwk@outlook.com, password=$2a$10$dWsT1NSw68Shy.1Kxf5pMOtwfYbliTz2qCUQrOEn234QFDJ52HJZq, enabled=1, tokenExpired=0, locked=0, accountExpired=0, dateAdded=2015-09-03, lastLogin=2016-03-02, passwordExpired=0, loggedIn=false, numRecipes=0, role=Role [id=21, name=ADMIN, description=Administrator], userProfile=UserProfile [id=5, city=Evanston, state=Illinois, age=4, interests=Cooking and baking. Learning to stir-fry.  Love baking cookies and cakes.  ATK is a favorite source for recipes.  Chicken is the best.  I'm stuffed from Thanksgiving!!!  Love my pumpkin bundt cake, avatar=2015 Lamium.JPG]]
2016-03-02 08:26:33,561 DEBUG: org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices - Added remember-me cookie for user 'lwk@outlook.com', expiry: 'Wed Mar 16 09:26:33 CDT 2016'
close browser/open app
2016-03-02 08:30:59,799 DEBUG: org.springframework.security.web.FilterChainProxy - / at position 11 of 15 in additional filter chain; firing Filter: 'RememberMeAuthenticationFilter'
2016-03-02 08:30:59,799 DEBUG: org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices - Remember-me cookie detected
2016-03-02 08:30:59,816 DEBUG: org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices - Remember-me cookie accepted
2016-03-02 08:30:59,816 DEBUG: org.springframework.security.authentication.ProviderManager - Authentication attempt using org.springframework.security.authentication.RememberMeAuthenticationProvider
2016-03-02 08:30:59,817 DEBUG: org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter - SecurityContextHolder populated with remember-me token: 'org.springframework.security.authentication.RememberMeAuthenticationToken@9182a08f: Principal: User [id=3, firstName=Larry, lastName=Kear, email=lwk@outlook.com, password=$2a$10$dWsT1NSw68Shy.1Kxf5pMOtwfYbliTz2qCUQrOEn234QFDJ52HJZq, enabled=1, tokenExpired=0, locked=0, accountExpired=0, dateAdded=2015-09-03, lastLogin=2016-03-02, passwordExpired=0, loggedIn=false, numRecipes=0, role=Role [id=21, name=ADMIN, description=Administrator], userProfile=UserProfile [id=5, city=Evanston, state=Illinois, age=4, interests=Cooking and baking. Learning to stir-fry.  Love baking cookies and cakes.  ATK is a favorite source for recipes.  Chicken is the best.  I'm stuffed from Thanksgiving!!!  Love my pumpkin bundt cake, avatar=2015 Lamium.JPG]]; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@b364: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: null; Granted Authorities: ADMIN'
2016-03-02 08:30:59,817 INFO : net.kear.recipeorganizer.security.RememberMeSuccessHandler - onAuthenticationSuccess: name=lwk@outlook.com
*/