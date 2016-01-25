package net.kear.recipeorganizer.controller;

import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.util.CommonView;
import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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
	@Autowired
	private CommonView commonView;

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
*/	
	
	@ExceptionHandler(value={
			JRException.class,
			SolrServerException.class,
			ObjectNotFoundException.class
		})
	public ModelAndView handleSpecificExceptions(Exception ex) {
		logger.info("handleSpecificExceptions exception class: " + ex.getClass().toString());
		String msg = ExceptionUtils.getMessage(ex);
		logger.debug("handleSpecificExceptions msg: " + msg);
		Throwable excptn = ExceptionUtils.getRootCause(ex);
		if (excptn != null) {
			msg = ExceptionUtils.getRootCause(ex).getClass().toString();
			logger.debug("handleSpecificExceptions root class: " + msg);
			msg = ExceptionUtils.getRootCauseMessage(ex);
			logger.debug("handleSpecificExceptions root msg: " + msg);
		}

		return commonView.getStandardErrorPage(ex);
	}

	@ExceptionHandler(value= {
			CannotCreateTransactionException.class,
			InternalAuthenticationServiceException.class
		})
	public String handleDBException(Exception ex) {
		logger.info("handleDBExceptions exception class: " + ex.getClass().toString());
		String msg = ExceptionUtils.getMessage(ex);
		logger.debug("handleDBExceptions msg: " + msg);
		Throwable excptn = ExceptionUtils.getRootCause(ex);
		if (excptn != null) {
			msg = ExceptionUtils.getRootCause(ex).getClass().toString();
			logger.debug("handleDBExceptions root class: " + msg);
			msg = ExceptionUtils.getRootCauseMessage(ex);
			logger.debug("handleDBExceptions root msg: " + msg);
		}
		
		//TODO: examine the error to make sure it's actually a database down error; otherwise pass it on to getErrorPage
		
		//log the error in the log file
		logger.error(ex.getClass().toString(), ex);		

		return "redirect:/systemError";
	}
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleGeneralException(Exception ex) {
		logger.info("handleGeneralExceptions exception class: " + ex.getClass().toString());
		String msg = ExceptionUtils.getMessage(ex);
		logger.debug("handleGeneralExceptions msg: " + msg);
		Throwable excptn = ExceptionUtils.getRootCause(ex);
		if (excptn != null) {
			msg = ExceptionUtils.getRootCause(ex).getClass().toString();
			logger.debug("handleGeneralExceptions root class: " + msg);
			msg = ExceptionUtils.getRootCauseMessage(ex);
			logger.debug("handleGeneralExceptions root msg: " + msg);
		}
		
		return commonView.getStandardErrorPage(ex);
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
