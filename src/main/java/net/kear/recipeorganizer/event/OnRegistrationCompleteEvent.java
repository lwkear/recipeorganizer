package net.kear.recipeorganizer.event;

import java.util.Locale;

import net.kear.recipeorganizer.persistence.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class OnRegistrationCompleteEvent extends ApplicationEvent {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
    private final String appUrl;
    private final Locale locale;
    private final User user;

    public OnRegistrationCompleteEvent(final User user, final Locale locale, final String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
        logger.debug("OnRegistrationCompleteEvent");
    }

    public String getAppUrl() {
        return appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public User getUser() {
        return user;
    }
}
