package net.kear.recipeorganizer.controller;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.kear.recipeorganizer.security.AuthCookie;
import net.kear.recipeorganizer.util.UserInfo;

@Controller
public class HomeController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AuthCookie authCookie;
	@Autowired
	private UserInfo userInfo;
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	private MessageSource messages;

	@RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
	public String getHome(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		logger.info("getHome");

		Date createTime = new Date(session.getCreationTime());
		Date lastAccess = new Date(session.getLastAccessedTime());
		int maxInactive = session.getMaxInactiveInterval();
		String sessID = session.getId();

		logger.info("Session created on: " + createTime);
		logger.info("Session last accessed on: " + lastAccess);
		logger.info("Session expires after: " + maxInactive + " seconds");
		logger.info("Session ID: " + sessID);

		List<Object> allPrinc = sessionRegistry.getAllPrincipals();

		for (Object obj : allPrinc) {
			final List<SessionInformation> sessions = sessionRegistry.getAllSessions(obj, true);

			for (SessionInformation sess : sessions) {
				Object princ = sess.getPrincipal();
				String sessId = sess.getSessionId();
				Date sessDate = sess.getLastRequest();
				
				logger.info("sessionRegistry.princ: " + princ);
				logger.info("sessionRegistry.sessId: " + sessId);
				logger.info("sessionRegistry.sessDate: " + sessDate.toString());
			}
		}

		Enumeration<String> attrNames = session.getAttributeNames();
		while (attrNames.hasMoreElements())
			logger.info("getHome: attrNames " + attrNames.nextElement());
		
		Object token = session.getAttribute("org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN");
		if (token != null)
			logger.info("getHome: csrf token: " + token.toString());
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.info("security context auth: " + auth.toString());
		
		//tell the page to not include the white vertical filler
		model.addAttribute("vertFiller", "1");
		
		if (!authCookie.cookieExists(request))
			authCookie.setCookie(request, response, userInfo.getName());
		
		return "home";
	}

	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String getAbout(Model model, HttpSession session) {
		logger.info("getAbout");
		
		return "about";
	}

	@RequestMapping(value = "/thankyou", method = RequestMethod.GET)
	public String getThankyou(Model model, HttpSession session) {
		logger.info("getThankyou");
	
		//tell the page to not include the white vertical filler
		model.addAttribute("vertFiller", "1");
		
		return "thankyou";
	}
	
	@RequestMapping(value = "/faq", method = RequestMethod.GET)
	public String getFaq(Model model) {
		logger.info("getFaq");
		
		return "faq";
	}

	@RequestMapping(value = "/contact", method = RequestMethod.GET)
	public String getContact(Model model) {
		logger.info("getContact");
		
		//throw new CannotCreateTransactionException(null);
		
		return "contact";
	}
	
	//AJAX/JSON request for getting the timeout interval for the current user
	@RequestMapping(value="/getSessionTimeout")
	@ResponseBody 
	public Integer getSessionTimeout(HttpSession session, HttpServletResponse response) {
		
		Integer maxInactive = session.getMaxInactiveInterval();
		
		logger.info("getSessionTimeout returned " + maxInactive + " seconds");
		
		return maxInactive;
	}

	//AJAX/JSON request for resetting the session timeout
	@RequestMapping(value="/setSessionTimeout", method = RequestMethod.POST)
	@ResponseBody 
	public String setSessionTimeout(HttpSession session, HttpServletResponse response) {
		
		response.setStatus(HttpServletResponse.SC_OK);
		
		Integer maxInactive = session.getMaxInactiveInterval();
		session.setMaxInactiveInterval(maxInactive);
		
		logger.info("setSessionTimeout for " + maxInactive + " seconds");
		
		return "{}";
	}
}


	/*@RequestMapping(value = "/testpage", method = RequestMethod.GET)
	public String getTestpage(Model model) {
		logger.info("getTestpage");

		Recipe recipe = new Recipe();
		recipe.setAllowShare(true);
		model.addAttribute("recipe", recipe);
		
		return "testpage";
	}

	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public String getStartpage(Model model) {
		logger.info("getStartpage");

		return "start";
	}*/
