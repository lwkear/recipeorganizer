package net.kear.recipeorganizer.event;

import java.util.Locale;

import net.kear.recipeorganizer.persistence.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class PasswordResetEvent extends ApplicationEvent {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
    private final Locale locale;
    private final User user;
    private final String token;

    public PasswordResetEvent(final User user, final Locale locale, final String token) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.token = token;
        logger.debug("PasswordResetCompleteEvent");
    }

    public Locale getLocale() {
        return locale;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
    	return token;
    }
}