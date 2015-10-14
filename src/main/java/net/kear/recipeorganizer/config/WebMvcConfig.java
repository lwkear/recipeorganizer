package net.kear.recipeorganizer.config;

import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = {"net.kear.recipeorganizer"})
@Configuration
@PropertySource("classpath:email.properties")
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Autowired
    private Environment env;	
	
    public WebMvcConfig() {
        super();
    }

	@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
    
	/*** resource location configuration ***/
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/").setCachePeriod(31556926);
    }
	
	/*** JSON configuration ***/
	//required in order to return a String as a JSON response to an AJAX method;
	//by default the first converter in the array returns the String as text/javascript, not JSON, so removing
	//the Jasckson converter from its place later in the list and adding it back as the first converter seems
	//to fix this problem 
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.remove(msgConverter());
        converters.add(0, msgConverter());
	}

	@Bean
    public MappingJackson2HttpMessageConverter msgConverter() {
		return new MappingJackson2HttpMessageConverter();
    }
	
	/*** view configuration ***/
	@Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    //this is an easy way to avoid creating a .GET method for every single page;
	//works best if there is little content on the page, e.g., error pages
	@Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        super.addViewControllers(registry);
        registry.addViewController("/errors/expiredToken.html");
        registry.addViewController("/errors/invalidToken.html");
    }

	/*** file upload configuration ***/
	@Bean
    public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setMaxUploadSize(20971520);
		return resolver;
    }
	
	/*** validation and i18n message configuration ***/
	@Bean
	public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource resource = new ReloadableResourceBundleMessageSource();
        resource.setBasenames("classpath:messages", "classpath:messages_fr");
        resource.setDefaultEncoding("UTF-8");
        //resource.setCacheSeconds(0);
        //resource.setFallbackToSystemLocale(false);
        return resource;
    }
	
	@Bean
    public CookieLocaleResolver localeResolver() {
		CookieLocaleResolver resolver = new CookieLocaleResolver();
		resolver.setDefaultLocale(new Locale("en"));
		return resolver;
	}
	
	@Bean
	public LocaleChangeInterceptor localeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("lang");
		return interceptor;
	}

    public void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(localeInterceptor());
    }
	
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
	
    /*** email configuration ***/
	@Bean
    public JavaMailSenderImpl javaMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(env.getProperty("smtp.host"));
        mailSenderImpl.setPort(env.getProperty("smtp.port", Integer.class));
        mailSenderImpl.setProtocol(env.getProperty("smtp.protocol"));
        mailSenderImpl.setUsername(env.getProperty("smtp.username"));
        mailSenderImpl.setPassword(env.getProperty("smtp.password"));
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", true);
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        return mailSenderImpl;
    }
	
	/*@Override
	protected void configureMessageConverters(
	        List<HttpMessageConverter<?>> converters) {
	    // put the jackson converter to the front of the list so that application/json content-type strings will be treated as JSON
	    converters.add(new MappingJackson2HttpMessageConverter());
	    // and probably needs a string converter too for text/plain content-type strings to be properly handled
	    converters.add(new StringHttpMessageConverter());
	}*/
}
