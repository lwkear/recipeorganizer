package net.kear.recipeorganizer.listener;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import net.kear.recipeorganizer.event.RegistrationCompleteEvent;
import net.kear.recipeorganizer.exception.VerificationResendException;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.model.VerificationToken;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.util.email.EmailDetail;
import net.kear.recipeorganizer.util.email.EmailSender;
import net.kear.recipeorganizer.util.email.RegistrationEmail;

@Component
public class RegistrationListener implements ApplicationListener<RegistrationCompleteEvent> {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private UserService userService;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private RegistrationEmail registrationEmail; 
	
    @Override
    public void onApplicationEvent(final RegistrationCompleteEvent event) {
    	logger.debug("onApplicationEvent");
		this.confirmRegistration(event);
    }

    private void confirmRegistration(final RegistrationCompleteEvent event) {
    	logger.debug("confirmRegistration");
        final User user = event.getUser();
        final String oldToken = event.getToken();
        String newToken = null;
        
        if (oldToken == null) {
        	newToken = UUID.randomUUID().toString();
        	userService.createUserVerificationToken(user, newToken);
        }
        else {
        	VerificationToken token = null;
        	token = userService.recreateUserVerificationToken(oldToken);
        	newToken = token.getToken();
        }
        
        String userName = user.getFirstName() + " " + user.getLastName();
        String confirmationUrl = "/confirmRegistration?token=" + newToken;
        
        EmailDetail emailDetail = new EmailDetail(userName, user.getEmail(), event.getLocale());
        emailDetail.setTokenUrl(confirmationUrl);
        try {
        	registrationEmail.constructEmail(emailDetail);
        	emailSender.sendHtmlEmail(emailDetail);
		} catch (Exception ex) {
			throw new VerificationResendException(ex);
		}
    }
}