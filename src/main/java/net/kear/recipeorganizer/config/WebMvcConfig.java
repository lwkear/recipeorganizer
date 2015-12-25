package net.kear.recipeorganizer.config;

import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.webflow.mvc.servlet.FlowHandlerAdapter;
import org.springframework.webflow.mvc.servlet.FlowHandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = {"net.kear.recipeorganizer"})
@Configuration
@PropertySource("classpath:email.properties")
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private Environment env;	

	@Autowired
	private WebFlowConfig webFlowConfig;
	
    public WebMvcConfig() {
        super();
    }

	@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		logger.debug("configureDefaultServletHandling");
        configurer.enable();
    }
    
	/*** resource location configuration ***/
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		logger.debug("addResourceHandlers");
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/").setCachePeriod(31556926);
        registry.addResourceHandler("/reports/**").addResourceLocations("/reports").setCachePeriod(0);
    }
	
	/*** JSON configuration ***/
	//required in order to return a String as a JSON response to an AJAX method;
	//by default the first converter in the array returns the String as text/javascript, not JSON, so removing
	//the Jasckson converter from its place later in the list and adding it back as the first converter seems
	//to fix this problem 
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		logger.debug("extendMessageConverters");
        converters.remove(msgConverter());
        converters.add(0, msgConverter());
	}

	@Bean
	public MappingJackson2HttpMessageConverter msgConverter(){
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
	
		ObjectMapper mapper = new ObjectMapper();
		//register Hibernate4Module to support lazy objects
		mapper.registerModule(new Hibernate4Module());
		
		messageConverter.setObjectMapper(mapper);
		return messageConverter;
	}	
	
	/*** file upload configuration ***/
	@Bean
	public StandardServletMultipartResolver filterMultipartResolver() {
		logger.debug("StandardServletMultipartResolver");
		StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
		return resolver;
	}
	
	/*** view configuration ***/
	@Bean
    public InternalResourceViewResolver viewResolver() {
		logger.debug("InternalResourceViewResolver");
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    //this is an easy way to avoid creating a .GET method for every single page;
	//works best if there is little content on the page, e.g., error pages
	/*@Override
    public void addViewControllers(final ViewControllerRegistry registry) {
		logger.debug("addViewControllers");
        super.addViewControllers(registry);
        registry.addViewController("/recipe/basics.htm");        
        registry.addViewController("/recipe/ingredients.htm");
        registry.addViewController("/recipe/instructions.htm");
        registry.addViewController("/recipe/optional.htm");
        registry.addViewController("/recipe/end.htm");
    }*/

	/*** webflow configuration ***/
	@Bean
	public FlowHandlerMapping flowHandlerMapping() {
		logger.debug("FlowHandlerMapping");
		FlowHandlerMapping handlerMapping = new FlowHandlerMapping();
		handlerMapping.setOrder(-1);
		handlerMapping.setFlowRegistry(this.webFlowConfig.flowRegistry());
		return handlerMapping;
	}

	@Bean
	public FlowHandlerAdapter flowHandlerAdapter() {
		logger.debug("FlowHandlerAdapter");
		FlowHandlerAdapter handlerAdapter = new FlowHandlerAdapter();
		handlerAdapter.setFlowExecutor(this.webFlowConfig.flowExecutor());
		handlerAdapter.setSaveOutputToFlashScopeOnRedirect(true);
		return handlerAdapter;
	}
	
	/*@Bean(name="recipe")
	public RecipeFlowHandler recipeFlowHandler() {
		logger.debug("RecipeFlowHandler");
		return new RecipeFlowHandler();
	}*/
	
	/*** validation and i18n message configuration ***/
	@Bean
	public MessageSource messageSource() {
		logger.debug("MessageSource");
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasenames(
        		"classpath:content",
        		"classpath:messages",
        		"classpath:labels",
        		"classpath:validation"
        		);
        source.setDefaultEncoding("UTF-8");
        source.setCacheSeconds(0);	//TODO: VALIDATION: be sure to change this value in production
        return source;
    }
	
    @Override
    public Validator getValidator() {
    	logger.debug("getValidator");
    	LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    	validator.setValidationMessageSource(messageSource());
    	return validator;
    }
	
	@Bean
    public CookieLocaleResolver localeResolver() {
		logger.debug("CookieLocaleResolver");
		CookieLocaleResolver resolver = new CookieLocaleResolver();
		resolver.setDefaultLocale(new Locale("en"));
		return resolver;
	}
	
	@Bean
	public LocaleChangeInterceptor localeInterceptor() {
		logger.debug("LocaleChangeInterceptor");
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("lang");
		return interceptor;
	}

    public void addInterceptors(InterceptorRegistry registry) {
    	logger.debug("addInterceptors");
    	registry.addInterceptor(localeInterceptor());
    }
	
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
	
    /*** email configuration ***/
	@Bean
    public JavaMailSenderImpl javaMailSenderImpl() {
		logger.debug("JavaMailSenderImpl");
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
