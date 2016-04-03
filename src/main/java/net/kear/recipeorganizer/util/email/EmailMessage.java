package net.kear.recipeorganizer.util.email;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

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

	@Autowired
    private Environment env;
    @Autowired
    private MessageSource messages;
	@Autowired
	private Configuration freemarkerConfig;

	private String senderName = "";
	private String senderEmail = "";
	private String recipientName = "";
	private String recipientEmail = "";
	private String subject = "";
	private String body = "";
	private HashMap<String, String> msgText;
	private String appUrl = "";
	private String tokenUrl = "";
	private Locale locale = null;
	private Template template = null;
	private boolean pdfAttached = false;
	private String pdfFileName = "";
	
	public EmailMessage() {}

	public void init(String recipientName, String recipientEmail, Locale locale) {
		this.senderName = env.getProperty("support.name");
		this.senderEmail = env.getProperty("support.email");
		this.appUrl = env.getProperty("support.baseurl");
		this.recipientName = recipientName;
		this.recipientEmail = recipientEmail;
		this.locale = locale;
	}
	
	public void constructEmail() {}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	public String getSenderEmail() {
		return senderEmail;
	}
	
	public String getRecipientName() {
		return recipientName;
	}
	
	public String getRecipientEmail() {
		return recipientEmail;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public String getTokenUrl() {
		return this.tokenUrl;
	}
	
	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = appUrl + tokenUrl;
	}
	
	public boolean isPdfAttached() {
		return pdfAttached;
	}

	public void setPdfAttached(boolean pdfAttached) {
		this.pdfAttached = pdfAttached;
	}

	public String getPdfFileName() {
		return pdfFileName;
	}

	public void setPdfFileName(String pdfFileName) {
		this.pdfFileName = pdfFileName;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(String template) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
		this.template = freemarkerConfig.getTemplate(template);
	}
	
	public String getArgMessage(String code, Object args[]) {
		return messages.getMessage(code, args, "", locale);
	}
	
	public void setMsgText(String bodyCodes[]) {
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
