package net.kear.recipeorganizer.controller;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AutoPopulatingList;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import net.kear.recipeorganizer.enums.ApprovalAction;
import net.kear.recipeorganizer.enums.UserAge;
import net.kear.recipeorganizer.exception.RestException;
import net.kear.recipeorganizer.persistence.dto.CommentDto;
import net.kear.recipeorganizer.persistence.dto.MaintenanceDto;
import net.kear.recipeorganizer.persistence.dto.QuestionDto;
import net.kear.recipeorganizer.persistence.dto.RecipeMessageDto;
import net.kear.recipeorganizer.persistence.dto.TopicDto;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeComment;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.model.UserMessage;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.UserMessageService;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.report.ReportGenerator;
import net.kear.recipeorganizer.security.AuthCookie;
import net.kear.recipeorganizer.security.UserSecurityService;
import net.kear.recipeorganizer.util.UserInfo;
import net.kear.recipeorganizer.util.db.ConstraintMap;
import net.kear.recipeorganizer.util.email.AccountChangeEmail;
import net.kear.recipeorganizer.util.email.EmailSender;
import net.kear.recipeorganizer.util.email.PasswordEmail;
import net.kear.recipeorganizer.util.email.RegistrationEmail;
import net.kear.recipeorganizer.util.email.ShareRecipeEmail;
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
	@Autowired
	private PasswordEmail passwordEmail; 
	@Autowired
	private AccountChangeEmail accountChangeEmail; 
	@Autowired
	private ShareRecipeEmail shareRecipeEmail;
	@Autowired
	private ServletContext servletContext;
	@Autowired
	private Environment env;
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private UserMessageService userMessageService;

	private static final int MAX_TOPICS = 25;
	private static final int MAX_QUESTIONS = 100;
	
	@RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
	public String getHome(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response, Locale locale) {
		logger.info("home GET");

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
	public String getFaq(Model model, Locale locale) {
		logger.info("FAQ GET");

		List<TopicDto> topics = new AutoPopulatingList<TopicDto>(TopicDto.class);
		List<QuestionDto> questions = new AutoPopulatingList<QuestionDto>(QuestionDto.class);
		
		String totalTopics = messages.getMessage("faq.topic.total", null, "0", locale);
		int totTopics = Integer.parseInt(totalTopics);
		
		int tNdx = 1;
		for (int ndx=1;;ndx++) {
			if ((tNdx > totTopics) || (tNdx > MAX_TOPICS))
				break;
			
			String currTopic = "faq.topic." + ndx + ".";
			String topicTotal = messages.getMessage(currTopic + "count", null, "", locale);
			if (StringUtils.isBlank(topicTotal))
				continue;
				
			int topicTot = Integer.parseInt(topicTotal);
			if (topicTot > 0) {
				TopicDto topic = new TopicDto();
				topic.setId(ndx);
				topic.setDescription(messages.getMessage(currTopic + "description", null, "", locale));
				topics.add(topic);
				tNdx++;
			}
		}

		if (!topics.isEmpty()) {
			TopicDto topic = topics.get(0); 
			int topicOne = topic.getId();
			String topicStr = "faq.topic." + topicOne + ".";
			String topicTotal = messages.getMessage(topicStr + "count", null, "0", locale);
			int topicTot = Integer.parseInt(topicTotal);
	
			int qNdx = 1;
			for (int ndx=1;;ndx++) {
				if ((qNdx > topicTot) || (qNdx > MAX_QUESTIONS))
					break;
				
				String question = messages.getMessage(topicStr + "question." + ndx, null, "", locale);
				if (StringUtils.isBlank(question))
					continue;
				String answer = messages.getMessage(topicStr + "answer." + ndx, null, "", locale);
				if (StringUtils.isBlank(answer))
					continue;
				
				QuestionDto questDto = new QuestionDto();
				questDto.setId(ndx);
				questDto.setQuestion(question);
				questDto.setAnswer(answer);
				questions.add(questDto);
				qNdx++;
			}
		}

		model.addAttribute("topics", topics);
		model.addAttribute("questions", questions);
				
		return "faq";
	}
	
	//NOTE: do NOT add @ResponseBody to this method since that will return a string instead of HTML
	@RequestMapping(value = "/questions/{topicId}", method = RequestMethod.GET)
	@ResponseStatus(value=HttpStatus.OK)
	public String getQuestions(Model model, @PathVariable Integer topicId, Locale locale, HttpServletResponse response) throws RestException {
		logger.info("questions/topic GET: topicId=" + topicId);

		List<QuestionDto> questions = new AutoPopulatingList<QuestionDto>(QuestionDto.class);
		
		String currTopic = "faq.topic." + topicId + ".";
		String topicTotal = messages.getMessage(currTopic + "count", null, "", locale);
		if (StringUtils.isBlank(topicTotal))
			return "questions";
		
		int topicTot = Integer.parseInt(topicTotal);
		String topicStr = "faq.topic." + topicId + ".";
		
		int qNdx = 1;
		for (int ndx=1;;ndx++) {
			if ((qNdx > topicTot) || (qNdx > MAX_QUESTIONS))
				break;
			
			String question = messages.getMessage(topicStr + "question." + ndx, null, "", locale);
			if (StringUtils.isBlank(question))
				continue;
			String answer = messages.getMessage(topicStr + "answer." + ndx, null, "", locale);
			if (StringUtils.isBlank(answer))
				continue;
			
			QuestionDto questDto = new QuestionDto();
			questDto.setId(ndx);
			questDto.setQuestion(question);
			questDto.setAnswer(answer);
			questions.add(questDto);
			qNdx++;
		}
		
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		model.addAttribute("questions", questions);
		
		return "questions";
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

	@RequestMapping(value = "/betatest", method = RequestMethod.GET)
	public String getBetaTest(Model model, Locale locale) {
		logger.info("betatest GET");

		return "betatest";
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
		
		/*MaintenanceDto maintDto = new MaintenanceDto();
		model.addAttribute("maintenanceDto", maintDto);
		model.addAttribute("dayMap", maintUtil.getWeekMap(locale));*/
		
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
		
		/*registrationEmail.init("Gene Kear", "kear.larry@gmail.com", locale);
		registrationEmail.setTokenUrl("/confirmRegistration?token=a72ad4cc-5772-4f5f-8fd6-9707aa85f920");
        registrationEmail.constructEmail();
    	emailSender.sendHtmlEmail(registrationEmail);

		passwordEmail.init("Larry Kear", "kear.larry@gmail.com", locale);
        String confirmationUrl = "/confirmPassword?id=131&token=a72ad4cc-5772-4f5f-8fd6-9707aa85f920";
		passwordEmail.setTokenUrl(confirmationUrl);
		passwordEmail.constructEmail();
    	emailSender.sendHtmlEmail(passwordEmail);
		
		accountChangeEmail.init("William Kear", "kear.larry@gmail.com", locale);
		accountChangeEmail.setChangeType(ChangeType.PASSWORD);
		accountChangeEmail.constructEmail();
    	emailSender.sendHtmlEmail(accountChangeEmail);

		accountChangeEmail.init("Peggy McKinney", "kear.larry@gmail.com", locale);
		accountChangeEmail.setChangeType(ChangeType.PROFILE);
		accountChangeEmail.constructEmail();
    	emailSender.sendHtmlEmail(accountChangeEmail);*/
		
    	/*reportGenerator.createRecipePDF(741L, locale);
    	reportGenerator.createRecipePDF(1463L, locale);
    	reportGenerator.createRecipePDF(1101L, locale);
    	reportGenerator.createRecipePDF(1522L, locale);
    	reportGenerator.createRecipePDF(1141L, locale);
    	reportGenerator.createRecipePDF(421L, locale);
    	reportGenerator.createRecipePDF(1462L, locale);*/
    	
    	/*
    	//shareRecipeEmail.init("Larry Kear", "kear.larry@gmail.com", locale);
    	shareRecipeEmail.init("Larry Kear", "lkear@outlook.com", locale);
		shareRecipeEmail.setSenderName("Larry Kear");
		shareRecipeEmail.setUserFirstName("Larry");
		shareRecipeEmail.setUserMessage("Hope you enjoy this recipe!");
		shareRecipeEmail.setRecipeName("Chicken Pot Pie for Two");
		shareRecipeEmail.setPdfAttached(true);
		shareRecipeEmail.setPdfFileName(pdfFileName);
		shareRecipeEmail.constructEmail();
    	emailSender.sendHtmlEmail(shareRecipeEmail);*/

		//reportGenerator.configureReports(servletContext, env);

		/*UserAge[] ages = UserAge.values();
		for (UserAge age : ages)
			logger.debug("ageValue:" + age);*/
		
		/*List<UserAge> ageList = UserAge.list();
		for (UserAge age : ageList)
			logger.debug("ageList:" + age);
		
		String age = UserAge.UA18TO30.toString();
		logger.debug("age:" + age);
				
		String[] strList = UserAge.strList();
		for (String strAge : strList)
			logger.debug("ageStrList:" + strAge);
		
		age = strList[UserAge.UA31TO50.getValue()];
		logger.debug("age:" + age);

		age = strList[UserAge.UA51TO70.ordinal()];
		logger.debug("age:" + age);*/

		/*User user = userService.getUser(5L);
		
		RecipeMessageDto recipeMessageDto = new RecipeMessageDto();
		model.addAttribute("recipeMessageDto", recipeMessageDto);
		model.addAttribute("approvalActions", ApprovalAction.list());

		UserMessage msg = new UserMessage();
		msg.setFromUserId(3L);
		msg.setToUserId(5L);
		msg.setRecipeId(null);
		msg.setViewed(false);
		msg.setMessage("Some regular text, then some html");
		msg.setHtmlMessage("<p>This is a message!</p><ul><li>Reason #1</li><li>Reason #2</li><li>Reason #3</li></ul><p>Some other message</p>");

		userMessageService.addMessage(msg);*/
		
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

