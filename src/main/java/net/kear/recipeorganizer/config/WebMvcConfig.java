package net.kear.recipeorganizer.config;

import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;

import net.kear.recipeorganizer.enums.ApprovalActionFormatter;
import net.kear.recipeorganizer.enums.ApprovalReasonFormatter;
import net.kear.recipeorganizer.interceptor.MaintenanceInterceptor;
import net.kear.recipeorganizer.report.ReportGenerator;
import net.kear.recipeorganizer.resolver.CustomCookieLocaleResolver;
import net.kear.recipeorganizer.security.HttpHeadFilter;
import net.kear.recipeorganizer.solr.SolrUtil;
import net.kear.recipeorganizer.solr.SolrUtilImpl;
import net.kear.recipeorganizer.util.SpeechUtil;
import net.kear.recipeorganizer.util.SpeechUtilImpl;
import net.kear.recipeorganizer.util.file.FileActions;
import net.kear.recipeorganizer.util.file.FileActionsImpl;
import net.kear.recipeorganizer.util.maint.MaintenanceProperties;
import net.kear.recipeorganizer.webflow.RecipeFlowHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.webflow.mvc.servlet.FlowHandlerAdapter;
import org.springframework.webflow.mvc.servlet.FlowHandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

@EnableWebMvc
@EnableAsync
@EnableTransactionManagement
@ComponentScan(basePackages = {
	"net.kear.recipeorganizer.controller",
	"net.kear.recipeorganizer.listener",
	"net.kear.recipeorganizer.util",
	"net.kear.recipeorganizer.util.email",
	"net.kear.recipeorganizer.enums"
	})
