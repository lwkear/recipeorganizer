package net.kear.recipeorganizer.persistence.service;

import java.util.Date;

import net.kear.recipeorganizer.persistence.model.ExceptionLog;
import net.kear.recipeorganizer.persistence.repository.ExceptionLogRepository;
import net.kear.recipeorganizer.util.CookieUtil;
import net.kear.recipeorganizer.util.UserInfo;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExceptionLogServiceImpl implements ExceptionLogService {

    @Autowired
    ExceptionLogRepository exceptionLogRepository; 
	@Autowired
	private UserInfo userInfo;
	
	public void addException(Exception ex) {
		ExceptionLog log = new ExceptionLog();
		
		Date timestamp = new Date();
		long eventId = exceptionLogRepository.getEventId();
		String userName;
		if (userInfo.isUserAnonymous())
			userName = CookieUtil.ANNON_USER;
		else
			userName = userInfo.getUserDetails().getUsername();

		log.setEventId(eventId);
		log.setEventTimestamp(timestamp);
		log.setUserName(userName);
		log.setMesssage(ExceptionUtils.getRootCauseMessage(ex));
		log.setClassName(ex.getClass().getName());
		log.setMethod("");
		log.setLineNum("");
		exceptionLogRepository.addException(log);
		
		for (StackTraceElement st : ex.getStackTrace())
		{
		    String className = st.getClassName();
		    if (className.indexOf("recipeorganizer") >= 0) {
		    	log = new ExceptionLog();
				log.setEventId(eventId);
				log.setEventTimestamp(timestamp);
				log.setUserName(userName);
				log.setMesssage(ExceptionUtils.getRootCauseMessage(ex));
				log.setClassName(className);
				log.setMethod(st.getMethodName());
				log.setLineNum(Integer.toString(st.getLineNumber()));
				exceptionLogRepository.addException(log);
		    }
		}
	}
}
