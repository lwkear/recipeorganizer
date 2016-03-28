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
import net.kear.recipeorganizer.util.email.EmailSender;
import net.kear.recipeorganizer.util.email.RegistrationEmail;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private UserService userService;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private RegistrationEmail registrationEmail; 
	
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

        String userName = user.getFirstName() + " " + user.getLastName();
        String confirmationUrl = "/confirmRegistration?token=" + token;
        
        registrationEmail.init(userName, user.getEmail(), event.getLocale());
        registrationEmail.setTokenUrl(confirmationUrl);
        registrationEmail.constructEmail();
    	emailSender.sendTokenEmail(registrationEmail);
    }
}