package net.kear.recipeorganizer.util;

import java.util.List;
import java.util.Locale;

import net.kear.recipeorganizer.persistence.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
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
	
	public SimpleMailMessage constructTokenEmailMessage(String msgLink, String token) {
    	logger.debug("constructTokenEmailMessage");
    	
        String subject = messages.getMessage(subjCode, null, locale);
        String link = msgLink + token;
        String message = "";
        
        if (msgCode != null) {
        	message = messages.getMessage(msgCode, null, locale);
        }
        else {
	        for (String code : msgCodes) {
	        	String msg = messages.getMessage(code, null, locale);
	        	message += msg + " \r\n ";
	        }
    	}
        
        final SimpleMailMessage email = new SimpleMailMessage();
        
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message + " \r\n" + link);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }	
}
