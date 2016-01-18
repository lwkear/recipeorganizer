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
import org.springframework.security.web.session.SessionManagementFilter;

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
		handler.setTargetUrlParameter("/thankyou");
		return handler;
	}
	
	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
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
	//TODO: SECURITY: look into putting URLs with their roles into a DB table?
	//TODO: SECURITY: consider securing every URL in the app to reduce unauth access
	//@SuppressWarnings("unchecked")
	@Override
	protected void configure(HttpSecurity http) throws Exception {
    	http
    	//.csrf()
    		//.disable()
    	.headers()
    		.frameOptions().sameOrigin()
    		.and()
    	.authorizeRequests()
			//.antMatchers("/", "/home", "/about", "/faq", "/contact", "/submitsearch", "/searchresults").permitAll()
			.antMatchers("/", "/home", "/about", "/contact", "/submitsearch", "/searchresults").permitAll()
    		.antMatchers("/thankyou", "/user/login**", "/user/signup**", "/user/resetPassword").permitAll()
			.antMatchers("/messages/**", "/errors/**", "/getSessionTimeout", "/lookupUser").permitAll()
			.antMatchers("/user/forgotPassword", "/user/newPassword", "/recipe/photo**").permitAll()
			.regexMatchers("/home/.*", "/user/signup/.*", "/confirmRegistration.*", "/confirmPassword.*").permitAll()
			.regexMatchers("/user/resendRegistrationToken.*", "/user/resendPasswordToken.*").permitAll()
			.regexMatchers("/errors/expiredToken.*","/errors/invalidToken.*").permitAll()			
			.antMatchers("/recipe/listRecipes", "/setSessionTimeout").hasAuthority("GUEST")
			.antMatchers("/user/profile", "/user/dashboard", "/user/changePassword**", "/user/avatar/**").hasAuthority("GUEST")
			.regexMatchers("user/avatar/.*", "/recipe/viewRecipe/.*").hasAuthority("GUEST")
			.regexMatchers("/recipe/getRecipeCount/.*","/report/gethtmlrpt/.*", "/recipe/editRecipe/.*").hasAuthority("AUTHOR")
			.antMatchers("/recipe/addRecipe").hasAuthority("AUTHOR")
			.antMatchers("/admin/**","/admin/deleteUser/.*","/admin/getUser/.*", "/admin/updateUser").hasAuthority("ADMIN")
			.antMatchers("/faq" ).hasAuthority("ADMIN")	//test accessed denied
			.anyRequest().authenticated()
			//.anyRequest().permitAll()	//comment out to test if above configs are causing a problem
			.expressionHandler(secExpressionHandler())
			.and()
		.formLogin()
			.loginPage("/user/login")
			.failureUrl("/user/login?err=1")
			.permitAll()
			.successHandler(loginSuccessHandler())
			.and()
		.logout()
			.deleteCookies("JSESSIONID")
			.invalidateHttpSession(true)
			.logoutSuccessUrl("/thankyou")
			.logoutSuccessHandler(logoutSuccessHandler())
			.and()
		.exceptionHandling()
			.accessDeniedPage("/accessDenied")	//403
			.and()
		.rememberMe()
			.authenticationSuccessHandler(rememberMeSuccessHandler())
			.key("recipeOrganizer-rmkey")
			.tokenValiditySeconds(60 * 60 * 24 * 14)	//2 weeks
			.rememberMeParameter("rememberMe")
			.and()
    	.sessionManagement()
    		.sessionAuthenticationErrorUrl("/authenticationError")	//402
    		.maximumSessions(1)
    		.sessionRegistry(sessionRegistry())
    		.maxSessionsPreventsLogin(true)
    		.expiredUrl("/expiredSession")
    	;
    }
	
	//.tokenRepository(persistentTokenRepository()) //rememberMe	
	
	//replaces the default SimpleRedirectInvalidSessionStrategy and allows for anonymous users to browse w/o getting an invalid session error 
	protected static class SessionManagementBeanPostProcessor implements BeanPostProcessor {

	    @Override
	    public Object postProcessBeforeInitialization(Object bean, String beanName) {
	        if (bean instanceof SessionManagementFilter) {
	            SessionManagementFilter filter = (SessionManagementFilter) bean;
	            filter.setInvalidSessionStrategy(new RedirectInvalidSession("/invalidSession"));
	        }
	        return bean;
	    }

	    @Override
	    public Object postProcessAfterInitialization(Object bean, String beanName) {
	        return bean;
	    }
	}
	
	//sets the hierarchy of roles
	private SecurityExpressionHandler<FilterInvocation> secExpressionHandler() {
        DefaultWebSecurityExpressionHandler defaultHandler = new DefaultWebSecurityExpressionHandler();
        defaultHandler.setRoleHierarchy(roleHierarchy());
        return defaultHandler;
    }
}