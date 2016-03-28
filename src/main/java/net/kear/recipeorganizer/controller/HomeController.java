package net.kear.recipeorganizer.controller;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.kear.recipeorganizer.persistence.dto.MaintenanceDto;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.report.ReportGenerator;
import net.kear.recipeorganizer.security.AuthCookie;
import net.kear.recipeorganizer.util.UserInfo;
import net.kear.recipeorganizer.util.db.ConstraintMap;
import net.kear.recipeorganizer.util.email.EmailSender;
import net.kear.recipeorganizer.util.email.RegistrationEmail;
import net.kear.recipeorganizer.util.maint.MaintenanceUtil;
import net.kear.recipeorganizer.util.view.CommonView;

@Controller
public class HomeController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AuthCookie authCookie;
	@Autowired
	private UserInfo userInfo;
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	private MessageSource messages;
	@Autowired
	private UserService userService;
	@Autowired
	private ConstraintMap constraintMap;
	@Autowired
	private CommonView commonView;
	@Autowired
	MaintenanceUtil maintUtil;

	@Autowired
	private ReportGenerator reportGenerator; 
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private RegistrationEmail registrationEmail; 

	@RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
	public String getHome(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		logger.info("home GET");

		Date createTime = new Date(session.getCreationTime());
		Date lastAccess = new Date(session.getLastAccessedTime());
		int maxInactive = session.getMaxInactiveInterval();
		String sessID = session.getId();

		logger.debug("Session created on: " + createTime);
		logger.debug("Session last accessed on: " + lastAccess);
		logger.debug("Session expires after: " + maxInactive + " seconds");
		logger.debug("Session ID: " + sessID);

		List<Object> allPrinc = sessionRegistry.getAllPrincipals();
		for (Object obj : allPrinc) {
			final List<SessionInformation> sessions = sessionRegistry.getAllSessions(obj, true);

			for (SessionInformation sess : sessions) {
				Object princ = sess.getPrincipal();
				String sessId = sess.getSessionId();
				Date sessDate = sess.getLastRequest();
				
				logger.debug("sessionRegistry.princ: " + princ);
				logger.debug("sessionRegistry.sessId: " + sessId);
				logger.debug("sessionRegistry.sessDate: " + sessDate.toString());
			}
		}

		Enumeration<String> attrNames = session.getAttributeNames();
		while (attrNames.hasMoreElements())
			logger.debug("getHome: attrNames " + attrNames.nextElement());
		
		Object token = session.getAttribute("org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN");
		if (token != null)
			logger.debug("getHome: csrf token: " + token.toString());
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("security context auth: " + auth.toString());
		
		//tell the page to not include the white vertical filler
		model.addAttribute("vertFiller", "1");
		
		if (!authCookie.cookieExists(request))
			authCookie.setCookie(request, response, userInfo.getName());
		
		return "home";
	}

	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String getAbout(Model model, HttpSession session) {
		logger.info("about GET");
		
		return "about";
	}

	@RequestMapping(value = "/thankyou", method = RequestMethod.GET)
	public String getThankyou(Model model, HttpSession session) {
		logger.info("thankyou GET");
	
		//tell the page to not include the white vertical filler
		model.addAttribute("vertFiller", "1");
		
		return "thankyou";
	}
	
	@RequestMapping(value = "/faq", method = RequestMethod.GET)
	public String getFaq(Model model) {
		logger.info("FAQ GET");
		
		return "faq";
	}
	
	@RequestMapping(value = "/technical", method = RequestMethod.GET)
	public String getTechnical(Model model) {
		logger.info("technical GET");
		
		return "technical";
	}

	@RequestMapping(value = "/contact", method = RequestMethod.GET)
	public String getContact(Model model) {
		logger.info("contact GET");
				
		return "contact";
	}
	
	@RequestMapping(value = "/policies", method = RequestMethod.GET)
	public String getPolicies(Model model) {
		logger.info("policies GET");
				
		return "policies";
	}

	@RequestMapping(value = "/sysmaint", method = RequestMethod.GET)
	public ModelAndView getMaintenance(Model model, Locale locale) {
		logger.info("sysmaint GET");

		return commonView.getMaintenancePage(locale);
	}
	
	//AJAX/JSON request for getting the timeout interval for the current user
	@RequestMapping(value="/getSessionTimeout")
	@ResponseBody 
	public Integer getSessionTimeout(HttpSession session, HttpServletResponse response) {
		
		Integer maxInactive = session.getMaxInactiveInterval();
		
		logger.debug("getSessionTimeout returned " + maxInactive + " seconds");
		
		return maxInactive;
	}

	//AJAX/JSON request for resetting the session timeout
	@RequestMapping(value="/setSessionTimeout", method = RequestMethod.POST)
	@ResponseBody 
	public String setSessionTimeout(HttpSession session, HttpServletResponse response) {
		
		response.setStatus(HttpServletResponse.SC_OK);
		
		Integer maxInactive = session.getMaxInactiveInterval();
		session.setMaxInactiveInterval(maxInactive);
		
		logger.debug("setSessionTimeout for " + maxInactive + " seconds");
		
		return "{}";
	}
	
	
	/*****************/
	/*** test page ***/
	/*****************/
	@RequestMapping(value = "/test/testpage", method = RequestMethod.GET)
	public String getTestpage(Model model, HttpServletRequest request, Locale locale) {
		logger.debug("getTestpage");

		/*String text = "celery, and ½ teaspoon salt";
		model.addAttribute("text", text);
		
		WebGreeting wg = new WebGreeting();
		model.addAttribute("webGreeting", wg);

        model.addAttribute("message", messages.getMessage("user.register.sentToken", null, "Token sent", locale));

		User currentUser = (User)userInfo.getUserDetails();
		
		User user = null;
		try {
			user = userService.getUser(currentUser.getId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}
        
		//Date lastLogin = user.getLastLogin();
		//if (lastLogin == null)
		Date lastLogin = user.getDateAdded();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(lastLogin);
		cal.add(Calendar.DATE, 365);
		
		Calendar tdy = Calendar.getInstance();
		
        if (tdy.getTime().getTime() > cal.getTime().getTime())
        	model.addAttribute("expired", "account has expired");
        else
        	model.addAttribute("expired", "account valid");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fmt = sdf.format(lastLogin);
        logger.debug("lastLogin: " + fmt);
        fmt = sdf.format(cal.getTime());
        logger.debug("lastLogin + 365: " + fmt);
        fmt = sdf.format(tdy.getTime());
        logger.debug("today: " + fmt);*/
        
		//reportGenerator.createRecipePDF(421L);
		
		MaintenanceDto maintDto = new MaintenanceDto();
		model.addAttribute("maintenanceDto", maintDto);
		model.addAttribute("dayMap", maintUtil.getWeekMap(locale));
		
		/*User user = (User)userInfo.getUserDetails();
        emailSender.setUser(user);
     	emailSender.setLocale(request.getLocale());
     	emailSender.setSubjectCode("user.email.signupSubject");
     	emailSender.setMessageCode("user.email.signupSuccess");

     	try {
			emailSender.sendFreemarkeMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		//registrationEmail.init("Larry Kear", "lkear@outlook.com", locale);
		/*registrationEmail.init("Peggy McKinney", "pmckinney1943@att.net", locale);*/
		registrationEmail.init("Gene Kear", "gene@kear.co", locale);
		registrationEmail.setTokenUrl("/confirmRegistration?token=123456");
        registrationEmail.constructEmail();
    	emailSender.sendTokenEmail(registrationEmail);
		
		return "test/testpage";
	}

	@RequestMapping(value = "/test/testpage", method = RequestMethod.POST)
	public String postTestpage(@ModelAttribute @Valid WebGreeting wb, BindingResult result) {
		if (result.hasErrors()) {
			logger.debug("Validation errors");
			return "test/testpage";
		}
		
		logger.debug("getTestpage");
		logger.debug("wb.greeting:" + wb.getGreeting());
		
		return "test/testpage";
	}
	
	public static class WebGreeting {
		
		//@Size(max=20)
		private String greeting;

		public WebGreeting() {}

		public String getGreeting() {
			return greeting;
		}

		public void setGreeting(String greeting) {
			this.greeting = greeting;
		}
	}
}


