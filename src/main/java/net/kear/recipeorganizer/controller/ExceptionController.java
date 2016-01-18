package net.kear.recipeorganizer.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.kear.recipeorganizer.persistence.service.ExceptionLogService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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
	*/	
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(Exception ex) {
		logger.info("Exception: " + ex.getClass().toString());
		return getErrorPage(ex);
	}

	private ModelAndView getErrorPage(Exception ex) {
		logService.addException(ex);
		
		Locale locale = LocaleContextHolder.getLocale();
		
		String msgCode = "exception.Exception";
		String classStr[] = ex.getClass().toString().split("[.]");
		if (classStr.length > 0)
			msgCode = "exception." + classStr[classStr.length-1];
		
		List<String> errorMsgs = new ArrayList<String>();
		errorMsgs.add(messages.getMessage(msgCode, null, ex.getClass().getName(), locale));
		
		ModelAndView view = new ModelAndView("/errors/errorMessage");
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
