package net.kear.recipeorganizer.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	//TODO: EXCEPTION: make this available across all controllers
	//general error handling routine - mostly SQL errors at this point
	@ExceptionHandler(DataAccessException.class)
	public ModelAndView handleDataAccessException(DataAccessException ex) {
		logger.info("DataAccessException exception!");

		ModelAndView view = new ModelAndView("/errors/errorData");		
		String errorMsg = ExceptionUtils.getRootCauseMessage(ex);
		
		//view.addObject("addError", "true");
		view.addObject("errorMessage", errorMsg);
		return view;
	}
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(Exception ex) {
		logger.info("Exception exception!");

		ModelAndView view = new ModelAndView("/errors/errorData");		
		String errorMsg = ExceptionUtils.getRootCauseMessage(ex);
		
		//view.addObject("addError", "true");
		view.addObject("errorMessage", errorMsg);
		return view;
	}
}
