package net.kear.recipeorganizer.controller;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.exception.RestException;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.util.ResponseObject;
import net.kear.recipeorganizer.util.view.CommonView;
import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionController {

	private final Logger logger = LoggerFactory.getLogger(getClass());	
	
	@Autowired
	private MessageSource messages;
	@Autowired
	private CommonView commonView;
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

Solr errors not accounted for:
###exception log id=703###	
HttpSolrClient.RemoteSolrException: Error from server at http://localhost:8983/solr/recipe: undefined field _text_

###exception log id=718###
HttpSolrClient.RemoteSolrException: Error from server at http://localhost:8983/solr/recipe: org.apache.solr.search.SyntaxError: Cannot parse '{allowshare:true}': Encountered " "}" "} "" at line 1, column 16.
Was expecting one of:
    "TO" ...
    <RANGE_QUOTED> ...
    <RANGE_GOOP> ...

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

		//log the exception in the error log file
		logger.error(ex.getClass().toString(), ex);		
		//wrap the logService in case the db is down - do not want to throw another exception!
		try {
			logService.addException(ex);
		} catch (Exception excpt) {}
		
		return commonView.getStandardErrorPage(ex);
	}

	@ExceptionHandler(value= {
			CannotCreateTransactionException.class,
			InternalAuthenticationServiceException.class,
			ConnectException.class
			})
	public ModelAndView handleDBException(Exception ex, HttpServletRequest request, HttpServletResponse response, Locale locale) {
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

		//log the exception in the error log file
		logger.error(ex.getClass().toString(), ex);		

		return commonView.getSystemErrorPage(request, response, locale);
	}
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleGeneralException(Exception ex, HttpServletRequest request, HttpServletResponse response, Locale locale) {
		logger.info("handleGeneralExceptions exception class: " + ex.getClass().toString());
		String msg = ExceptionUtils.getMessage(ex);
		logger.debug("handleGeneralExceptions msg: " + msg);
		//log the exception in the error log file
		logger.error(ex.getClass().toString(), ex);

		Throwable excptn = ExceptionUtils.getRootCause(ex);
		if (excptn != null) {
			msg = ExceptionUtils.getRootCause(ex).getClass().toString();
			logger.debug("handleGeneralExceptions root class: " + msg);
			msg = ExceptionUtils.getRootCauseMessage(ex);
			logger.debug("handleGeneralExceptions root msg: " + msg);
			
			//is the database down?
			if (excptn instanceof ConnectException) {
				return commonView.getSystemErrorPage(request, response, locale);
			}
		}

		//wrap the logService just in case the db is down but the exception is not a ConnectException - do not want to throw another exception!
		try {
			logService.addException(ex);
		} catch (Exception excpt) {}
		
		return commonView.getStandardErrorPage(ex);
	}
	
	@ExceptionHandler(RestException.class)
	@ResponseBody
	public ResponseObject handleRestException(RestException ex, HttpServletResponse response, Locale locale) {
		//the RestException may or may not have underlying exception(s)
		logger.info("handleRestExceptions exception class: " + ExceptionUtils.getCause(ex).getClass().toString());
		//get the error string - it consists of the exception class name plus a code for looking up the message
		String code = ExceptionUtils.getMessage(ex);
		//ExceptionUtils prepends the error code with the exception class followed by ": "; need to remove this to look up the message
		code = code.substring(code.indexOf(":")+2, code.length());
		
		//get the message for this exception
		String msg = messages.getMessage(code, null, "Exception", locale);
		//log the exception in the error log file
		logger.error(ex.getClass().toString(), ex);
		//wrap the logService in case the db is down - do not want to throw another exception!
		try {
			logService.addException(ex);
		} catch (Exception excpt) {}

		//create a json object; not much in the object right now but it could contain more info in the future
		ResponseObject obj = new ResponseObject(msg, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		//return the ResponseObject as json (@ResponseBody) to make it available to the client, e.g., jqXHR.responseJSON
		return obj;
	}
}