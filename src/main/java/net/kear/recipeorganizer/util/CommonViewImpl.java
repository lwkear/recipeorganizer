package net.kear.recipeorganizer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.kear.recipeorganizer.persistence.service.ExceptionLogService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.ModelAndView;

public class CommonViewImpl implements CommonView {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MessageSource messages;
	@Autowired
	private ExceptionLogService logService;

	public ModelAndView getStandardErrorPage(Exception ex) {
		//log the error in the log file
		logger.error(ex.getClass().toString(), ex);
		logger.debug("getErrorPage exception simpleName: " + ex.getClass().getSimpleName());
		//log the error in the database 
		try {
			logService.addException(ex);
		}
		catch (Exception e) {
			//do nothing - it is likely that the database is down
		}
		
		Locale locale = LocaleContextHolder.getLocale();

		//default exception
		String msgCode = "exception." + ex.getClass().getSimpleName();
		
		String title = messages.getMessage("exception.title.error", null, "Error", locale);
		List<String> errorMsgs = new ArrayList<String>();
		errorMsgs.add(messages.getMessage(msgCode, null, ex.getClass().getSimpleName(), locale));
		
		ModelAndView view = new ModelAndView("/error");
		view.addObject("errorTitle", title);
		view.addObject("errorMsgs", errorMsgs);
		
		return view;
	}
}
