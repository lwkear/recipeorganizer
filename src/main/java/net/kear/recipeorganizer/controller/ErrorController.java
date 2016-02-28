package net.kear.recipeorganizer.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.security.AuthCookie;
import net.kear.recipeorganizer.util.CommonView;
import net.kear.recipeorganizer.util.CookieUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {

	private final Logger logger = LoggerFactory.getLogger(getClass());	

	@Autowired
	private MessageSource messages;
	@Autowired
	private CommonView commonView;
	@Autowired
	private AuthCookie authCookie;
	@Autowired
	private CookieUtil cookieUtil;
	
	@RequestMapping(value = "/systemError", method = RequestMethod.GET)
	public ModelAndView getSystemError(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		logger.info("getSystemError");
		
	    return commonView.getSystemErrorPage(request, response, locale);
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

	@RequestMapping(value = "/expiredSession", method = RequestMethod.GET)
	public ModelAndView expiredSessionError() {
		
		return commonView.getStandardErrorPage(new SessionAuthenticationException(null));		
	}
}