/*
	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public String getStartpage(Model model) {
		logger.debug("getStartpage");

		return "start";
	}*/

/*
UserDto user = new UserDto();
//user.setEmail("ilsa@gmail.com");
user.setEmail(null);
user.setFirstName("Ilsa");
user.setLastName("Kear");
user.setPassword("$2a$10$btkjPF8CVqS1v5W8Hh5qrujLSTyhVAXAA5YHbEm2lP1m3lp46DMnC");

try {
	userService.addUser(user);
} catch (Exception ex) {
	logger.debug("exception class: " + ex.getClass().toString());
	String msg = ExceptionUtils.getMessage(ex);
	logger.debug("msg: " + msg);
	Throwable excptn = ExceptionUtils.getRootCause(ex);
	if (excptn != null) {
		msg = ExceptionUtils.getRootCause(ex).getClass().toString();
		logger.debug("root class: " + msg);
		msg = ExceptionUtils.getRootCauseMessage(ex);
		logger.debug("root msg: " + msg);
	}
}

try {
	userService.deleteUser(78L);
} catch (Exception ex) {
	logger.debug("exception class: " + ex.getClass().toString());
	String msg = ExceptionUtils.getMessage(ex);
	logger.debug("msg: " + msg);
	Throwable excptn = ExceptionUtils.getRootCause(ex);
	if (excptn != null) {
		msg = ExceptionUtils.getRootCause(ex).getClass().toString();
		logger.debug("root class: " + msg);
		msg = ExceptionUtils.getRootCauseMessage(ex);
		logger.debug("root msg: " + msg);
	}
}			
*/

