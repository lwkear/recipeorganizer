package net.kear.recipeorganizer.controller;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.exception.RestException;
import net.kear.recipeorganizer.exception.AccessUserException;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.util.ResponseObject;
import net.kear.recipeorganizer.util.view.CommonView;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.exception.JDBCConnectionException;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
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

	@ExceptionHandler(value={
			JRException.class,
			JRRuntimeException.class,
			SolrServerException.class,
			RemoteSolrException.class,
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
			JDBCConnectionException.class,
			CannotCreateTransactionException.class,
			InternalAuthenticationServiceException.class,
			ConnectException.class,
			PSQLException.class,
			AccessUserException.class,
			RememberMeAuthenticationException.class
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
		
		Object[] msgobj = new Object[] {null};
		msgobj[0] = (Object) messages.getMessage("exception.common.tryagain", null, "", locale);
		
		//get the message for this exception
		String msg = messages.getMessage(code, msgobj, "", locale);
		if (StringUtils.isEmpty(msg))
			msg = messages.getMessage("exception.restDefault", null, "Houston, we have a problem!", locale);
		
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


/*
2016-03-29 10:34:08,770 DEBUG: net.kear.recipeorganizer.util.email.PasswordEmail - construct PasswordEmail
2016-03-29 10:34:10,330 INFO : net.kear.recipeorganizer.controller.ExceptionController - handleGeneralExceptions exception class: class org.springframework.mail.MailSendException
2016-03-29 10:34:10,342 DEBUG: net.kear.recipeorganizer.controller.ExceptionController - handleGeneralExceptions msg: MailSendException: Mail server connection failed; nested exception is javax.mail.MessagingException: Could not connect to SMTP host: localhost, port: 587;
  nested exception is:
	java.net.ConnectException: Connection refused: connect. Failed messages: javax.mail.MessagingException: Could not connect to SMTP host: localhost, port: 587;
  nested exception is:
	java.net.ConnectException: Connection refused: connect
2016-03-29 10:34:10,342 ERROR: net.kear.recipeorganizer.controller.ExceptionController - class org.springframework.mail.MailSendException

CannotCreateTransactionException.class,	Could not open Hibernate Session for transaction
GenericJDBCException.class,				Unable to acquire JDBC Connection
SQLException.class,						Cannot create PoolableConnectionFactory
PSQLException.class,					Connection to localhost:5432 refused
ConnectException.class					Connection refused: connect

** try to connect to hubbard.reciporganizer.net
MailConnectException.class,	Couldn't connect to host, port: 127.0.0.1, 25
SocketException.class		Permission denied: connect

** remember me cookie **
InternalAuthenticationServiceException.class,	Could not open Hibernate Session for transaction
CannotCreateTransactionException.class,			Could not open Hibernate Session for transaction
GenericJDBCException.class,						Unable to acquire JDBC Connection
Cannot create PoolableConnectionFactory.class,	Connection to localhost:5432 refused
PSQLException.class,							Connection to localhost:5432 refused
ConnectException.class							Connection refused: connect
*/

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
