package net.kear.recipeorganizer.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;

import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import net.kear.recipeorganizer.enums.FileType;
import net.kear.recipeorganizer.enums.MessageType;
import net.kear.recipeorganizer.enums.OptOutType;
import net.kear.recipeorganizer.enums.UserAge;
import net.kear.recipeorganizer.event.PasswordResetEvent;
import net.kear.recipeorganizer.event.RegistrationCompleteEvent;
import net.kear.recipeorganizer.event.UserMessageEvent;
import net.kear.recipeorganizer.exception.AccessProfileException;
import net.kear.recipeorganizer.exception.AccessUserException;
import net.kear.recipeorganizer.exception.AddUserException;
import net.kear.recipeorganizer.exception.OptOutException;
import net.kear.recipeorganizer.exception.PasswordResendException;
import net.kear.recipeorganizer.exception.PasswordResetException;
import net.kear.recipeorganizer.exception.RestException;
import net.kear.recipeorganizer.exception.SaveAccountException;
import net.kear.recipeorganizer.exception.VerificationException;
import net.kear.recipeorganizer.exception.VerificationResendException;
import net.kear.recipeorganizer.interceptor.MaintenanceInterceptor;
import net.kear.recipeorganizer.persistence.dto.ChangeEmailDto;
import net.kear.recipeorganizer.persistence.dto.ChangeEmailDto.ChangeEmailDtoSequence;
import net.kear.recipeorganizer.persistence.dto.ChangeNameDto;
import net.kear.recipeorganizer.persistence.dto.ChangeNameDto.ChangeNameDtoSequence;
import net.kear.recipeorganizer.persistence.dto.ChangeNotificationDto;
import net.kear.recipeorganizer.persistence.dto.ChangePasswordDto;
import net.kear.recipeorganizer.persistence.dto.ChangePasswordDto.ChangePasswordDtoSequence;
import net.kear.recipeorganizer.persistence.dto.NewPasswordDto;
import net.kear.recipeorganizer.persistence.dto.NewPasswordDto.NewPasswordDtoSequence;
import net.kear.recipeorganizer.persistence.dto.RecipeDisplayDto;
import net.kear.recipeorganizer.persistence.dto.UserDto;
import net.kear.recipeorganizer.persistence.dto.UserDto.UserDtoSequence;
import net.kear.recipeorganizer.persistence.dto.UserMessageDto;
import net.kear.recipeorganizer.persistence.model.PasswordResetToken;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.model.UserMessage;
import net.kear.recipeorganizer.persistence.model.UserProfile;
import net.kear.recipeorganizer.persistence.model.VerificationToken;
import net.kear.recipeorganizer.persistence.service.CommentService;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.persistence.service.IngredientService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.UserMessageService;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.security.LoginAttemptService;
import net.kear.recipeorganizer.security.UserSecurityService;
import net.kear.recipeorganizer.util.CookieUtil;
import net.kear.recipeorganizer.util.EncryptionUtil;
import net.kear.recipeorganizer.util.ResponseObject;
import net.kear.recipeorganizer.util.SpeechUtil;
import net.kear.recipeorganizer.util.UserInfo;
import net.kear.recipeorganizer.util.db.ConstraintMap;
import net.kear.recipeorganizer.util.email.AccountChangeEmail;
import net.kear.recipeorganizer.util.email.EmailDetail;
import net.kear.recipeorganizer.util.email.EmailSender;
import net.kear.recipeorganizer.util.email.AccountChangeEmail.ChangeType;
import net.kear.recipeorganizer.util.email.NewMessageEmail;
import net.kear.recipeorganizer.util.file.FileActions;
import net.kear.recipeorganizer.util.file.FileConstant;
import net.kear.recipeorganizer.util.file.FileResult;
import net.kear.recipeorganizer.util.maint.MaintAware;

