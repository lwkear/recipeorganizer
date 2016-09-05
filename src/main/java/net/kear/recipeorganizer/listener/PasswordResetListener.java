package net.kear.recipeorganizer.listener;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import net.kear.recipeorganizer.event.PasswordResetEvent;
import net.kear.recipeorganizer.exception.PasswordResendException;
import net.kear.recipeorganizer.persistence.model.PasswordResetToken;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.util.email.EmailDetail;
import net.kear.recipeorganizer.util.email.EmailSender;
import net.kear.recipeorganizer.util.email.PasswordEmail;

@Component
public class PasswordResetListener implements ApplicationListener<PasswordResetEvent> {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private UserService userService;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private PasswordEmail passwordEmail; 
	
    @Override
    public void onApplicationEvent(final PasswordResetEvent event) {
    	logger.debug("onApplicationEvent");
        this.confirmPasswordReset(event);
    }

    private void confirmPasswordReset(final PasswordResetEvent event) {
    	logger.debug("confirmPasswordReset");
        final User user = event.getUser();
        final String oldToken = event.getToken();
        String newToken = null;
        
        if (oldToken == null) {
        	newToken = UUID.randomUUID().toString();
        	userService.createPasswordResetTokenForUser(user, newToken);
        }
        else {
        	PasswordResetToken token = null;
        	token = userService.recreatePasswordResetTokenForUser(oldToken);
        	newToken = token.getToken();
        }

        String userName = user.getFirstName() + " " + user.getLastName();
        String confirmationUrl = "/confirmPassword?id=" + user.getId() + "&token=" + newToken;
        
        EmailDetail emailDetail = new EmailDetail(userName, user.getEmail(), event.getLocale());
        emailDetail.setTokenUrl(confirmationUrl);
        
        try {
            passwordEmail.constructEmail(emailDetail);        	
        	emailSender.sendHtmlEmail(emailDetail);
		} catch (Exception ex) {
	    	throw new PasswordResendException(ex);
	    }
    }
}