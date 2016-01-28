package net.kear.recipeorganizer.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.security.AuthCookie;
import net.kear.recipeorganizer.util.CommonView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ErrorController {

	private final Logger logger = LoggerFactory.getLogger(getClass());	

	@Autowired
	private MessageSource messages;
	@Autowired
	private ExceptionLogService logService;
	@Autowired
	private CommonView commonView;
	@Autowired
	private AuthCookie authCookie;

	@RequestMapping(value = "/systemError", method = RequestMethod.GET)
	public ModelAndView getSystemError(RedirectAttributes redir, HttpServletRequest request, HttpServletResponse response, Locale locale) {		
		logger.info("getSystemError");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}

		Enumeration<String> attrNames = request.getSession().getAttributeNames();
		while (attrNames.hasMoreElements())
			logger.info("getHome: attrNames " + attrNames.nextElement());
		
		String title = messages.getMessage("exception.title.systemUnavailable", null, "System not available", locale);
		
		List<String> errorMsgs = new ArrayList<String>();
		errorMsgs.add(messages.getMessage("exception.databaseDown", null, "", locale));
		errorMsgs.add(messages.getMessage("exception.apologize", null, "", locale));		
		
		//NOTE: must redirect to another page; otherwise security does not log in as anonymous and all hell breaks loose with the _csrf token, etc.
		ModelAndView mv = new ModelAndView();
		redir.addFlashAttribute("errorTitle", title);
		redir.addFlashAttribute("errorMsgs", errorMsgs);
        mv.setViewName("redirect:/system");
	    return mv;
	}

	@RequestMapping(value = "/system", method = RequestMethod.GET)
	public String getSystem(Model model) {
		logger.info("getSystem");
		
		return "system";
	}
	
	/**********************/
	/*** session errors ***/
	/**********************/
	@RequestMapping(value = "/accessDenied", method = RequestMethod.GET)
	public ModelAndView accessDeniedError() {
		
		return commonView.getStandardErrorPage(new AccessDeniedException(null));
	}
}