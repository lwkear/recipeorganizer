package net.kear.recipeorganizer.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class RememberMeSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	private AuthCookie authCookie;
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	private UserService userService;	
	
	public RememberMeSuccessHandler() {
		super();
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		logger.info("onAuthenticationSuccess");
		
		authCookie.setCookie(request, response, authentication.getName());
		
		User user = userService.findUserByEmail(authentication.getName());
		user.setLastLogin(new Date());
		userService.updateUser(user);
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
}

/*Object principal = authentication.getPrincipal();
logger.info("Session principal: " + principal.toString());

HttpSession session = request.getSession(false);
if (session != null) {
	String sessID = session.getId();
	logger.info("Session ID: " + sessID);
}
else
	logger.info("Session is null");

List<Object> allPrinc = sessionRegistry.getAllPrincipals();

if (principal != null && allPrinc.size() > 0) {
	if (allPrinc.contains(principal))
		logger.info("getAllPrincipals() contains prinicpal");
}

for (Object obj : allPrinc) {
	final List<SessionInformation> sessions = sessionRegistry.getAllSessions(obj, true);

	for (SessionInformation sess : sessions) {
		Object princ = sess.getPrincipal();
		String sessId = sess.getSessionId();
		Date sessDate = sess.getLastRequest();
		
		logger.info("sessionRegistry.princ: " + princ);
		logger.info("sessionRegistry.sessId: " + sessId);
		logger.info("sessionRegistry.sessDate: " + sessDate.toString());
		
		if (principal.equals(princ))
			logger.info("found prinicpal match");
	}
}*/
