package net.kear.recipeorganizer.util.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.interceptor.MaintenanceInterceptor;
import net.kear.recipeorganizer.util.CookieUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CommonViewImpl implements CommonView {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private Environment env;
	@Autowired
	private MessageSource messages;
	@Autowired
	private CookieUtil cookieUtil;
	@Autowired
	private MaintenanceInterceptor maintInterceptor; 

	public ModelAndView getStandardErrorPage(Exception ex) {
		//log the error in the log file
		logger.error(ex.getClass().toString(), ex);
		logger.debug("getErrorPage exception simpleName: " + ex.getClass().getSimpleName());
		
		Locale locale = LocaleContextHolder.getLocale();

		//default exception
		String msgCode = "exception." + ex.getClass().getSimpleName();
    	Object[] obj = new Object[] {null};
    	obj[0] = (Object) env.getProperty("company.email.support.account");
		
    	ModelAndView mv = new ModelAndView("/error");
    	mv.setViewName("/error");
    	
		List<String> errorMsgs = new ArrayList<String>();
		String msg = messages.getMessage(msgCode, obj, null, locale);
		if (msg == null) {
			//use the generic "testers missed something" message - display system instead of error page
			msg = messages.getMessage("exception.500", obj, null, locale);
			mv.setViewName("/system");
		}
		errorMsgs.add(msg);		
		mv.addObject("errorMsgs", errorMsgs);
		
		return mv;
	}
	
	public ModelAndView getSystemErrorPage(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		//the database is down so log out the user to prevent further errors;
		//must delete the rememberMe cookie or otherwise Spring Security will try to access the db while automatically logging the user in 
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
			cookieUtil.deleteRememberMe(request, response);
			cookieUtil.setAuthCookie(request, response, CookieUtil.ANNON_USER);
		}

		List<String> errorMsgs = new ArrayList<String>();
		errorMsgs.add(messages.getMessage("exception.databaseDown", null, "", locale));
		errorMsgs.add(messages.getMessage("exception.common.apologize", null, "", locale));		
		errorMsgs.add(messages.getMessage("exception.common.comebacklater", null, "", locale));
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("errorMsgs", errorMsgs);
        mv.setViewName("/system");
		
        return mv;
	}

	public ModelAndView getMaintenancePage(Locale locale) {
		
		List<String> systemMsgs = new ArrayList<String>();
		systemMsgs.add(messages.getMessage("sysmaint.message1", null, "", locale));
		systemMsgs.add(maintInterceptor.getMaintEnd(locale));
		systemMsgs.add(messages.getMessage("sysmaint.message3", null, "", locale));		
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("systemMsgs", systemMsgs);
        mv.setViewName("/sysmaint");
		
		return mv;
	}
}
