package net.kear.recipeorganizer.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
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
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;

import net.kear.recipeorganizer.enums.FileType;
import net.kear.recipeorganizer.enums.UserAge;
import net.kear.recipeorganizer.event.PasswordResetEvent;
import net.kear.recipeorganizer.event.RegistrationCompleteEvent;
import net.kear.recipeorganizer.exception.AccessProfileException;
import net.kear.recipeorganizer.exception.AccessUserException;
import net.kear.recipeorganizer.exception.AddUserException;
import net.kear.recipeorganizer.exception.PasswordResendException;
import net.kear.recipeorganizer.exception.PasswordResetException;
import net.kear.recipeorganizer.exception.RestException;
import net.kear.recipeorganizer.exception.SaveAccountException;
import net.kear.recipeorganizer.exception.VerificationException;
import net.kear.recipeorganizer.exception.VerificationResendException;
import net.kear.recipeorganizer.interceptor.MaintenanceInterceptor;
import net.kear.recipeorganizer.persistence.dto.ChangePasswordDto;
import net.kear.recipeorganizer.persistence.dto.ChangePasswordDto.ChangePasswordDtoSequence;
import net.kear.recipeorganizer.persistence.dto.NewPasswordDto;
import net.kear.recipeorganizer.persistence.dto.NewPasswordDto.NewPasswordDtoSequence;
import net.kear.recipeorganizer.persistence.dto.RecipeDisplayDto;
import net.kear.recipeorganizer.persistence.dto.UserDto;
import net.kear.recipeorganizer.persistence.dto.UserDto.UserDtoSequence;
import net.kear.recipeorganizer.persistence.dto.UserMessageDto;
import net.kear.recipeorganizer.persistence.model.PasswordResetToken;
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
import net.kear.recipeorganizer.security.UserSecurityService;
import net.kear.recipeorganizer.util.CookieUtil;
import net.kear.recipeorganizer.util.ResponseObject;
import net.kear.recipeorganizer.util.UserInfo;
import net.kear.recipeorganizer.util.db.ConstraintMap;
import net.kear.recipeorganizer.util.email.AccountChangeEmail;
import net.kear.recipeorganizer.util.email.EmailSender;
import net.kear.recipeorganizer.util.email.PasswordEmail;
import net.kear.recipeorganizer.util.email.RegistrationEmail;
import net.kear.recipeorganizer.util.email.AccountChangeEmail.ChangeType;
import net.kear.recipeorganizer.util.file.FileActions;
import net.kear.recipeorganizer.util.file.FileConstant;
import net.kear.recipeorganizer.util.file.FileResult;
import net.kear.recipeorganizer.util.maint.MaintAware;

