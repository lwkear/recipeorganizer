package net.kear.recipeorganizer.config;

import java.util.EnumSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionTrackingMode;

import net.kear.recipeorganizer.listener.PasswordResetListener;
import net.kear.recipeorganizer.listener.RegistrationListener;
import net.kear.recipeorganizer.listener.SessionListener;

import org.springframework.core.Conventions;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        
    	AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(WebMvcConfig.class, RepositoryConfig.class, SecurityConfig.class, WebFlowConfig.class);

    	AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
    	applicationContext.addApplicationListener(new RegistrationListener());
    	applicationContext.addApplicationListener(new PasswordResetListener());

    	DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
    	dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);

    	String dispatcherName = Conventions.getVariableName(dispatcherServlet);
    	ServletRegistration.Dynamic dispatcher = servletContext.addServlet(dispatcherName, dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
        MultipartConfig config = new MultipartConfig();
        dispatcher.setMultipartConfig(config.getMultipartConfig());
        
        servletContext.addListener(new ContextLoaderListener(rootContext));
    	servletContext.addListener(new HttpSessionEventPublisher());
        servletContext.addListener(new SessionListener());
    	servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
    }
}


/*
/*import java.util.EnumSet;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionTrackingMode;

import net.kear.recipeorganizer.listener.PasswordResetListener;
import net.kear.recipeorganizer.listener.RegistrationListener;
import net.kear.recipeorganizer.listener.SessionListener;

import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
    	//return new Class<?>[] { };
    	return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { WebMvcConfig.class, RepositoryConfig.class, SecurityConfig.class, WebFlowConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return new Filter[] {characterEncodingFilter};
    }

	@Override
	protected WebApplicationContext createRootApplicationContext() {
		AnnotationConfigWebApplicationContext rootAppContext = new AnnotationConfigWebApplicationContext();
		return rootAppContext;
	}
    
    @Override
    protected WebApplicationContext createServletApplicationContext() {
    	AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
    	applicationContext.addApplicationListener(new RegistrationListener());
    	applicationContext.addApplicationListener(new PasswordResetListener());
    	return applicationContext;
    }
    
    @Override
    protected void registerDispatcherServlet(ServletContext servletContext) {
    	servletContext.addListener(new ContextLoaderListener(createServletApplicationContext()));
        servletContext.addListener(new HttpSessionEventPublisher());
        servletContext.addListener(new SessionListener());
        servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
    	super.registerDispatcherServlet(servletContext);
    }
    
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
    	registration.setMultipartConfig(new MultipartConfigElement("G:\\Temp", 1024*1024*5, 1024*1024*5*5, 1024*1024));
    }
}*/
