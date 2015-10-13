package net.kear.recipeorganizer.listener;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import net.kear.recipeorganizer.event.OnPasswordResetEvent;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.UserService;

@Component
public class PasswordResetListener implements ApplicationListener<OnPasswordResetEvent> {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

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

        final SimpleMailMessage email = constructEmailMessage(event, user, token);
        mailSender.send(email);
    }

    private final SimpleMailMessage constructEmailMessage(final OnPasswordResetEvent event, final User user, final String token) {
    	logger.debug("constructEmailMessage");
        final String recipientAddress = user.getEmail();
        final String subject = "Password Reset";
        final String confirmationUrl = event.getAppUrl() + "/confirmPassword?id=" + user.getId() + "&token=" + token;
        final String message = messages.getMessage("passwordReset", null, event.getLocale());
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }
}