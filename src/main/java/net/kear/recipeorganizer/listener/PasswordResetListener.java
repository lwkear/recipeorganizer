package net.kear.recipeorganizer.listener;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import net.kear.recipeorganizer.event.OnPasswordResetEvent;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.util.email.EmailSender;

@Component
public class PasswordResetListener implements ApplicationListener<OnPasswordResetEvent> {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private UserService userService;

	@Autowired
	private EmailSender emailSender;
	
    @Override
    public void onApplicationEvent(final OnPasswordResetEvent event) {
    	logger.debug("onApplicationEvent");
        this.confirmPasswordReset(event);
    }

    private void confirmPasswordReset(final OnPasswordResetEvent event) {
    	logger.debug("confirmPasswordReset");
        final User user = event.getUser();
        final String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        String confirmationUrl = event.getAppUrl() + "/confirmPassword?id=" + user.getId() + "&token=" + token;
    	
    	emailSender.setUser(user);
    	emailSender.setLocale(event.getLocale());
    	emailSender.setSubjectCode("user.email.accountChange");
    	emailSender.setMessageCode("user.email.passwordResetMessage");
    	emailSender.sendTokenEmailMessage(confirmationUrl);
    }
}