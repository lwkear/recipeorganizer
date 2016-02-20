package net.kear.recipeorganizer.config;

import javax.sql.DataSource;

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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.security.AccessDeniedErrorHandler;
import net.kear.recipeorganizer.security.AuthenticationFailureHandler;
import net.kear.recipeorganizer.security.CustomLogoutSuccessHandler;
import net.kear.recipeorganizer.security.LoginSuccessHandler;
import net.kear.recipeorganizer.security.RedirectInvalidSession;
import net.kear.recipeorganizer.security.RememberMeSuccessHandler;
import net.kear.recipeorganizer.security.UserSecurityService;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses=UserSecurityService.class)
@EnableGlobalMethodSecurity( prePostEnabled = true )
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	DataSource dataSource;
	@Autowired 
	CharacterEncodingFilter encodingFilter;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsService userDetailsService) throws Exception {
		auth
		.userDetailsService(userDetailsService)
		.passwordEncoder(encoder());
	}

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
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
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
			.antMatchers("/resources/**")
		;
	}

	//Note: the regexMatcher is required because signup uses an AJAX call to check if the email is already registered and
	//antMatcher does not handle the /?{} parameter to the url
	//Note: use hasAuthority instead of hasRole, otherwise the role is prepended with ROLE_
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
    	http
    	//.csrf().disable()
    	.addFilterBefore(encodingFilter, CsrfFilter.class)
    	.headers()
    		.frameOptions().sameOrigin()
    		.and()
    	.authorizeRequests()
			.antMatchers("/", "/home", "/about", "/contact",  "/faq", "/thankyou", "/technical", "/policies", "/test/testpage").permitAll()
			.antMatchers("/submitsearch", "/searchresults", "/system*", "/error", "/message").permitAll()
    		.antMatchers("/user/login**", "/user/signup**", "/user/forgotPassword", "/user/fatalError", "/lookupUser").permitAll()
    		.antMatchers("/recipe/photo**").permitAll()
    		.regexMatchers("/confirmRegistration.*", "/confirmPassword.*").permitAll()
    		.antMatchers("user/account", "/recipe/favorites").hasAuthority(Role.TYPE_GUEST)
    		.regexMatchers("/recipe/viewRecipe/.*", "/report/getHtmlRpt/.*", "/report/getPdfRpt/.*").hasAuthority(Role.TYPE_GUEST)
    		.antMatchers("/recipe", "/recipe/**", "/recipe/recipeList").hasAuthority(Role.TYPE_AUTHOR)
    		.antMatchers("/admin/**").hasAuthority(Role.TYPE_ADMIN)
    		.anyRequest().authenticated()
			//.anyRequest().permitAll()	//comment out to test if above configs are causing a problem
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
			.and()
		.rememberMe()
			.authenticationSuccessHandler(rememberMeSuccessHandler())
			.key("recipeOrganizer-rmkey")
			.tokenValiditySeconds(60 * 60 * 24 * 14)	//2 weeks
			.rememberMeParameter("rememberMe")
			.and()
    	.sessionManagement()
    		.maximumSessions(1)
    		.sessionRegistry(sessionRegistry())
    		.maxSessionsPreventsLogin(true)
    	;
    }
	
	//.tokenRepository(persistentTokenRepository()) //rememberMe	
	
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
.antMatchers("/user/resetPassword", "/user/fatalError").permitAll()
.antMatchers("/getSessionTimeout", "/setSessionTimeout", "/lookupUser", "/errorPage").permitAll()
.antMatchers("/user/forgotPassword", "/user/newPassword", "/recipe/photo**", "/invalidSession").permitAll()
.regexMatchers("/home/.*", "/user/signup/.*", "/confirmRegistration.*", "/confirmPassword.*").permitAll()
.regexMatchers("/user/resendRegistrationToken.*", "/user/resendPasswordToken.*").permitAll()
.regexMatchers("/errors/expiredToken.*","/errors/invalidToken.*").permitAll()			

.antMatchers("/user/profile", "/user/dashboard", "user/account", "user/changeAccount", "/user/changePassword**", "/user/avatar/**").hasAuthority("GUEST")
.regexMatchers("user/avatar/.*", "/recipe/viewRecipe/.*", "recipe/favorites").hasAuthority("GUEST")

.antMatchers("/recipe", "/recipe/listRecipes").hasAuthority("AUTHOR")
.regexMatchers("/report/gethtmlrpt/.*", "/recipe/editRecipe/.*").hasAuthority("AUTHOR")

.antMatchers("/admin/**","/admin/deleteUser/.*","/admin/getUser/.*").hasAuthority("ADMIN")	
.regexMatchers("/recipe/getRecipeCount/.*").hasAuthority("ADMIN")
*/
/*			
ANON
ant
x "/"
x "/home"
x "/about"
x "/contact"
x "/faq"
"/thankyou"
"/systemError"
"/system"
x "user/login"
"user/loginError"
"user/fatalError"
x "user/signup"
x "/lookupUser" - AJAX
x "/submitsearch"	- need to allow for search and search results, but not allow to view the recipes
"/searchresults" - ditto
"/getSessionTimeout" - AJAX
"/setSessionTimeout" - AJAX
regex
"/confirmRegistration.*"
"/confirmPassword.*"

GUEST
ant
"/recipe/favorites"
"/recipe/addFavorite" - AJAX
"/recipe/removeFavorite" - AJAX
"/recipe/recipeMade" - AJAX
"/recipe/recipeNote" - AJAX
"/recipe/recipeComment" - AJAX
regex
"/recipe/viewRecipe/.*"
"/report/getHtmlRpt/.*"
"/report/getPdfRpt/.*"

AUTHOR
ant
"/recipe/listRecipes"
"recipe/getCategories" - AJAX doesn't requrie an antmatch?
"recipe/addIngredient" - AJAX
"recipe/getIngredients" - AJAX
"recipe/getQualifiers" - AJAX
"recipe/getSources" - AJAX
"recipe/getTags" - AJAX
"recipe/lookupRecipeName" - AJAX
"recipe/getRecipeCount" - AJAX; why is this in ADMIN above?
regex
"/recipe/editRecipe/.*"
"recipe/deleteRecipe/.*" or "recipe/deleteRecipe" - AJAX

ADMIN
ant
"/admin/**"
"admin/updateUser"
"/admin/category"
regex
"/admin/deleteUser/.*" - AJAX
"/admin/getUser/.*" - AJAX

*/			
