package net.kear.recipeorganizer.persistence.repository;

import java.util.List;

import net.kear.recipeorganizer.persistence.dto.UserMessageDto;
import net.kear.recipeorganizer.persistence.model.UserMessage;
 
public interface UserMessageRepository {

    public void addMessage(UserMessage message);
    public void updateMessage(UserMessage message);
    public void deleteMessage(long id);
    public void setViewed(long id);
    public void setUserViewed(long userId);
    public List<UserMessageDto> listMessages(long toUserId);
    public long getMessageCount(long toUserId);
    public long getNotViewedCount(long toUserId);
}