@Configuration
@PropertySources(value={@PropertySource("classpath:email.${spring.profiles.active}.properties"),
						@PropertySource("classpath:filedir.${spring.profiles.active}.properties"),
						@PropertySource("classpath:solr.${spring.profiles.active}.properties"),
						@PropertySource("classpath:company.${spring.profiles.active}.properties")})
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private Environment env;
	@Autowired
	private WebFlowConfig webFlowConfig;
	@Autowired
	private ServletContext servletContext;
	@Autowired
	private ApprovalActionFormatter actionFormatter;
	@Autowired
	private ApprovalReasonFormatter reasonFormatter;
	
	/*** resource location configuration ***/
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		logger.debug("addResourceHandlers");
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/").setCachePeriod(31556926);
        registry.addResourceHandler("/robots.txt").addResourceLocations("/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/resources/images");
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
		//register Hibernate5Module to support lazy objects
		mapper.registerModule(new Hibernate5Module());
		
		messageConverter.setObjectMapper(mapper);
		return messageConverter;
	}	
	
	/*** file upload/download configuration ***/
	@Bean(name="filterMultipartResolver")
	public CommonsMultipartResolver filterMultipartResolver() {
		logger.debug("CommonsServletMultipartResolver");
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setDefaultEncoding("UTF-8");
		return resolver;
	}

	@Bean
	public FileActions fileActions() {
		logger.debug("FileActionsImpl");
		FileActionsImpl actions = new FileActionsImpl();
		actions.setAvatarDir(env.getProperty("file.directory.avatar"));
		actions.setRecipeDir(env.getProperty("file.directory.recipe"));
		return actions;
	}
	
	/*** view configuration ***/
	@Bean
	public PropertiesFactoryBean properties() {
		PropertiesFactoryBean bean = new PropertiesFactoryBean();
		//make this file accessible in a jsp
		String path = "company." + env.getActiveProfiles()[0] + ".properties";
		Resource resource = new ClassPathResource(path);
		bean.setLocation(resource);
		return bean;
	}
	
	@Bean
    public InternalResourceViewResolver viewResolver() {
		logger.debug("InternalResourceViewResolver");
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        //required to access company.properties in a jsp
        resolver.setExposeContextBeansAsAttributes(true);
        resolver.setExposedContextBeanNames("properties");
        //this allows webflow jsp's to access the request locale
        resolver.setRequestContextAttribute("requestContext");
        return resolver;
    }
	
	/*** solr configuration ***/
	@Bean
	public SolrUtil solrUtil() {
		logger.debug("SolrUtil");
		SolrUtilImpl solr = new SolrUtilImpl(); 
		solr.setUrl(env.getProperty("solr.url"));
		solr.setCore();
		return solr;
	}	
	
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
		FlowHandlerAdapter handlerAdapter = new RecipeFlowHandlerAdapter();
		handlerAdapter.setFlowExecutor(this.webFlowConfig.flowExecutor());
		handlerAdapter.setSaveOutputToFlashScopeOnRedirect(true);
		return handlerAdapter;
	}
	
	/*** validation and i18n message configuration ***/
	@Bean
	public MessageSource messageSource() {
		logger.debug("MessageSource");
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasenames(
    		"WEB-INF/messages/content",
    		"WEB-INF/messages/messages",
    		"WEB-INF/messages/labels",
    		"WEB-INF/messages/validation",
    		"WEB-INF/messages/faq",
    		"WEB-INF/messages/whatsnew"
    		);
        source.setDefaultEncoding("UTF-8");
        source.setCacheSeconds(5);	//TODO: PRODUCTION: change this back to 5
        source.setFallbackToSystemLocale(false);
        return source;
    }

	@Bean 
	public CharacterEncodingFilter encodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
	    characterEncodingFilter.setEncoding("UTF-8");
	    characterEncodingFilter.setForceEncoding(true);
	    return characterEncodingFilter;
	}
	
    @Override
    public Validator getValidator() {
    	logger.debug("getValidator");
    	LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    	validator.setValidationMessageSource(messageSource());
    	return validator;
    }
	
	@Bean
    public CustomCookieLocaleResolver localeResolver() {
		logger.debug("CustomCookieLocaleResolver");
		CustomCookieLocaleResolver resolver = new CustomCookieLocaleResolver();
		return resolver;
	}
    
	@Bean
	public LocaleChangeInterceptor localeInterceptor() {
		logger.debug("LocaleChangeInterceptor");
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("language");
		return interceptor;
	}
	
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addFormatter(actionFormatter);
		registry.addFormatter(reasonFormatter);
		super.addFormatters(registry);
	}
	
	/*** system maintenance configuration ***/
	@Bean
	public MaintenanceProperties maintProps() {
		return new MaintenanceProperties(servletContext);
	}
	
	@Bean
	public MaintenanceInterceptor maintenanceInterceptor() {
		logger.debug("MaintenanceInterceptor");
		MaintenanceInterceptor interceptor = new MaintenanceInterceptor();
		interceptor.setMaintenanceUrl("/sysmaint");		
		interceptor.setMaintProperties(maintProps());
		interceptor.initializeSettings();
		interceptor.setNextWindow();
		return interceptor;
	}

	/*** interceptors, filters and exception resolver ***/
	public void addInterceptors(InterceptorRegistry registry) {
    	logger.debug("addInterceptors");
    	registry.addInterceptor(localeInterceptor());
    	registry.addInterceptor(maintenanceInterceptor());
    }
	@Bean
	public HttpHeadFilter httpHeadFilter() {
    	logger.debug("HttpHeadFilter");
		return new HttpHeadFilter();
	}
	
    /*** email configuration ***/
	@Bean
    public JavaMailSenderImpl javaMailSender() {
		logger.debug("JavaMailSenderImpl");
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(env.getProperty("smtp.host"));
        mailSenderImpl.setPort(env.getProperty("smtp.port", Integer.class));
        mailSenderImpl.setProtocol(env.getProperty("smtp.protocol"));
        mailSenderImpl.setUsername(env.getProperty("smtp.username"));
        mailSenderImpl.setPassword(env.getProperty("smtp.password"));
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.debug", "true");
        javaMailProps.put("mail.smtp.elho", "false");
        javaMailProps.put("mail.smtp.host", env.getProperty("smtp.host"));
        javaMailProps.put("mail.smtp.port", env.getProperty("smtp.port"));
        javaMailProps.put("mail.smtp.auth", env.getProperty("mail.smtp.auth"));
        javaMailProps.put("mail.smtp.localhost", env.getProperty("smtp.host"));
        javaMailProps.put("mail.smtp.starttls.enable", env.getProperty("smtp.starttls.enable"));
        
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        mailSenderImpl.getSession().setDebug(true);
        
        //removed test since it doesn't really matter at this point if mail is up or down;
        //the user should be notified when an email is sent
        /*try {
			mailSenderImpl.testConnection();
		} catch (MessagingException ex) {
			logger.debug("test mail connection failed");
			logger.error(ex.getClass().toString(), ex);
		}*/
        
        return mailSenderImpl;
    }
	
	@Bean
	public FreeMarkerConfigurationFactoryBean freemarkerConfig() {
		FreeMarkerConfigurationFactoryBean config = new FreeMarkerConfigurationFactoryBean();
		config.setTemplateLoaderPath("WEB-INF/emails");
		config.setDefaultEncoding("UTF-8");
		config.setPreferFileSystemAccess(false);
		return config;
	}
	
    /*** jasper reports configuration ***/
	@Bean
	public ReportGenerator reportGenerator() {
		ReportGenerator generator = new ReportGenerator();
		generator.configureReports(servletContext, env.getProperty("file.directory.pdfs"));
		return generator;
	}

    /*** Watson SST and TTS configuration ***/
	@Bean
	public SpeechUtil speechUtil() {
		SpeechUtilImpl speech = new SpeechUtilImpl();
		speech.setWatsonTTSAccount(env.getProperty("company.watson.tts.username"), env.getProperty("company.watson.tts.password"));
		speech.setWatsonSTTAccount(env.getProperty("company.watson.stt.username"), env.getProperty("company.watson.stt.password"));
		speech.setWatsonConvAccount(env.getProperty("company.watson.conv.username"), env.getProperty("company.watson.conv.password"), 
				env.getProperty("company.watson.conv.workspace"));
		speech.setSpeechDir(env.getProperty("file.directory.speech"));
		return speech;
	}
}
