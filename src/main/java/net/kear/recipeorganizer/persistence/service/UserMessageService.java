package net.kear.recipeorganizer.persistence.service;

import java.util.List;
import java.util.Locale;

import net.kear.recipeorganizer.enums.MessageType;
import net.kear.recipeorganizer.persistence.dto.UserMessageDto;
import net.kear.recipeorganizer.persistence.model.UserMessage;
 
public interface UserMessageService {

    public void addMessage(UserMessage message, MessageType messageType, Locale locale);
    public void updateMessage(UserMessage message);
    public void deleteMessage(long id);
    public void setViewed(long id);
    public void setUserViewed(long userId);
    public List<UserMessageDto> listMessages(long toUserId);
    public long getMessageCount(long toUserId);
    public long getNotViewedCount(long toUserId);
}
