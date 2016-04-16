package net.kear.recipeorganizer.security;

import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.UserMessageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomHttpSessionSecurityContextRepository extends HttpSessionSecurityContextRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UserSecurityService securityService;
	@Autowired
	private UserMessageService userMessageService;
	
	@Override
	public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
	
		SecurityContext context = super.loadContext(requestResponseHolder);
		
		Authentication auth = context.getAuthentication();
		if (auth == null)
			return context;
		
		User user = (User)auth.getPrincipal();
		if (user == null)
			return context;
		
		long count = userMessageService.getNotViewedCount(user.getId());
		if (count == 0) {
			//logger.debug("No new messages found");
			return context;
		}
		
		if (count == user.getNewMsgCount()) {
			//logger.debug("Message count matches principal count: " + count);
			return context;
		}			
		
		user.setNewMsgCount(count);
		UserDetails details = securityService.getUserDetails(user);
		Authentication newAuth= new UsernamePasswordAuthenticationToken(details, user.getPassword(), details.getAuthorities());
		context.setAuthentication(newAuth);
	
		return context;
	}
}