@Controller
public class UserController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private Environment env;
	@Autowired
	private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
	@Autowired
	private UserSecurityService userSecurityService;
	@Autowired
	private LoginAttemptService loginAttemptService;
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private IngredientService ingredientService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private UserMessageService userMessageService;
	@Autowired
	private MessageSource messages;
	@Autowired
    private ApplicationEventPublisher eventPublisher;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private UserInfo userInfo;
	@Autowired
	private FileActions fileAction;
	@Autowired
	private CookieUtil cookieUtil;
	@Autowired
	private ExceptionLogService logService;
	@Autowired
	private ConstraintMap constraintMap;
	@Autowired
	private MaintenanceInterceptor maintInterceptor; 
	@Autowired
	private AccountChangeEmail accountChangeEmail;
	@Autowired
	private NewMessageEmail newMessageEmail;
	@Autowired
	PropertiesFactoryBean properties;
	@Autowired
	EncryptionUtil encryptUtil;
	@Autowired
	SpeechUtil speechUtil;
	
    /*********************/
    /*** Login handler ***/
    /*********************/
	@MaintAware
	@RequestMapping(value = "user/login", method = RequestMethod.GET)
	public String getLogin(Model model) {
		logger.info("user/login GET");
		
		return "user/login";
	}

	@MaintAware
	@RequestMapping(value = "user/loginError", method = RequestMethod.GET)
	public ModelAndView handleLoginError(RedirectAttributes redir, HttpServletRequest request, HttpSession session, Locale locale) {
		logger.info("user/loginError GET");
	
		String authExClass = "";
		String msg = "Unknown error";
		AuthenticationException authEx = (AuthenticationException)request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

		if (authEx != null) {
			//many security messages include the email.support.account argument, so just get the property and pass it to all messages 
			Object[] obj = new Object[] {null,null};
        				
			//two messages also require the site context
			authExClass = authEx.getClass().getSimpleName();
			if ((StringUtils.equals(authExClass, "AccountExpiredException")) ||
				(StringUtils.equals(authExClass, "CredentialsExpiredException"))) {
				obj[0] = request.getContextPath();	//getServletPath();
				obj[1] = (Object) env.getProperty("company.email.support.account");
			}
			else
				obj[0] = (Object) env.getProperty("company.email.support.account");
			
			msg = messages.getMessage("exception." + authExClass, obj, "Login error", locale);
			
			if (ExceptionUtils.indexOfThrowable(authEx, BadCredentialsException.class) != -1) {
				String userName = (String) session.getAttribute("LAST_USERNAME");
				if (userName != null) {
					int attempts = loginAttemptService.getAttempts(userName);
					redir.addFlashAttribute("attempts", attempts);
				}
			}
	    }
	    
		ModelAndView mv = new ModelAndView("user/login");
        redir.addFlashAttribute("error", msg);
        mv.setViewName("redirect:/user/login");
        return mv;
	}
	
	@RequestMapping(value = "user/fatalError", method = RequestMethod.GET)
	public void handleLoginFatalError(HttpServletRequest request) throws AuthenticationException {
		logger.info("user/fatalError GET");
	
		//most authentication exceptions are caught in the custom AuthenticationFailureHandler class
		//those that are not need to be passed on to the @ControllerAdvice ExceptionController for further handling
		AuthenticationException authEx = (AuthenticationException)request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		throw authEx;
	}

	@RequestMapping(value = "user/newMember", method = RequestMethod.GET)
	public String getNewMember(Model model) {
		logger.info("user/newMember GET");
		
		User user = (User)userInfo.getUserDetails();
		model.addAttribute("user", user);
		
		return "user/newMember";
	}

	/****************************/
	/*** Registration handler ***/
	/****************************/
	@MaintAware
	@RequestMapping(value = "user/signup", method = RequestMethod.GET)
	public String getSignup(Model model) {
		logger.info("user/signup GET");
		
		//TODO: PRODUCTION: restore this after beta testing is completed
		/*UserDto user = new UserDto();
		user.setSubmitRecipes(true);
		user.setEmailAdmin(true);
		user.setEmailRecipe(true);
		user.setEmailMessage(true);
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", UserDto.class); 
		model.addAttribute("sizeMap", sizeMap);
		model.addAttribute("userDto", user);		
		
		return "user/signup";*/
		
		return "betatest";
	}
	
	@MaintAware
	@RequestMapping(value = "user/signup", method = RequestMethod.POST)
	public ModelAndView postSignup(@ModelAttribute @Validated(UserDtoSequence.class) UserDto userDto, 
			BindingResult result, HttpServletRequest request, RedirectAttributes redir, Locale locale) throws AddUserException {
		logger.info("user/signup POST: email=" + userDto.getEmail());

		ModelAndView mv = new ModelAndView("user/signup");
		
		//must re-add attribute(s) in case of an error
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", UserDto.class); 
		
		if (result.hasErrors()) {
			mv.addObject("sizeMap", sizeMap);
			logger.debug("Validation errors");
			return mv;
		}
		
		//double-check the email isn't in use in case user ignored the AJAX error
		boolean exists = false;
		try {
			exists = userService.doesUserEmailExist(userDto.getEmail());
		} catch (Exception ex) {
			//do nothing - if there is a problem with the database the user will be notifed when they submit the form
			logService.addException(ex);
		}

		if (exists) {
			logger.debug("Validation errors");
			String msg = messages.getMessage("user.duplicateEmail", null, "Duplicate email", locale);
			FieldError err = new FieldError("userDto", "email", userDto.getEmail(), false, null, null, msg);
			result.addError(err);
			mv.addObject("sizeMap", sizeMap);
			return mv;
		}

		User user = null;
		try {
			user = userService.addUser(userDto);
		} catch (Exception ex) {
			throw new AddUserException(ex);
		}
		
       	logger.debug("user added - publishing event");
       	eventPublisher.publishEvent(new RegistrationCompleteEvent(user, locale, null));
        
       	Object[] obj = new String[] {null};
       	redir.addFlashAttribute("title", messages.getMessage("registration.title", null, "Success", locale));
       	obj[0] = user.getFirstName() + " " + user.getLastName();
		String msg = messages.getMessage("user.register.header.sent", obj, "", locale);
		redir.addFlashAttribute("msgHeader", msg);
		List<String> msgList = new ArrayList<String>();
        msg = messages.getMessage("user.register.sentToken", null, "Token sent", locale);
        msgList.add(msg);
        redir.addFlashAttribute("messages", msgList);
        mv.setViewName("redirect:/message");
        
        return mv;
	}

	//AJAX/JSON request for checking user (email) duplication
	@RequestMapping(value="/lookupUser", method = RequestMethod.GET)
	@ResponseBody
	public ResponseObject lookupUser(@RequestParam("email") String lookupEmail, HttpServletResponse response, Locale locale) throws RestException {
		logger.info("lookupUser GET: email=" + lookupEmail);
		
		//query the DB for the user
		boolean result = false;
		try {
			result = userService.doesUserEmailExist(lookupEmail);
		} catch (Exception ex) {
			throw new RestException("exception.restDefault", ex);
		}
		
		logger.debug("lookupEmail result=" + result);
	
		ResponseObject obj = new ResponseObject();
		response.setStatus(HttpServletResponse.SC_OK);
		
		//name was found
		if (result) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			obj.setStatus(HttpServletResponse.SC_CONFLICT);
			String msg = messages.getMessage("user.duplicateEmail", null, "Duplicate email", locale);
			obj.setMsg(msg);
		}

		return obj;
	}

	//respond to user click on email link
	@RequestMapping(value = "/confirmRegistration", method = RequestMethod.GET)
	public ModelAndView confirmRegistration(@RequestParam("token") final String token, RedirectAttributes redir, Locale locale) throws VerificationException, SaveAccountException {
		logger.info("confirmRegistration GET");		
		
		ModelAndView mv = new ModelAndView();
	
		User user = null;
		VerificationToken verificationToken = null;
		try {
			verificationToken = userService.getVerificationToken(token);
		} catch (Exception ex) {
			throw new VerificationException(ex);
		}
        
        if (verificationToken != null) {
        	user = verificationToken.getUser();
        }
        
		Object[] obj = new Object[] {null};
        if (verificationToken == null || user == null) {
        	logger.debug("invalid token");
    		String msg = messages.getMessage("user.register.header.invalid", null, "", locale);
    		redir.addFlashAttribute("msgHeader", msg);
    		List<String> msgList = new ArrayList<String>();
    		obj[0] = (Object) env.getProperty("company.email.support.account");
        	msg = messages.getMessage("user.register.invalidToken", obj, "Invalid token", locale);
            msgList.add(msg);
            redir.addFlashAttribute("messages", msgList);
        	redir.addFlashAttribute("register", true);
        	redir.addFlashAttribute("invalid", true);
        	mv.setViewName("redirect:/user/tokenError");
        	return mv;     	
        }

        //the user may have already clicked on the link once which would have enabled them; if that's the case just display the login,
        //even if the token has expired
        //TODO: SECURITY: test what happens if the user record has been deleted
        if (user.isEnabled()) {
            logger.debug("user already enabled");
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        	logger.debug("token expired");
    		String msg = messages.getMessage("user.register.header.expired", null, "", locale);
    		redir.addFlashAttribute("msgHeader", msg);
    		List<String> msgList = new ArrayList<String>();
        	msg = messages.getMessage("user.register.expiredToken", null, "Expired token", locale);
            msgList.add(msg);
            redir.addFlashAttribute("messages", msgList);
        	redir.addFlashAttribute("register", true);
        	redir.addFlashAttribute("expired", true);
        	redir.addFlashAttribute("token", token);
        	mv.setViewName("redirect:/user/tokenError");
        	return mv;
        }

        user.setEnabled(1);
        try {
        	userService.updateUser(user);
        	//based upon confusion during beta testing deleting the token is just not working
        	//a couple of users clicked on the email link a second time and got an invalid token message, even though they completed the registration
        	//when they clicked on it the first time;  the token is required to look up the user to check if they are already enabled, in which case 
        	//they should be presented with the login screen
        	//userService.deleteVerificationToken(verificationToken);
        } catch (Exception ex) {
        	throw new SaveAccountException(ex);
        }
        logger.debug("user updated");
        
        mv.setViewName("redirect:/user/login");
        return mv;
	}
	
	//resend a registration email
	@RequestMapping(value = "/user/resendRegistrationToken", method = RequestMethod.GET)
    public ModelAndView resendRegistrationToken(@RequestParam("token") final String token, final HttpServletRequest request, RedirectAttributes redir, Locale locale)  {
		logger.info("user/resendRegistrationToken GET");
		
		ModelAndView mv = new ModelAndView();

		User user = null;
		try {
	        user = userService.getVerificationUser(token);
		} catch (Exception ex) {
        	throw new VerificationResendException(ex);
        }
		
       	logger.debug("new token requested - publishing event");
       	eventPublisher.publishEvent(new RegistrationCompleteEvent(user, locale, token));

       	Object[] obj = new String[] {null};
       	redir.addFlashAttribute("title", messages.getMessage("registration.title", null, "Successful registration", locale));
       	obj[0] = user.getFirstName() + " " + user.getLastName();
		String msg = messages.getMessage("user.register.header.sent", obj, "", locale);
		redir.addFlashAttribute("msgHeader", msg);
		List<String> msgList = new ArrayList<String>();
        msg = messages.getMessage("user.register.sentNewToken", null, "Token sent", locale);
        msgList.add(msg);
        redir.addFlashAttribute("messages", msgList);
        mv.setViewName("redirect:/message");
        return mv;
    }
	
	/******************************************/
	/*** Profile and Account upgrade handler ***/
	/******************************************/
	@MaintAware
	@RequestMapping(value = "user/profile", method = RequestMethod.GET)
	public String getProfile(Model model, Locale locale) throws AccessUserException, AccessProfileException {
		logger.info("user/profile GET");
		
		User currentUser = (User)userInfo.getUserDetails();

		User user = null;
		try {
			user = userService.getUser(currentUser.getId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}
		
		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (user == null) {
			throw new AccessUserException();
		}		
		
		UserProfile userProfile = user.getUserProfile();
		if (userProfile == null) {
			userProfile = new UserProfile();
			userProfile.setUser(user);
		}
		
		List<Voice> voices = speechUtil.getVoices(locale);
		
		if (userInfo.isUserRole(Role.TYPE_GUEST))
			userProfile.setSubmitRecipes(false);
		
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", UserProfile.class); 
		model.addAttribute("sizeMap", sizeMap);
		model.addAttribute("userProfile", userProfile);
		String rangeDescription = messages.getMessage("profile.nevermind", null, "Default", locale);
		UserAge.UANEVERMIND.setDescription(rangeDescription);		
		model.addAttribute("ageRanges", UserAge.values());
		model.addAttribute("voices", voices);
		
		return "user/profile";
	}

	@MaintAware
	@RequestMapping(value = "user/profile", method = RequestMethod.POST)
	public String postProfile(Model model, @ModelAttribute @Valid UserProfile userProfile, BindingResult result, Locale locale,
			@RequestParam(value = "file", required = false) MultipartFile file, HttpSession session) throws AccessUserException, SaveAccountException {
		logger.info("user/profile POST: user=" + userProfile.getUser().getId());

		//must re-add attribute(s) in case of an error
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", UserProfile.class); 
		model.addAttribute("sizeMap", sizeMap);

		if (result.hasErrors()) {
			logger.debug("Validation errors");
			return "user/profile";
		}

		User user = null;
		try {
			user = userService.getUser(userProfile.getUser().getId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}
		
		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (user == null) {
			throw new AccessUserException();
		}		

		if (file != null) {
			FileResult rslt = fileAction.uploadFile(FileType.AVATAR, user.getId(), file);
			if (rslt == FileResult.SUCCESS) {
				String currAvatar = userProfile.getAvatar();
				if (!StringUtils.isBlank(currAvatar)) {
					String newAvatar = file.getOriginalFilename();
					if (!currAvatar.equals(newAvatar))
						fileAction.deleteFile(FileType.AVATAR, user.getId(), currAvatar);
				}
				userProfile.setAvatar(file.getOriginalFilename());
			}
			else {
				if (rslt != FileResult.NO_FILE) {
					String msg = fileAction.getErrorMessage(rslt, locale);
					FieldError fieldError = new FieldError("userProfile", "avatar", msg);
					result.addError(fieldError);
					return "user/profile";
				}
			}
		}
		
		String avatarName = userProfile.getAvatar();
		if (avatarName!= null && avatarName.startsWith(FileConstant.REMOVE_PHOTO_PREFIX)) {
			String name = avatarName.substring(12);
			//errors are not fatal and will be logged by FileAction
			fileAction.deleteFile(FileType.AVATAR, user.getId(), name);
			userProfile.setAvatar("");
		}

		//set the user to this profile
		user.setUserProfile(userProfile);		
		
		try {		
			userService.saveUserProfile(userProfile);
			if (userInfo.isUserRole(Role.TYPE_GUEST)) {
				if (userProfile.getSubmitRecipes()) {
					//reload the user's authentication with the AUTHOR role
					userService.changeRole(Role.TYPE_AUTHOR, user);
					userSecurityService.reauthenticateUser(user);
				}
			}
		} catch (Exception ex) {
			throw new SaveAccountException(ex);
		}
		
		if (user.isEmailAdmin()) {
			String userName = user.getFirstName() + " " + user.getLastName();
	
	        EmailDetail emailDetail = new EmailDetail(userName, user.getEmail(), locale);
			emailDetail.setChangeType(ChangeType.PROFILE);
	        String idStr = String.valueOf(user.getId());
			String userIdStr = encryptUtil.encryptURLParam(idStr);
			String msgTypeStr = encryptUtil.encryptURLParam(OptOutType.ACCOUNT.name());
			String optoutUrl = "/user/optout?id=" + userIdStr + "&type=" + msgTypeStr;
			emailDetail.setOptoutUrl(optoutUrl);

			try {
				accountChangeEmail.constructEmail(emailDetail);
				emailSender.sendHtmlEmail(emailDetail);
			} catch (Exception ex) {
				//log the error but do nothing - it should not be fatal if the email doesn't get sent
				logService.addException(ex);
			}
		}
		
		return "redirect:/user/dashboard";
	}
	
	@RequestMapping(value = "user/changeAccountLevel", method = RequestMethod.GET)
	public String getchangeAccount(Model model) {
		logger.info("user/changeAccountLevel GET");
		
		//accessDenied redirects to changeAccountLevel if appropriate, but the URL displayed by the browser is 
		//still the original URL, e.g., /recipe;  redirecting to user/account displays the correct URL
		return "redirect:/user/upgradeAccount";
	}

	@RequestMapping(value = "user/upgradeAccount", method = RequestMethod.GET)
	public String upgradeAccount(Model model) {
		logger.info("user/upgradeAccount GET");
		
		return "user/upgradeAccount";
	}

	@RequestMapping(value = "user/upgradeAccount", method = RequestMethod.POST)
	public String upgradeAccount() {
		logger.info("user/upgradeAccount POST");
		
		//reload the user's authentication with the AUTHOR role
		User currentUser = (User)userInfo.getUserDetails();
		User user = null;
		try {
			user = userService.getUser(currentUser.getId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}
		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (user == null) {
			throw new AccessUserException();
		}		
		userService.changeRole(Role.TYPE_AUTHOR, user);
		userSecurityService.reauthenticateUser(user);
		
		return "redirect:/user/dashboard";
	}

	@RequestMapping(value = "user/join", method = RequestMethod.GET)
	public String getJoin(Model model) {
		logger.info("user/join GET");
		
		return "user/join";
	}

	/*************************/
	/*** Dashboard handler ***/
	/*************************/
	@MaintAware
	@RequestMapping(value = "user/dashboard", method = RequestMethod.GET)
	public String getDashboard(Model model, HttpServletRequest request, Locale locale) throws AccessUserException {
		logger.info("user/dashboard GET");

		User currentUser = (User)userInfo.getUserDetails();
		
		User user = null;
		try {
			user = userService.getUser(currentUser.getId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}

		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (user == null) {
			throw new AccessUserException();
		}		
		
		Long recipeCount = null;
		try {
			recipeCount = recipeService.getRecipeCount(user.getId());
		} 
		catch (Exception ex) {
			//do nothing - this is not a fatal error
			logService.addException(ex);
		}

		List<RecipeDisplayDto> recentRecipes = null;
		List<RecipeDisplayDto> viewedRecipes = null;
		RecipeDisplayDto mostViewedRecipe = null;
		RecipeDisplayDto featuredRecipe = null;
		long viewCount = 0;
		
		try {
			recentRecipes = recipeService.recentRecipes();
			viewedRecipes = recipeService.viewedRecipes(user.getId());
			viewCount = recipeService.getUserViewCount(user.getId());
			mostViewedRecipe = recipeService.getMostViewedRecipe(true);
		} 
		catch (Exception ex) {
			//do nothing - these are not a fatal errors
			logService.addException(ex);
		}

		String recipeIdStr = messages.getMessage("featuredrecipe", null, "0", locale);
		long recipeId = Long.parseLong(recipeIdStr);
		
		try {
			featuredRecipe = recipeService.getFeaturedRecipe(recipeId);
		} 
		catch (Exception ex) {
			//do nothing - these are not a fatal errors
			logService.addException(ex);
		}
		
		Date passwordExpirtyDt = user.getPasswordExpiryDate();
		Date todaysDt = new Date(); 
		if (passwordExpirtyDt != null) {
	        if (todaysDt.getTime() < passwordExpirtyDt.getTime()) {
	        	int days = Days.daysBetween(new DateTime(todaysDt), new DateTime(passwordExpirtyDt)).getDays();
	        	if (days <= 15) {
		        	int hours = Hours.hoursBetween(new DateTime(todaysDt), new DateTime(passwordExpirtyDt)).getHours();
		        	days = Math.round((float)hours / 24);
	        		String msg1 = messages.getMessage("user.password.willExpire1", null, "Your password is about to expire!", locale);
	        		String msg2;
	        		if (hours < 24)
	        			msg2 = messages.getMessage("user.password.willExpire3", null, "", locale);
	        		else {
		        		String plural = days > 1 ? "s" : ""; 
		        		Object[] args = new Object[] {days, plural};
	        			msg2 = messages.getMessage("user.password.willExpire2", args, "", locale);
	        		}
	        		String pswdExpire = msg1 + " " + msg2;	        		
	        		model.addAttribute("pswdExpire", pswdExpire);
	        	}
	        }
		}
		
		
		String roleType = currentUser.getRole().getName(); 
		if (roleType.equalsIgnoreCase(Role.TYPE_EDITOR) || roleType.equalsIgnoreCase(Role.TYPE_ADMIN)) {
			long recipeApprovals = recipeService.getRequireApprovalCount();
			long ingredientReviews = ingredientService.getNotReviewedCount();
			long flaggedComments = commentService.getFlaggedCount();
			model.addAttribute("recipeApprovals", recipeApprovals);
			model.addAttribute("ingredientReviews", ingredientReviews);
			model.addAttribute("flaggedComments", flaggedComments);
		}

		model.addAttribute("user", user);
		//need to use the userInfo role because an admin may have changed the user's role in the DB, but the user hasn't re-logged in yet
		model.addAttribute("role", currentUser.getRole());
		model.addAttribute("recipeCount", recipeCount);
		model.addAttribute("viewCount", viewCount);
		model.addAttribute("nextMaint", maintInterceptor.getNextStartWindow(true, "sysmaint.usermessage", locale));
		model.addAttribute("recentRecipes", recentRecipes);
		model.addAttribute("viewedRecipes", viewedRecipes);
		model.addAttribute("mostViewedRecipe", mostViewedRecipe);
		model.addAttribute("featuredRecipe", featuredRecipe);

		return "user/dashboard";
	}
	
	@RequestMapping(value = "user/avatar", method = RequestMethod.GET)
	public void getAvatar(@RequestParam("id") final long id, @RequestParam("filename") final String fileName, HttpServletResponse response) {
		logger.info("user/avatar GET: id=" + id);
		
		//errors are not fatal and will be logged by FileAction
		fileAction.downloadFile(FileType.AVATAR, id, fileName, response);
	}
	
	/*******************************/
	/*** Change account handler ***/
	/*******************************/
	@MaintAware
	@RequestMapping(value = "user/changeAccount", method = RequestMethod.GET)
	public String getChangeAccount(Model model) {
		logger.info("user/changeAccount GET");

		//must get the user info from the database, not the userInfo object because
		//the user may have clicked on an opt-out link in an email; that method
		//is not able to update the userInfo object since it can't tell if the user
		//is logged in or not
		User user = (User)userInfo.getUserDetails();
		try {
			user = userService.getUser(user.getId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}
		
		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (user == null) {
			throw new AccessUserException();
		}		

		ChangeNameDto changeNameDto = new ChangeNameDto(user);
		ChangeEmailDto changeEmailDto = new ChangeEmailDto(user);
		ChangePasswordDto changePasswordDto = new ChangePasswordDto(user);
		ChangeNotificationDto changeNotificationDto = new ChangeNotificationDto(user);
		model.addAttribute("changeNameDto", changeNameDto);		
		model.addAttribute("changeEmailDto", changeEmailDto);
		model.addAttribute("changePasswordDto", changePasswordDto);
		model.addAttribute("changeNotificationDto", changeNotificationDto);

		Map<String, Object> sizeMap = constraintMap.getModelConstraints("Size", "max", 
				ChangeNameDto.class, ChangeEmailDto.class, ChangePasswordDto.class); 
		model.addAttribute("sizeMap", sizeMap);
		
		return "user/changeAccount";
	}

	@MaintAware
	@RequestMapping(value = "user/changeName", method = RequestMethod.POST)
	public String postName(Model model, @ModelAttribute @Validated(ChangeNameDtoSequence.class) ChangeNameDto changeNameDto, BindingResult result, Locale locale) 
			throws SaveAccountException, AccessUserException {
		logger.info("user/changeName POST");

		User user = null;
		try {
			user = userService.getUser(changeNameDto.getUserId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}
		
		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (user == null) {
			throw new AccessUserException();
		}		

		//must re-add attribute(s) in case of an error
		ChangeEmailDto changeEmailDto = new ChangeEmailDto(user);
		ChangePasswordDto changePasswordDto = new ChangePasswordDto(user);
		ChangeNotificationDto changeNotificationDto = new ChangeNotificationDto(user);
		model.addAttribute("changeEmailDto", changeEmailDto);
		model.addAttribute("changePasswordDto", changePasswordDto);
		model.addAttribute("changeNotificationDto", changeNotificationDto);

		Map<String, Object> sizeMap = constraintMap.getModelConstraints("Size", "max", 
				ChangeNameDto.class, ChangeEmailDto.class, ChangePasswordDto.class); 
		model.addAttribute("sizeMap", sizeMap);

		if (result.hasErrors()) {
			logger.debug("Validation errors");
			changeNameDto.setCurrentFirstName(user.getFirstName());
			changeNameDto.setCurrentLastName(user.getLastName());
			return "user/changeAccount";
		}
		
		try {
			user = userService.changeName(changeNameDto.getFirstName(), changeNameDto.getLastName(), user);
			//reload the user's authentication with the new name
			userSecurityService.reauthenticateUser(user);
		} catch (Exception ex) {
			throw new SaveAccountException(ex);
		}        

		if (user.isEmailAdmin()) {
			String userName = user.getFirstName() + " " + user.getLastName();
	
	        EmailDetail emailDetail = new EmailDetail(userName, user.getEmail(), locale);
	        emailDetail.setChangeType(ChangeType.NAME);
			String idStr = String.valueOf(user.getId());
			String userIdStr = encryptUtil.encryptURLParam(idStr);
			String msgTypeStr = encryptUtil.encryptURLParam(OptOutType.ACCOUNT.name());
			String optoutUrl = "/user/optout?id=" + userIdStr + "&type=" + msgTypeStr;
			emailDetail.setOptoutUrl(optoutUrl);
			
			try {
				accountChangeEmail.constructEmail(emailDetail);
				emailSender.sendHtmlEmail(emailDetail);
			} catch (Exception ex) {
				//log the error but do nothing - it should not be fatal if the email doesn't get sent
				logService.addException(ex);
			}
		}
		
		//return a success message
		String msg = messages.getMessage("account.change.nameupdated", null, "Success", locale);
		changeNameDto.setCurrentFirstName(user.getFirstName());
		changeNameDto.setCurrentLastName(user.getLastName());
		model.addAttribute("changeNameDto", changeNameDto);
		model.addAttribute("nameSuccessMessage", msg);

		return "user/changeAccount";
	}

	@MaintAware
	@RequestMapping(value = "user/changeEmail", method = RequestMethod.POST)
	public String postEmail(Model model, @ModelAttribute @Validated(ChangeEmailDtoSequence.class) ChangeEmailDto changeEmailDto, BindingResult result, Locale locale) 
			throws SaveAccountException, AccessUserException {
		logger.info("user/changeEmail POST");

		User user = null;
		try {
			user = userService.getUser(changeEmailDto.getUserId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}

		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (user == null) {
			throw new AccessUserException();
		}		
		
		//must re-add attribute(s) in case of an error
		ChangeNameDto changeNameDto = new ChangeNameDto(user);
		ChangePasswordDto changePasswordDto = new ChangePasswordDto(user);
		ChangeNotificationDto changeNotificationDto = new ChangeNotificationDto(user);
		model.addAttribute("changeNameDto", changeNameDto);
		model.addAttribute("changePasswordDto", changePasswordDto);
		model.addAttribute("changeNotificationDto", changeNotificationDto);

		Map<String, Object> sizeMap = constraintMap.getModelConstraints("Size", "max", 
				ChangeNameDto.class, ChangeEmailDto.class, ChangePasswordDto.class); 
		model.addAttribute("sizeMap", sizeMap);

		if (result.hasErrors()) {
			logger.debug("Validation errors");
			changeEmailDto.setCurrentEmail(user.getEmail());
			return "user/changeAccount";
		}

		//double-check the email isn't in use in case user ignored the AJAX error
		boolean exists = false;
		try {
			exists = userService.doesUserEmailExist(changeEmailDto.getEmail());
		} catch (Exception ex) {
			//do nothing - if there is a problem with the database the user will be notifed when they submit the form
			logService.addException(ex);
		}

		if (exists) {
			logger.debug("Validation errors");
			String msg = messages.getMessage("user.duplicateEmail", null, "Duplicate email", locale);
			FieldError err = new FieldError("changeEmailDto", "email", changeEmailDto.getEmail(), false, null, null, msg);
			result.addError(err);
			changeEmailDto.setCurrentEmail(user.getEmail());
			return "user/changeAccount";
		}

		try {
			user = userService.changeEmail(changeEmailDto.getEmail(), user);
			//reload the user's authentication with the new email
			userSecurityService.reauthenticateUser(user);
		} catch (Exception ex) {
			throw new SaveAccountException(ex);
		}        
		
		if (user.isEmailAdmin()) {
			String userName = user.getFirstName() + " " + user.getLastName();
	
	        EmailDetail emailDetail = new EmailDetail(userName, user.getEmail(), locale);
	        emailDetail.setChangeType(ChangeType.EMAIL);
			String idStr = String.valueOf(user.getId());
			String userIdStr = encryptUtil.encryptURLParam(idStr);
			String msgTypeStr = encryptUtil.encryptURLParam(OptOutType.ACCOUNT.name());
			String optoutUrl = "/user/optout?id=" + userIdStr + "&type=" + msgTypeStr;
			emailDetail.setOptoutUrl(optoutUrl);
			
			try {
				accountChangeEmail.constructEmail(emailDetail);
				emailSender.sendHtmlEmail(emailDetail);
			} catch (Exception ex) {
				//log the error but do nothing - it should not be fatal if the email doesn't get sent
				logService.addException(ex);
			}
		}
		
		//return a success message
		String msg = messages.getMessage("account.change.emailupdated", null, "Success", locale);
		changeEmailDto.setCurrentEmail(user.getEmail());
		model.addAttribute("changeEmailDto", changeEmailDto);
		model.addAttribute("emailSuccessMessage", msg);
		
		return "user/changeAccount";
	}
	
	@MaintAware
	@RequestMapping(value = "user/changePassword", method = RequestMethod.POST)
	public String postPassword(Model model, @ModelAttribute @Validated(ChangePasswordDtoSequence.class) ChangePasswordDto changePasswordDto, BindingResult result, Locale locale) 
			throws SaveAccountException, AccessUserException {
		logger.info("user/changePassword POST");

		User user = null;
		try {
			user = userService.getUser(changePasswordDto.getUserId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}

		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (user == null) {
			throw new AccessUserException();
		}		
		
		//must re-add attribute(s) in case of an error
		ChangeNameDto changeNameDto = new ChangeNameDto(user);
		ChangeEmailDto changeEmailDto = new ChangeEmailDto(user);
		ChangeNotificationDto changeNotificationDto = new ChangeNotificationDto(user);
		
		model.addAttribute("changeNameDto", changeNameDto);
		model.addAttribute("changeEmailDto", changeEmailDto);
		model.addAttribute("changeNotificationDto", changeNotificationDto);

		Map<String, Object> sizeMap = constraintMap.getModelConstraints("Size", "max", 
				ChangeNameDto.class, ChangeEmailDto.class, ChangePasswordDto.class); 
		model.addAttribute("sizeMap", sizeMap);
		
		if (result.hasErrors()) {
			logger.debug("Validation errors");
			changePasswordDto.setCurrentPassword("");
			changePasswordDto.setPassword("");
			changePasswordDto.setConfirmPassword("");
			return "user/changeAccount";
		}
		
		if (!userService.isPasswordValid(changePasswordDto.getCurrentPassword(), user)) {
			logger.debug("Validation errors");
			String msg = messages.getMessage("user.invalidPassword", null, "Invalid password", locale);
			FieldError err = new FieldError("changePasswordDto", "currentPassword",  msg);
			result.addError(err);
			changePasswordDto.setCurrentPassword("");
			changePasswordDto.setPassword("");
			changePasswordDto.setConfirmPassword("");
			return "user/changeAccount";
        }
		
		if (passwordEncoder.matches(changePasswordDto.getPassword(), user.getPassword())) {
			String msg = messages.getMessage("PasswordNotDuplicate", null, "", locale);
			FieldError fieldError = new FieldError("changePasswordDto", "password", msg);
			result.addError(fieldError);
			changePasswordDto.setCurrentPassword("");
			changePasswordDto.setPassword("");
			changePasswordDto.setConfirmPassword("");
			return "user/changeAccount";
		}
        
		try {
			userService.changePassword(changePasswordDto.getPassword(), user);
			//reload the user's authentication with the new password
			userSecurityService.reauthenticateUser(user);
		} catch (Exception ex) {
			throw new SaveAccountException(ex);
		}        
		
		if (user.isEmailAdmin()) {
			String userName = user.getFirstName() + " " + user.getLastName();
	
	        EmailDetail emailDetail = new EmailDetail(userName, user.getEmail(), locale);
			emailDetail.setChangeType(ChangeType.PASSWORD);
			String idStr = String.valueOf(user.getId());
			String userIdStr = encryptUtil.encryptURLParam(idStr);
			String msgTypeStr = encryptUtil.encryptURLParam(OptOutType.ACCOUNT.name());
			String optoutUrl = "/user/optout?id=" + userIdStr + "&type=" + msgTypeStr;
			emailDetail.setOptoutUrl(optoutUrl);
			
			try {
				accountChangeEmail.constructEmail(emailDetail);
				emailSender.sendHtmlEmail(emailDetail);
			} catch (Exception ex) {
				//log the error but do nothing - it should not be fatal if the email doesn't get sent
				logService.addException(ex);
			}
		}
		
		//return a success message
		String msg = messages.getMessage("account.change.passwordupdated", null, "Success", locale);
		model.addAttribute("passwordSuccessMessage", msg);
		
		return "user/changeAccount";
	}

	@MaintAware
	@RequestMapping(value = "user/changeNotification", method = RequestMethod.POST)
	public String postNotification(Model model, @ModelAttribute ChangeNotificationDto changeNotificationDto, BindingResult result, Locale locale) 
			throws SaveAccountException, AccessUserException {
		logger.info("user/changeNotification POST");

		User user = null;
		try {
			user = userService.getUser(changeNotificationDto.getUserId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}

		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (user == null) {
			throw new AccessUserException();
		}		
		
		//must re-add attribute(s) in case of an error
		ChangeNameDto changeNameDto = new ChangeNameDto(user);
		ChangeEmailDto changeEmailDto = new ChangeEmailDto(user);
		ChangePasswordDto changePasswordDto = new ChangePasswordDto(user);
		model.addAttribute("changeNameDto", changeNameDto);
		model.addAttribute("changeEmailDto", changeEmailDto);
		model.addAttribute("changePasswordDto", changePasswordDto);
		
		Map<String, Object> sizeMap = constraintMap.getModelConstraints("Size", "max", 
				ChangeNameDto.class, ChangeEmailDto.class, ChangePasswordDto.class); 
		model.addAttribute("sizeMap", sizeMap);

		if (result.hasErrors()) {
			logger.debug("Validation errors");
			return "user/changeAccount";
		}
		
		try {
			user = userService.changeNotification(changeNotificationDto, user);
			//reload the user's authentication with the new notification
			userSecurityService.reauthenticateUser(user);
		} catch (Exception ex) {
			throw new SaveAccountException(ex);
		}        

		if (user.isEmailAdmin()) {
	        String userName = user.getFirstName() + " " + user.getLastName();
	
	        EmailDetail emailDetail = new EmailDetail(userName, user.getEmail(), locale);
	        emailDetail.setChangeType(ChangeType.NOTIFY);
			String idStr = String.valueOf(user.getId());
			String userIdStr = encryptUtil.encryptURLParam(idStr);
			String msgTypeStr = encryptUtil.encryptURLParam(OptOutType.ACCOUNT.name());
			String optoutUrl = "/user/optout?id=" + userIdStr + "&type=" + msgTypeStr;
			emailDetail.setOptoutUrl(optoutUrl);
	        
			try {
				accountChangeEmail.constructEmail(emailDetail);
				emailSender.sendHtmlEmail(emailDetail);
			} catch (Exception ex) {
				//log the error but do nothing - it should not be fatal if the email doesn't get sent
				logService.addException(ex);
			}
		}
		
		//return a success message
		String msg = messages.getMessage("account.change.notificationupdated", null, "Success", locale);
		model.addAttribute("notificationSuccessMessage", msg);

		return "user/changeAccount";
	}
	
	@RequestMapping(value = "user/optout", method = RequestMethod.GET)
	public ModelAndView optOut(@RequestParam("id") final String id, @RequestParam("type") final String type, RedirectAttributes redir, Locale locale) {
		
		String userIdStr = encryptUtil.decryptURLParam(id);
		String msgTypeStr = encryptUtil.decryptURLParam(type);
		
		if (StringUtils.isBlank(userIdStr) || StringUtils.isBlank(msgTypeStr)) {
			throw new OptOutException("exception.OptOutException", null);
		}

		long userId = 0;
		User user = null;
		OptOutType outType = null;
		
		try {
			userId = Long.parseLong(userIdStr);
			outType = OptOutType.valueOf(msgTypeStr);
		}
		catch (Exception ex) {
			throw new OptOutException(ex);
		}
		
		try {
			user = userService.getUser(userId);
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}
		
		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (user == null) {
			throw new AccessUserException();
		}		

		ChangeNotificationDto changeNotificationDto = new ChangeNotificationDto(user);
		
		String msgCode = null;
		
		if (outType == OptOutType.ACCOUNT) {
			changeNotificationDto.setEmailAdmin(false);
			msgCode = "account.optout.emailAdmin";
		}
		else
		if (outType == OptOutType.NEWMESSAGE) {
			changeNotificationDto.setEmailMessage(false);
			msgCode = "account.optout.emailMessage";
		}
		else
		if (outType == OptOutType.RECIPE) {
			changeNotificationDto.setEmailRecipe(false);
			msgCode = "account.optout.emailRecipe";
		}
		else {
			throw new OptOutException("exception.OptOutException", null);
		}
		
		try {
			user = userService.changeNotification(changeNotificationDto, user);
			if (userSecurityService.isUserLoggedIn(user))
				userSecurityService.reauthenticateUser(user);
		} catch (Exception ex) {
			throw new SaveAccountException(ex);
		}        
	
		ModelAndView mv = new ModelAndView();
	
		Object[] obj = new String[] {null};
		redir.addFlashAttribute("title", messages.getMessage("signup.notification", null, "Success", locale));
		obj[0] = user.getFirstName() + " " + user.getLastName();
		String msg = messages.getMessage("account.optout.header", obj, "", locale);
		redir.addFlashAttribute("msgHeader", msg);
		List<String> msgList = new ArrayList<String>();		
		obj[0] = (Object) messages.getMessage(msgCode, null, "", locale);
		msg = messages.getMessage("account.optout.message", obj, "", locale);
        msgList.add(msg);
        obj[0] = env.getProperty("company.support.baseurl");
		msg = messages.getMessage("account.optout.review", obj, "", locale);
        msgList.add(msg);
        redir.addFlashAttribute("messages", msgList);
        mv.setViewName("redirect:/message");
        return mv;
	}
	
	/***********************************************/
	/*** Reset password and new password handler ***/
	/***********************************************/
	@RequestMapping(value = "user/resetPassword", method = RequestMethod.GET)
	public String getResetPassword(Model model) {
		logger.info("user/resetPassword GET");
		
		UserEmail email = new UserEmail();
		model.addAttribute("userEmail", email);
		
		return "user/resetPassword";
	}

	//single-field class (not worth creating a DTO object)
	public static class UserEmail {
		
		@NotBlank
		@Email
		@Size(max=50)	//50
		private String email;
		
		public UserEmail() {}
		
		public String getEmail() {
			return email;
		}
		
		public void setEmail(String email) {
			this.email = email;
		}
	}

	@RequestMapping(value = "user/resetPassword", method = RequestMethod.POST)
	public ModelAndView postResetPassword(@ModelAttribute @Valid UserEmail userEmail, BindingResult result, HttpServletRequest request, RedirectAttributes redir, 
			Locale locale) throws AccessUserException {
		logger.info("user/resetPassword POST: email=" + userEmail);
		
		ModelAndView mv = new ModelAndView("user/resetPassword");
		
		if (result.hasErrors()) {
			logger.debug("Validation errors");
			return mv;
		}

		User user = null;
		try {
			user = userService.findUserByEmail(userEmail.getEmail());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}

		if (user == null) {
			logger.debug("Validation errors");
			String msg = messages.getMessage("user.userNotFound", null, "User not found", locale);
			FieldError err = new FieldError("userEmail", "email", userEmail.getEmail(), false, null, null, msg);
			result.addError(err);
			return mv;
		}
		
		logger.debug("password reset - publishing event");
       	eventPublisher.publishEvent(new PasswordResetEvent(user, request.getLocale(), null));
        
       	Object[] obj = new String[] {null};
       	redir.addFlashAttribute("title", messages.getMessage("password.title", null, "Success", locale));
       	obj[0] = user.getFirstName() + " " + user.getLastName();
		String msg = messages.getMessage("user.password.header.sent", obj, "", locale);
		redir.addFlashAttribute("msgHeader", msg);
		List<String> msgList = new ArrayList<String>();
        msg = messages.getMessage("user.password.sentToken", null, "Token sent", locale);
        msgList.add(msg);
        redir.addFlashAttribute("messages", msgList);
        mv.setViewName("redirect:/message");
        return mv;
    }

	//respond to user click on email link
	@RequestMapping(value = "/confirmPassword", method = RequestMethod.GET)
    public ModelAndView confirmPassword(@RequestParam("id") final String id, @RequestParam("token") final String token,
    		RedirectAttributes redir, Locale locale) {
		logger.info("confirmPassword GET: id=" + id);		
		
		ModelAndView mv = new ModelAndView();

		User user = null;
		PasswordResetToken passwordResetToken = null;
		
		try {
			passwordResetToken = userService.getPasswordResetToken(token);
		} 
		catch (Exception ex) {
			throw new PasswordResetException(ex);
		}

		if (passwordResetToken != null) {
        	user = passwordResetToken.getUser();
        }
        
		String userIdStr = encryptUtil.decryptURLParam(id);
		long userId = 0;
	
		try {
			userId = Long.parseLong(userIdStr);
		}
		catch (Exception ex) {
			//do nothing - the error will be returned to the user in the next if statement
		}
		
    	if (passwordResetToken == null || user == null || user.getId() != userId) {
        	logger.debug("invalid token");
    		String msg = messages.getMessage("user.password.header.invalid", null, "", locale);
    		redir.addFlashAttribute("msgHeader", msg);
    		List<String> msgList = new ArrayList<String>();
        	msg = messages.getMessage("user.password.invalidToken", null, "Invalid token", locale);
            msgList.add(msg);
            redir.addFlashAttribute("messages", msgList);
        	redir.addFlashAttribute("password", true);
        	redir.addFlashAttribute("invalid", true);
        	mv.setViewName("redirect:/user/tokenError");
        	return mv;
    	}
		
        final Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        	logger.debug("token expired");
    		String msg = messages.getMessage("user.password.header.expired", null, "", locale);
    		redir.addFlashAttribute("msgHeader", msg);
    		List<String> msgList = new ArrayList<String>();
        	msg = messages.getMessage("user.password.expiredToken", null, "Expired token", locale);
            msgList.add(msg);
            redir.addFlashAttribute("messages", msgList);
        	redir.addFlashAttribute("password", true);
        	redir.addFlashAttribute("expired", true);
        	redir.addFlashAttribute("token", token);
        	mv.setViewName("redirect:/user/tokenError");
        	return mv;
        }

		//Note: setting security at this point as suggested by Baeldung opens up all menu items to the user prior to 
        //creating a new password;  alternatively, adding the user ID to the model allows for retrieving in in the POST method
        //final Authentication auth = new UsernamePasswordAuthenticationToken(user, null, userDetailsService.loadUserByUsername(user.getEmail()).getAuthorities());
        //SecurityContextHolder.getContext().setAuthentication(auth);
        
        NewPasswordDto newPasswordDto = new NewPasswordDto();
		newPasswordDto.setUserId(user.getId());
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", NewPasswordDto.class); 
		redir.addFlashAttribute("sizeMap", sizeMap);
		redir.addFlashAttribute("newPasswordDto", newPasswordDto);
		mv.setViewName("redirect:/user/newPassword");
        return mv;
    }	

	//resend a password reset email
	@RequestMapping(value = "/user/resendPasswordToken", method = RequestMethod.GET)
    public ModelAndView resendPasswordToken(final HttpServletRequest request, @RequestParam("token") final String token, RedirectAttributes redir, Locale locale) throws PasswordResendException {
		logger.info("user/resendPasswordToken GET");

		ModelAndView mv = new ModelAndView();
		
		User user = null;
		try {
	        user = userService.getPasswordResetUser(token);
		} catch (Exception ex) {
        	throw new PasswordResendException(ex);
        }
		
		logger.debug("new token requested - publishing event");
       	eventPublisher.publishEvent(new PasswordResetEvent(user, request.getLocale(), token));
		
       	Object[] obj = new String[] {null};
       	redir.addFlashAttribute("title", messages.getMessage("password.title", null, "Success", locale));
       	obj[0] = user.getFirstName() + " " + user.getLastName();
		String msg = messages.getMessage("user.password.header.sent", obj, "", locale);
		redir.addFlashAttribute("msgHeader", msg);
		List<String> msgList = new ArrayList<String>();
        msg = messages.getMessage("user.password.sentNewToken", null, "Token sent", locale);
        msgList.add(msg);
        redir.addFlashAttribute("messages", msgList);
        mv.setViewName("redirect:/message");
        return mv;
    }

	@MaintAware
	@RequestMapping(value = "user/newPassword", method = RequestMethod.GET)
	public String getNewPassword(Model model) {
		logger.info("user/newPassword GET");
				
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", NewPasswordDto.class); 
		model.addAttribute("sizeMap", sizeMap);
		
		return "user/newPassword";
	}
	
	@MaintAware
	@RequestMapping(value = "user/newPassword", method = RequestMethod.POST)
	public String postNewPassword(Model model, @ModelAttribute @Validated(NewPasswordDtoSequence.class) NewPasswordDto newPasswordDto, BindingResult result, Locale locale) throws PasswordResetException {
		logger.info("user/newPassword POST");

		//must re-add attribute(s) in case of an error
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", NewPasswordDto.class); 
		model.addAttribute("sizeMap", sizeMap);
		
		if (result.hasErrors()) {
			logger.debug("Validation errors");
			newPasswordDto.setPassword("");
			newPasswordDto.setConfirmPassword("");
			return "user/newPassword";
		}
		
		User user = null;
		try {
			user = userService.getUser(newPasswordDto.getUserId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}
		
		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (user == null) {
			throw new AccessUserException();
		}		

		if (passwordEncoder.matches(newPasswordDto.getPassword(), user.getPassword())) {
			String msg = messages.getMessage("PasswordNotDuplicate", null, "", locale);
			FieldError fieldError = new FieldError("newPasswordDto", "password", msg);
			result.addError(fieldError);
			newPasswordDto.setPassword("");
			newPasswordDto.setConfirmPassword("");
			return "user/newPassword";
		}
				
		try {
			user.setAccountExpired(0);
			userService.setLastLogin(user);
			userService.changePassword(newPasswordDto.getPassword(), user);
			userService.deletePasswordResetToken(user.getId());
		} catch (Exception ex) {
        	throw new PasswordResetException(ex);
        }
        
		if (user.isEmailAdmin()) {
			String userName = user.getFirstName() + " " + user.getLastName();
			
			EmailDetail emailDetail = new EmailDetail(userName, user.getEmail(), locale);
			emailDetail.setChangeType(ChangeType.PASSWORD);
			String idStr = String.valueOf(user.getId());
			String userIdStr = encryptUtil.encryptURLParam(idStr);
			String msgTypeStr = encryptUtil.encryptURLParam(OptOutType.ACCOUNT.name());
			String optoutUrl = "/user/optout?id=" + userIdStr + "&type=" + msgTypeStr;
			emailDetail.setOptoutUrl(optoutUrl);
			
			try {
				accountChangeEmail.constructEmail(emailDetail);
				emailSender.sendHtmlEmail(emailDetail);
			} catch (Exception ex) {
				//log the error but do nothing - it should not be fatal if the email doesn't get sent
				logService.addException(ex);
		    }
		}
		
		return "redirect:/user/login";
	}

	/****************************/
	/*** User message handler ***/
	/****************************/
	@MaintAware
	@RequestMapping(value = "user/messages", method = RequestMethod.GET)
	public String listMessages(ModelMap model, Locale locale) {
		logger.info("user/messages GET");
	
		User user = (User)userInfo.getUserDetails();
		List<UserMessageDto> messages = userMessageService.listMessages(user.getId()); 
	
		if (messages.size() > 0 && user.getNewMsgCount() > 0) {
			//set the viewed flag
			userMessageService.setUserViewed(user.getId());
			//re-authenticate the user so that the nav bar will no longer show new messages
			userSecurityService.reauthenticateUser(user);
		}
		
		model.addAttribute("messages", messages);

		return "user/messages";
	}
	
	@RequestMapping(value = "user/sendMessage", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject sendUserMessage(@RequestBody UserMessage userMessage, Locale locale) throws RestException {
		logger.info("user/sendMessage POST");
		
		try {
			userMessageService.addMessage(userMessage, MessageType.MEMBER, locale);
		} catch (Exception ex) {
			throw new RestException("exception.sendUserMessage", ex);
		}
		
		return new ResponseObject();
	}

	@RequestMapping(value="user/deleteMessage", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject deleteMessage(@RequestParam("messageId") Long messageId) throws RestException {
		logger.info("user/deleteMessage POST: messageId=" + messageId);
		
		try {
			userMessageService.deleteMessage(messageId);
		} catch (Exception ex) {
			throw new RestException("exception.deleteMessage", ex);
		}

		return new ResponseObject();
	}

	@Async
	@TransactionalEventListener(fallbackExecution = true)
	public void handleUserMessageEvent(UserMessageEvent event) {
		logger.info("handleUserMessageEvent");
		User recipient = null;

		try {
			recipient = userService.getUser(event.getUserMessage().getToUserId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}
		
		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (recipient == null) {
			throw new AccessUserException();
		}		
		
		//do nothing if the user opted out of receiving message emails
		if (!recipient.isEmailMessage())
			return;
		
		String recipientName = recipient.getFirstName() + " " + recipient.getLastName();
		String recipientEmail = recipient.getEmail();
		
		EmailDetail emailDetail = new EmailDetail(recipientName, recipientEmail, event.getLocale());
		
		MessageType type = event.getMessageType();
		if (type == MessageType.MEMBER) {
			User sender = null;

			try {
				sender = userService.getUser(event.getUserMessage().getFromUserId());
			} 
			catch (Exception ex) {
				throw new AccessUserException(ex);
			}
			
			//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
			if (sender == null) {
				throw new AccessUserException();
			}		

			String senderName = sender.getFirstName() + " " + sender.getLastName();
			emailDetail.setUserName(senderName);
		}
		if (type == MessageType.RECIPE) {
			Recipe recipe = recipeService.getRecipe(event.getUserMessage().getRecipeId());
			emailDetail.setRecipeName(recipe.getName());
		}
		
		//build the URL with encrypted parameters
		String idStr = String.valueOf(recipient.getId());
		String recipIdStr = encryptUtil.encryptURLParam(idStr);
		String msgTypeStr = encryptUtil.encryptURLParam(OptOutType.NEWMESSAGE.name());
		String optoutUrl = "/user/optout?id=" + recipIdStr + "&type=" + msgTypeStr;
		emailDetail.setOptoutUrl(optoutUrl);
		
		emailDetail.setMessageType(event.getMessageType());
		//it's not possible to get the precise date/time without querying the DB again;
		//using "now" is close enough since the seconds do not appear in the email
		//emailDetail.setMessageDate(event.getUserMessage().getDateSent());
		emailDetail.setMessageDate(new Date());
		
    	try {
    		newMessageEmail.constructEmail(emailDetail);
			emailSender.sendHtmlEmail(emailDetail);
		} catch (Exception ex) {
			// do nothing - not a fatal error
			logService.addException(ex);
		}
	}
	
	/********************/
	/*** Shared pages ***/
	/********************/
	@RequestMapping(value = "user/tokenError", method = RequestMethod.GET)
	public String getExpiredToken(Model model) {
		logger.info("user/tokenError GET");
		
		return "user/tokenError";
	}
	
	@RequestMapping(value = "message", method = RequestMethod.GET)
	public String getUserMessage(Model model) {
		logger.info("message GET");
		
		return "message";
	}	
}
