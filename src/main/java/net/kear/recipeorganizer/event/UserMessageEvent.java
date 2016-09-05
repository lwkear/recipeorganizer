package net.kear.recipeorganizer.event;

import java.util.Locale;

import net.kear.recipeorganizer.enums.MessageType;
import net.kear.recipeorganizer.persistence.model.UserMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class UserMessageEvent extends ApplicationEvent {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private UserMessage userMessage;
	private MessageType messageType;
	private Locale locale;

    public UserMessageEvent(UserMessage userMessage, MessageType messageType, final Locale locale) {
    	super(userMessage);
    	this.userMessage = userMessage;
    	this.messageType = messageType;
        this.locale = locale;
        logger.debug("UserMessageEvent");
    }

    public UserMessage getUserMessage() {
    	return userMessage;
    }
    
    public MessageType getMessageType() {
    	return messageType;
    }
    
    public Locale getLocale() {
        return locale;
    }
}
