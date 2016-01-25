package net.kear.recipeorganizer.util;

import java.util.List;
import java.util.Locale;

import net.kear.recipeorganizer.persistence.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
    @Autowired
    private MessageSource messages;
	@Autowired
    private JavaMailSender mailSender;
	@Autowired
    private Environment env;

    private String subjCode = "";
    private String msgCode = "";
    private List<String> msgCodes;
    private Locale locale;
    private User user;
    
	public EmailSender() {}

	public void setSubjectCode(String code) {
		this.subjCode = code;
	}
		
	public void setMessageCode(String code) {
		this.msgCode = code;
	}
	
	public void setMessageCodes(List<String> codes) {
		this.msgCodes = codes;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void sendSimpleEmailMessage() throws MailException {
    	logger.debug("sendSimpleEmailMessage");
    	
        String subject = messages.getMessage(subjCode, null, locale);
        String message = getMessage();
        if (message == null) {
        	throw new MailSendException("No message content");
        }
        
        final SimpleMailMessage email = new SimpleMailMessage();
        
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(env.getProperty("support.email"));
       	mailSender.send(email);	//TODO: SECURITY: don't forget to add this back in production
        
		//throw new MailSendException("sendSimpleEmailMessage forced error");

		//TODO: EMAIL: not sure if variables need to be initialized???
		//initialize();
		
        //return true;        
    }

	public void sendTokenEmailMessage(String msgLink) throws MailException {
    	logger.debug("sendTokenEmailMessage");
    	
        String subject = messages.getMessage(subjCode, null, locale);
        String message = getMessage();
        if (message == null) {
        	throw new MailSendException("No message content");
        }
        
        final SimpleMailMessage email = new SimpleMailMessage();
        
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message + " \r\n\r\n" + msgLink);
        email.setFrom(env.getProperty("support.email"));
       	mailSender.send(email);	//TODO: SECURITY: don't forget to add this back in production
        
		//throw new MailSendException("sendTokenEmailMessage forced error");

		//TODO: EMAIL: not sure if variables need to be initialized???
		//initialize();
		
        //return true;        
    }
	
	private String getMessage() {
		String message = "";
		
        if (msgCode != null) {
        	message = messages.getMessage(msgCode, null, locale);
        }
        else {
        	if (msgCodes != null && !msgCodes.isEmpty() ) {
		        for (String code : msgCodes) {
		        	String msg = messages.getMessage(code, null, locale);
		        	message += msg + " \r\n\r\n ";
		        }
        	}
    	}
		
		return message;
	}
	
	private void initialize() {
	    subjCode = "";
	    msgCode = "";
	    msgCodes.clear();
	    locale = null;
	    user = null;
	}
}