/*

		logger.debug("1 valid: " + isValid("1"));
		logger.debug("1/ valid: " + isValid("1/"));
		logger.debug("1/2 valid: " + isValid("1/2"));
		logger.debug("1. valid: " + isValid("1."));
		logger.debug("1.1 valid: " + isValid("1.1"));
		logger.debug("1, valid: " + isValid("1,"));
		logger.debug("a valid: " + isValid("a"));
		logger.debug("1a valid: " + isValid("1a"));
		logger.debug("1 1/2 valid: " + isValid("1 1/2"));
		logger.debug("a1 valid: " + isValid("a1"));
		logger.debug("/ valid: " + isValid("/"));
		logger.debug(". valid: " + isValid("."));
		logger.debug(", valid: " + isValid(","));
		
public boolean isValid(String qty) {
	String str = qty;
	str = str.trim();
	
	if (str == null) {
        return false;
    }
    int length = str.length();
    if (length == 0) {
        return false;
    }
    if ((str.charAt(0) == '/') || (str.charAt(0) == ',') || (str.charAt(0) == '.')) {
        if (length == 1) {
            return false;
        }
    }
    for (int i=0; i < length; i++) {
        char c = str.charAt(i);
        if ((c < '0' || c > '9') && (c != '/' && c != '.' && c != ',' && c != ' ')) {
        	return false;
        }
    }
    for (int i=0; i < length; i++) {
        char c = str.charAt(i);
        if ((c == '/') && (i+1 >= length))
        	return false;
    }
    
    Fraction fract;
    str = str.replace(',', '.');
    try {
    	fract = Fraction.getFraction(str);
    } catch (NumberFormatException ex) {
    	logger.debug("Exception: " + qty);
    	return false;
    }
    
	float value = fract.floatValue();
    
    return true;
}

		//Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", UserDto.class); 
		//model.addAttribute("sizeMap", sizeMap);

		/*Map<String, Object> sizeMap = constraintMap.getModelConstraints("Size", "max", 
				new Class[] {Recipe.class, RecipeIngredient.class, Ingredient.class, Source.class, InstructionSection.class, 
							IngredientSection.class});
							
*/ 

