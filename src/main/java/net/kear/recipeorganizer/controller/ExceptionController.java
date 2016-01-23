package net.kear.recipeorganizer.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.sf.jasperreports.engine.JRException;

import org.apache.solr.client.solrj.SolrServerException;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionController {

	private final Logger logger = LoggerFactory.getLogger(getClass());	
	
	@Autowired
	private MessageSource messages;
	@Autowired
	private ExceptionLogService logService;

	/*	see ResponseEntityExceptionHandler
	BindException.class,
	ConversionNotSupportedException.class,
	HttpMediaTypeNotAcceptableException.class,
	HttpMediaTypeNotSupportedException.class,
	HttpMessageNotReadableException.class,
	HttpMessageNotWritableException.class,
	HttpRequestMethodNotSupportedException.class,
	MethodArgumentNotValidException.class,
	MissingServletRequestParameterException.class,
	MissingServletRequestPartException.class,
	NoHandlerFoundException.class
	NoSuchRequestHandlingMethodException.class,
	ServletRequestBindingException.class,
	TypeMismatchException.class,


1) manually entered a recipe ID into viewRecipe URL
ObjectNotFoundException: No row with the given identifier exists: [net.kear.recipeorganizer.persistence.model.Recipe#112]
NumberFormatException: For parameter string: "abc"
2) used a recipe view bookmark (referer is null)
NullPointerException
3) solr not running
NullPointerException

	*
	*/	
	
	@ExceptionHandler(value={
			JRException.class,
			SolrServerException.class,
			ObjectNotFoundException.class
		})
	public ModelAndView handleException(Exception ex) {
		logger.info("Exception: " + ex.getClass().toString());
		return getErrorPage(ex);
	}

	@ExceptionHandler(CannotCreateTransactionException.class)
	public String handleDBException(Exception ex) {
		logger.info("Exception: " + ex.getClass().toString());

		//TODO: examine the error to make sure it's actually a database down error; otherwise pass it on to getErrorPage
		
		//log the error in the log file
		logger.error(ex.getClass().toString(), ex);		

		return "redirect:/systemError";
	}
	
	private ModelAndView getErrorPage(Exception ex) {
		//log the error in the log file
		logger.error(ex.getClass().toString(), ex);		
		//log the error in the database
		logService.addException(ex);
		
		Locale locale = LocaleContextHolder.getLocale();
		
		String msgCode = "exception.Exception";
		String classStr[] = ex.getClass().toString().split("[.]");
		if (classStr.length > 0)
			msgCode = "exception." + classStr[classStr.length-1];
		
		String title = messages.getMessage("exception.error.title", null, "Error", locale);
		List<String> errorMsgs = new ArrayList<String>();
		errorMsgs.add(messages.getMessage(msgCode, null, ex.getClass().getName(), locale));
		
		ModelAndView view = new ModelAndView("/errors/errorMessage");
		view.addObject("errorTitle", title);
		view.addObject("errorMsgs", errorMsgs);
		
		return view;
	}
}

/*
List<String> errorMsgs = new ArrayList<String>();
String msg = "getMessage: " + ExceptionUtils.getMessage(ex);
errorMsgs.add(msg);
msg = "getRootCauseMessage: " + ExceptionUtils.getRootCauseMessage(ex);
errorMsgs.add(msg);
//msg = "getStackTrace: " + ExceptionUtils.getStackTrace(ex);
*/

/*
//returns an array of strings
//String[] stackTrace = ExceptionUtils.getRootCauseStackTrace(ex);
//msg = "getRootCauseStackTrace: " + stackTrace[0].getClass();
//errorMsgs.add(msg);

Throwable rootThrow = ExceptionUtils.getRootCause(ex);
if (rootThrow != null) {
	StackTraceElement[] element = rootThrow.getStackTrace();
	if (element.length > 0) {
		msg = "className: " + element[0].getClassName();
		errorMsgs.add(msg);
		msg = "methodName: " + element[0].getMethodName();
		errorMsgs.add(msg);
		msg = "lineNumber: " + element[0].getLineNumber();
		errorMsgs.add(msg);
	}
}*/
