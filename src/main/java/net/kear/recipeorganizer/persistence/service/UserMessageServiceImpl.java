package net.kear.recipeorganizer.persistence.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.kear.recipeorganizer.persistence.dto.UserMessageDto;
import net.kear.recipeorganizer.persistence.model.UserMessage;
import net.kear.recipeorganizer.persistence.repository.UserMessageRepository;

@Service("messageService")
@Transactional
public class UserMessageServiceImpl implements UserMessageService {

	@Autowired
	UserMessageRepository messageRepository;
	
	@Override
	public void addMessage(UserMessage message) {
		messageRepository.addMessage(message);
	}

	@Override
	public void updateMessage(UserMessage message) {
		messageRepository.updateMessage(message);
	}

	@Override
	public void deleteMessage(long id) {
		messageRepository.deleteMessage(id);
	}

	@Override
	public void setViewed(long id) {
		messageRepository.setViewed(id);
	}

	public void setUserViewed(long userId) {
		messageRepository.setUserViewed(userId);
	}
	
	@Override
	public List<UserMessageDto> listMessages(long toUserId) {
		return messageRepository.listMessages(toUserId);
	}

	@Override
	public long getMessageCount(long toUserId) {
		return messageRepository.getMessageCount(toUserId);
	}

	@Override
	public long getNotViewedCount(long toUserId) {
		return messageRepository.getNotViewedCount(toUserId);
	}

}