@Controller
public class UserController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
	@Autowired
	private UserSecurityService userSecurityService;
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
	private RegistrationEmail registrationEmail; 
	@Autowired
	private PasswordEmail passwordEmail; 
	@Autowired
	private AccountChangeEmail accountChangeEmail; 
	
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
	public ModelAndView handleLoginError(RedirectAttributes redir, HttpServletRequest request, Locale locale) {
		logger.info("user/loginError GET");
	
		String authExClass = "";
		String msg = "Unknown error";
		AuthenticationException authEx = (AuthenticationException)request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

		if (authEx != null) {
			authExClass = authEx.getClass().getSimpleName();
			msg = messages.getMessage("exception." + authExClass, null, "Invalid login", locale);
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
		
		UserDto user = new UserDto();
		//default to AUTHOR
		user.setSubmitRecipes(true);
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", UserDto.class); 
		model.addAttribute("sizeMap", sizeMap);
		model.addAttribute("userDto", user);		
		
		return "user/signup";
	}
	
	@MaintAware
	@RequestMapping(value = "user/signup", method = RequestMethod.POST)
	public ModelAndView postSignup(@ModelAttribute @Validated(UserDtoSequence.class) UserDto userDto, 
			BindingResult result, HttpServletRequest request, RedirectAttributes redir, Locale locale) throws AddUserException {
		logger.info("user/signup POST: email=" + userDto.getEmail());

		ModelAndView mv = new ModelAndView("user/signup");
		
		if (result.hasErrors()) {
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
			FieldError err = new FieldError("userDto","email", msg);
			result.addError(err);
			return mv;
		}

		User user = null;
	
		try {
			user = userService.addUser(userDto);
		} catch (Exception ex) {
			throw new AddUserException(ex);
		}
		
       	logger.debug("user added - publishing event");
       	eventPublisher.publishEvent(new RegistrationCompleteEvent(user, locale));
        
        redir.addFlashAttribute("title", messages.getMessage("registration.title", null, "Success", locale));
        redir.addFlashAttribute("message", messages.getMessage("user.register.sentToken", null, "Token sent", locale));
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
			throw new RestException("exception.default", ex);
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
	
		VerificationToken verificationToken = null;
		try {
			verificationToken = userService.getVerificationToken(token);
		} catch (Exception ex) {
			throw new VerificationException(ex);
		}
        
        if (verificationToken == null) {
        	String msg = messages.getMessage("user.register.invalidToken1", null, "Invalid token", locale) + 
        				 messages.getMessage("user.register.invalidToken2", null, "Invalid token", locale);
        	redir.addFlashAttribute("message", msg);
        	redir.addFlashAttribute("register", true);
        	mv.setViewName("redirect:/user/invalidToken");
        	return mv;     	
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        	redir.addFlashAttribute("message", messages.getMessage("user.register.expiredToken", null, "Expired token", locale));
        	redir.addFlashAttribute("register", true);
        	redir.addFlashAttribute("expired", true);
        	redir.addFlashAttribute("token", token);
        	mv.setViewName("redirect:/user/expiredToken");
        	return mv;
        }

        user.setEnabled(1);
        try {
        	userService.updateUser(user);
        	userService.deleteVerificationToken(verificationToken);
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
		
		VerificationToken newToken = null;
		User user = null;
		try {
			newToken = userService.recreateUserVerificationToken(token);
	        user = userService.getVerificationUser(newToken.getToken());
		} catch (Exception ex) {
        	throw new VerificationResendException(ex);
        }

        String userName = user.getFirstName() + " " + user.getLastName();
        String confirmationUrl = "/confirmRegistration?token=" + newToken.getToken();

        registrationEmail.init(userName, user.getEmail(), locale);
        registrationEmail.setTokenUrl(confirmationUrl);
        registrationEmail.constructEmail();
    	try {
			emailSender.sendHtmlEmail(registrationEmail);
		} catch (Exception ex) {
			throw new VerificationResendException(ex);
		}
        
        redir.addFlashAttribute("title", messages.getMessage("registration.title", null, "Successful registration", locale));
        redir.addFlashAttribute("message", messages.getMessage("user.register.sentNewToken", null, "Token sent", locale));
        mv.setViewName("redirect:/message");
        return mv;
    }
	
	/******************************************/
	/*** Profile and Account change handler ***/
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
		
		UserProfile userProfile = user.getUserProfile();
		if (userProfile == null) {
			userProfile = new UserProfile();
			userProfile.setUser(user);
		}
		
		if (userInfo.isUserRole(Role.TYPE_GUEST))
			userProfile.setSubmitRecipes(false);
		
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", UserProfile.class); 
		model.addAttribute("sizeMap", sizeMap);
		model.addAttribute("userProfile", userProfile);
		String rangeDescription = messages.getMessage("profile.nevermind", null, "Default", locale);
		UserAge.UANEVERMIND.setDescription(rangeDescription);		
		model.addAttribute("ageRanges", UserAge.values());
		
		return "user/profile";
	}

	@MaintAware
	@RequestMapping(value = "user/profile", method = RequestMethod.POST)
	public String postProfile(@ModelAttribute @Valid UserProfile userProfile, BindingResult result, Locale locale,
			@RequestParam(value = "file", required = false) MultipartFile file, HttpSession session) throws AccessUserException, SaveAccountException {
		logger.info("user/profile POST: user=" + userProfile.getUser().getId());

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
		
        String userName = user.getFirstName() + " " + user.getLastName();

        accountChangeEmail.init(userName, user.getEmail(), locale);
		accountChangeEmail.setChangeType(ChangeType.PROFILE);
		accountChangeEmail.constructEmail();
		try {
			emailSender.sendHtmlEmail(accountChangeEmail);
		} catch (Exception ex) {
			throw new SaveAccountException(ex);
		}
		
		return "redirect:/user/dashboard";
	}
	
	@RequestMapping(value = "user/changeAccount", method = RequestMethod.GET)
	public String getchangeAccount(Model model) {
		logger.info("user/changeAccount GET");
		
		//accessDenied redirects to changeAccount if appropriate, but the URL displayed by the browser is 
		//still the original URL, e.g., /recipe;  redirecting to user/account displays the correct URL
		return "redirect:/user/account";
	}

	@RequestMapping(value = "user/account", method = RequestMethod.GET)
	public String getAccount(Model model) {
		logger.info("user/account GET");
		
		return "user/account";
	}

	@RequestMapping(value = "user/account", method = RequestMethod.POST)
	public String upgradeAccount() {
		logger.info("user/account POST");
		
		//reload the user's authentication with the AUTHOR role
		User currentUser = (User)userInfo.getUserDetails();
		User user = userService.getUser(currentUser.getId());
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

		Long recipeCount = null;
		try {
			recipeCount = recipeService.getRecipeCount(user.getId());
		} 
		catch (Exception ex) {
			//do nothing - this is not a fatal error
			logService.addException(ex);
		}
		
		List<RecipeDisplayDto> viewedRecipes = null;		
		Cookie recentRecipesCookie = cookieUtil.findUserCookie(request, "recentRecipes", user.getId()); 
		if (recentRecipesCookie != null) {
			String recipes = recentRecipesCookie.getValue();
			ArrayList<String> recipeIds = new ArrayList<String>(Arrays.asList(recipes.split(",")));
			try {
				viewedRecipes = recipeService.listRecipes(recipeIds);
			} 
			catch (Exception ex) {
				//do nothing - this is not a fatal error
				logService.addException(ex);
			}
		}
		
		List<RecipeDisplayDto> recentRecipes = null;
		long viewCount = 0;
		try {
			recentRecipes = recipeService.recentRecipes(user.getId());
			viewCount = recipeService.getUserViewCount(user.getId());
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
		        	//float hrsToDays = (float)hours / 24;
		        	//days = Math.round(hrsToDays);
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

		return "user/dashboard";
	}
	
	@RequestMapping(value = "user/avatar", method = RequestMethod.GET)
	public void getAvatar(@RequestParam("id") final long id, @RequestParam("filename") final String fileName, HttpServletResponse response) {
		logger.info("user/avatar GET: id=" + id);
		
		//errors are not fatal and will be logged by FileAction
		fileAction.downloadFile(FileType.AVATAR, id, fileName, response);
	}
	
	/*******************************/
	/*** Change password handler ***/
	/*******************************/
	@MaintAware
	@RequestMapping(value = "user/changePassword", method = RequestMethod.GET)
	public String getPassword(Model model) {
		logger.info("user/changePassword GET");

		ChangePasswordDto changePasswordDto = new ChangePasswordDto();
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", ChangePasswordDto.class); 
		model.addAttribute("sizeMap", sizeMap);
		model.addAttribute("changePasswordDto", changePasswordDto);
		
		return "user/changePassword";
	}

	@MaintAware
	@RequestMapping(value = "user/changePassword", method = RequestMethod.POST)
	public String postPassword(@ModelAttribute @Validated(ChangePasswordDtoSequence.class) ChangePasswordDto changePasswordDto, BindingResult result, Locale locale) 
			throws SaveAccountException, AccessUserException {
		logger.info("user/changePassword POST");

		User currentUser = (User)userInfo.getUserDetails();
		
		if (result.hasErrors()) {
			logger.debug("Validation errors");
			changePasswordDto.setCurrentPassword("");
			changePasswordDto.setPassword("");
			changePasswordDto.setConfirmPassword("");
			return "user/changePassword";
		}
		
		User user = null;
		try {
			user = userService.getUser(currentUser.getId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}

		if (!userService.isPasswordValid(changePasswordDto.getCurrentPassword(), user)) {
			logger.debug("Validation errors");
			String msg = messages.getMessage("user.invalidPassword", null, "Invalid password", locale);
			FieldError err = new FieldError("changePasswordDto","currentPassword", msg);
			result.addError(err);
			return "user/changePassword";
        }
        
		try {		
			userService.changePassword(changePasswordDto.getPassword(), user);
		} catch (Exception ex) {
			throw new SaveAccountException(ex);
		}        
		
        String userName = user.getFirstName() + " " + user.getLastName();

        accountChangeEmail.init(userName, user.getEmail(), locale);
		accountChangeEmail.setChangeType(ChangeType.PASSWORD);
		accountChangeEmail.constructEmail();
		try {
			emailSender.sendHtmlEmail(accountChangeEmail);
		} catch (Exception ex) {
			throw new SaveAccountException(ex);
		}        
		
		return "redirect:/home";
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
			FieldError err = new FieldError("userEmail","email", msg);
			result.addError(err);
			return mv;
		}
		
       	logger.debug("password reset - publishing event");
       	final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
       	eventPublisher.publishEvent(new PasswordResetEvent(user, request.getLocale(), appUrl));
        
        redir.addFlashAttribute("title", messages.getMessage("password.title", null, "Success", locale));
        redir.addFlashAttribute("message", messages.getMessage("user.password.sentToken", null, "Token sent", locale));
        mv.setViewName("redirect:/message");
        return mv;
    }

	//respond to user click on email link
	@RequestMapping(value = "/confirmPassword", method = RequestMethod.GET)
    public ModelAndView confirmPassword(@RequestParam("id") final long id, @RequestParam("token") final String token,
    		RedirectAttributes redir, Locale locale) throws PasswordResetException {
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
        
    	if (passwordResetToken == null || user == null || user.getId() != id) {
        	redir.addFlashAttribute("message", messages.getMessage("user.password.invalidToken", null, "Invalid token", locale));
        	redir.addFlashAttribute("password", true);
        	mv.setViewName("redirect:/user/invalidToken");
        	return mv;     	
    	}
		
        final Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        	redir.addFlashAttribute("message", messages.getMessage("user.password.expiredToken", null, "Expired token", locale));
        	redir.addFlashAttribute("password", true);
        	redir.addFlashAttribute("expired", true);
        	redir.addFlashAttribute("token", token);
        	mv.setViewName("redirect:/user/expiredToken");
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
		
		PasswordResetToken newToken = null;
		User user = null;
		try {
			newToken = userService.recreatePasswordResetTokenForUser(token);
	        user = userService.getPasswordResetUser(newToken.getToken());
		} catch (Exception ex) {
        	throw new PasswordResendException(ex);
        }
        
        String userName = user.getFirstName() + " " + user.getLastName();
        String confirmationUrl = "/confirmPassword?id=" + user.getId() + "&token=" + newToken.getToken();
        
        passwordEmail.init(userName, user.getEmail(), locale);
        passwordEmail.setTokenUrl(confirmationUrl);
        passwordEmail.constructEmail();
		try {
			emailSender.sendHtmlEmail(passwordEmail);
		} catch (Exception ex) {
	    	throw new PasswordResendException(ex);
	    }
        
        redir.addFlashAttribute("title", messages.getMessage("password.title", null, "Success", locale));
        redir.addFlashAttribute("message", messages.getMessage("user.password.sentNewToken", null, "Token sent", locale));
        mv.setViewName("redirect:/message");
        return mv;
    }

	@MaintAware
	@RequestMapping(value = "user/newPassword", method = RequestMethod.GET)
	public String getNewPassword(Model model) {
		logger.info("user/newPassword GET");
				
		return "user/newPassword";
	}
	
	@MaintAware
	@RequestMapping(value = "user/newPassword", method = RequestMethod.POST)
	public String postNewPassword(Model model, @ModelAttribute @Validated(NewPasswordDtoSequence.class) NewPasswordDto newPasswordDto, BindingResult result, Locale locale) throws PasswordResetException {
		logger.info("user/newPassword POST");

		if (result.hasErrors()) {
			logger.debug("Validation errors");
			newPasswordDto.setPassword("");
			newPasswordDto.setConfirmPassword("");
			return "user/newPassword";
		}
		
		User user = userService.getUser(newPasswordDto.getUserId());

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
        
		String userName = user.getFirstName() + " " + user.getLastName();
		
        accountChangeEmail.init(userName, user.getEmail(), locale);
		accountChangeEmail.setChangeType(ChangeType.PASSWORD);
		accountChangeEmail.constructEmail();
		try {
			emailSender.sendHtmlEmail(accountChangeEmail);
		} catch (Exception ex) {
	    	throw new PasswordResetException(ex);
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
	public ResponseObject sendUserMessage(@RequestBody UserMessage userMessage) throws RestException {
		logger.info("user/sendMessage POST");
		
		try {
			userMessageService.addMessage(userMessage);
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

	/********************/
	/*** Shared pages ***/
	/********************/
	@RequestMapping(value = "user/expiredToken", method = RequestMethod.GET)
	public String getExpiredToken(Model model) {
		logger.info("user/expiredToken GET");
		
		return "user/expiredToken";
	}
	
	@RequestMapping(value = "user/invalidToken", method = RequestMethod.GET)
	public String getInvalidToken(Model model) {
		logger.info("user/invalidToken GET");
		
		return "user/invalidToken";
	}	

	@RequestMapping(value = "message", method = RequestMethod.GET)
	public String getUserMessage(Model model) {
		logger.info("message GET");
		
		return "message";
	}	
}