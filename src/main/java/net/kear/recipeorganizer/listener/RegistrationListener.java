package net.kear.recipeorganizer.listener;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import net.kear.recipeorganizer.event.OnRegistrationCompleteEvent;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.util.EmailSender;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private UserService userService;

	@Autowired
	private EmailSender emailSender;
	
    @Override
    public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
    	logger.debug("onApplicationEvent");
        
		this.confirmRegistration(event);
    }

    private void confirmRegistration(final OnRegistrationCompleteEvent event) {
    	logger.debug("confirmRegistration");
        final User user = event.getUser();
        final String token = UUID.randomUUID().toString();
        userService.createUserVerificationToken(user, token);

    	String confirmationUrl = event.getAppUrl() + "/confirmRegistration?token=" + token;
    	
    	emailSender.setUser(user);
    	emailSender.setLocale(event.getLocale());
    	emailSender.setSubjectCode("user.email.signupSubject");
    	emailSender.setMessageCode("user.email.signupSuccess");
    	emailSender.sendTokenEmailMessage(confirmationUrl);
    }
}