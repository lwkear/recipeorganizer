package net.kear.recipeorganizer.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.util.view.CommonView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

@Controller
public class ErrorController {

	private final Logger logger = LoggerFactory.getLogger(getClass());	

	@Autowired
    private Environment env;
	@Autowired
	private MessageSource messages;
	@Autowired
	private CommonView commonView;
	
	@RequestMapping(value = "/systemError", method = RequestMethod.GET)
	public ModelAndView getSystemError(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		logger.info("getSystemError");
		
	    return commonView.getSystemErrorPage(request, response, locale);
	}

	@RequestMapping(value = "/system", method = RequestMethod.GET)
	public String getSystem(Model model, HttpServletRequest request, Locale locale) {
		logger.info("getSystem");
		
		//need to check if this exception was caught by a web.xml 500 error
		int exception = 0;
		Object obj = request.getAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE);
		if (obj != null)
			exception = (Integer)obj;
		if (exception == 500) {
			List<String> errorMsgs = new ArrayList<String>();
			errorMsgs.add(messages.getMessage("exception.500", null, "", locale));
			model.addAttribute("errorMsgs", errorMsgs);
		}
		
		return "system";
	}

	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public String getError(HttpServletRequest request) {
		logger.info("getError");
		
		return "error";
	}

	/**********************/
	/*** session errors ***/
	/**********************/
	@RequestMapping(value = "/accessDenied", method = RequestMethod.GET)
	public ModelAndView accessDeniedError() {
		return commonView.getStandardErrorPage(new AccessDeniedException(null));		
	}

	@RequestMapping(value = "/expiredSession", method = RequestMethod.GET)
	public ModelAndView expiredSessionError() {
		return commonView.getStandardErrorPage(new SessionAuthenticationException(null));		
	}
}