package net.kear.recipeorganizer.exception;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
    @Override
	protected
    ResponseEntity handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.info("NoHandlerFoundException exception!");

		/*ModelAndView view = new ModelAndView("/errors/errorData");
		
		List<String> errorMsgs = new <String>ArrayList();
		String msg = "getMessage: " + ExceptionUtils.getMessage(ex);
		errorMsgs.add(msg);
		msg = "getRootCauseMessage: " + ExceptionUtils.getRootCauseMessage(ex);
		errorMsgs.add(msg);
		msg = "getStackTrace: " + ExceptionUtils.getStackTrace(ex);
		errorMsgs.add(msg);
		
		view.addObject("errorMsgs", errorMsgs);
		return view;*/
    	
    	return super.handleExceptionInternal(ex, null, headers, status, request);
    }
}
