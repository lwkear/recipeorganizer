package net.kear.recipeorganizer.util.email;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;

@Component
public abstract class EmailMessage {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    public Environment env;
    @Autowired
    public MessageSource messages;
	@Autowired
	public Configuration freemarkerConfig;

	private String senderName = "";
	private String senderEmail = "";
	private HashMap<String, String> msgText;
	private String appUrl = "";
	private Locale locale;
	private Template template = null;
	
	public EmailMessage() {}
	
	@PostConstruct
	public void getProperties() {
		logger.debug("email getProperties()");
		this.senderName = env.getProperty("company.support.name");
		this.senderEmail = env.getProperty("company.support.email");
		this.appUrl = env.getProperty("company.support.baseurl");
	}
	
	public void constructEmail(EmailDetail emailDetail) {
		setLocale(emailDetail.getLocale());
		emailDetail.setSenderName(getSenderName());
    	emailDetail.setSenderEmail(getSenderEmail());
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	public String getSenderEmail() {
		return senderEmail;
	}
		
	public String getAppUrl() {
		return appUrl;
	}

	public Template getTemplate() {
		return template;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setTemplate(String template) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
		this.template = freemarkerConfig.getTemplate(template);
	}
	
	public String getArgMessage(String code, Object args[]) {
		return messages.getMessage(code, args, "", locale);
	}
	
	public void setMsgText(String[] bodyCodes) {
		msgText = new HashMap<String, String>();
		
		if (bodyCodes != null) {
	        for (String code : bodyCodes) {
	        	String msg = messages.getMessage(code, null, "", locale);
	        	msgText.put(code, msg);
	        }
    	}
	}

	public String getMsgText(String key) {
		return msgText.get(key);
	}
}
