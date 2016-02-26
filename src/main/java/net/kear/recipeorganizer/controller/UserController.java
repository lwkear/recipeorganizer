package net.kear.recipeorganizer.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import net.kear.recipeorganizer.enums.FileType;
import net.kear.recipeorganizer.event.OnPasswordResetEvent;
import net.kear.recipeorganizer.event.OnRegistrationCompleteEvent;
import net.kear.recipeorganizer.exception.AccessProfileException;
import net.kear.recipeorganizer.exception.AccessUserException;
import net.kear.recipeorganizer.exception.AddUserException;
import net.kear.recipeorganizer.exception.PasswordResendException;
import net.kear.recipeorganizer.exception.PasswordResetException;
import net.kear.recipeorganizer.exception.RestException;
import net.kear.recipeorganizer.exception.SaveAccountException;
import net.kear.recipeorganizer.exception.VerificationException;
import net.kear.recipeorganizer.exception.VerificationResendException;
import net.kear.recipeorganizer.persistence.dto.PasswordDto;
import net.kear.recipeorganizer.persistence.dto.RecipeDisplayDto;
import net.kear.recipeorganizer.persistence.dto.UserDto;
import net.kear.recipeorganizer.persistence.dto.UserDto.UserDtoSequence;
import net.kear.recipeorganizer.persistence.model.PasswordResetToken;
import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.model.UserProfile;
import net.kear.recipeorganizer.persistence.model.VerificationToken;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.util.ConstraintMap;
import net.kear.recipeorganizer.util.CookieUtil;
import net.kear.recipeorganizer.util.EmailSender;
import net.kear.recipeorganizer.util.FileActions;
import net.kear.recipeorganizer.util.FileResult;
import net.kear.recipeorganizer.util.ResponseObject;
import net.kear.recipeorganizer.util.UserInfo;

@Controller
public class UserController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserService userService;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private MessageSource messages;
	@Autowired
    private ApplicationEventPublisher eventPublisher;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private UserInfo userInfo;
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	private FileActions fileAction;
	@Autowired
	private CookieUtil cookieUtil;
	@Autowired
	private ExceptionLogService logService;
	@Autowired
	private ConstraintMap constraintMap;
	@Autowired
	private UserDetailsService detailsService;
	
    /*********************/
    /*** Login handler ***/
    /*********************/
	@RequestMapping(value = "user/login", method = RequestMethod.GET)
	public String getLogin(Model model) {
		logger.info("user/login GET");
		
		return "user/login";
	}

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
	
	/****************************/
	/*** Registration handler ***/
	/****************************/
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
       	final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
       	eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
        
        redir.addFlashAttribute("title", messages.getMessage("registration.success.title", null, "Success", locale));
        redir.addFlashAttribute("message", messages.getMessage("user.register.sentToken", null, "Token sent", locale));
        mv.setViewName("redirect:/message");
        return mv;
	}

	//AJAX/JSON request for checking user (email) duplication
	@RequestMapping(value="/lookupUser", produces="text/javascript")
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
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
		
		//name was found
		if (result) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			obj.setStatus(HttpServletResponse.SC_CONFLICT);
			obj.setMsg("user.duplicateEmail");
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
        	redir.addFlashAttribute("message", messages.getMessage("user.register.invalidToken", null, "Invalid token", locale));
        	redir.addFlashAttribute("register", true);
        	mv.setViewName("redirect:/errors/invalidToken");
        	return mv;     	
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        	redir.addFlashAttribute("message", messages.getMessage("user.register.expiredToken", null, "Expired token", locale));
        	redir.addFlashAttribute("register", true);
        	redir.addFlashAttribute("expired", true);
        	redir.addFlashAttribute("token", token);
        	mv.setViewName("redirect:/errors/expiredToken");
        	return mv;
        }

        user.setEnabled(1);
        try {
        	userService.updateUser(user);
        } catch (Exception ex) {
        	throw new SaveAccountException(ex);
        }
        logger.debug("user updated");
        
        mv.setViewName("redirect:/user/login");
        return mv;
	}
	
	//resend a registration email
	@RequestMapping(value = "/user/resendRegistrationToken", method = RequestMethod.GET)
    public ModelAndView resendRegistrationToken(@RequestParam("token") final String token, final HttpServletRequest request, RedirectAttributes redir, Locale locale) throws VerificationResendException {
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

        final String confirmationUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/confirmRegistration?token=" 
        		+ newToken.getToken();
        
        emailSender.setUser(user);
     	emailSender.setLocale(request.getLocale());
     	emailSender.setSubjectCode("user.email.signupSubject");
     	emailSender.setMessageCode("user.email.signupSuccess");
     	emailSender.sendTokenEmailMessage(confirmationUrl);
        
        redir.addFlashAttribute("title", messages.getMessage("registration.success.title", null, "Successful registration", locale));
        redir.addFlashAttribute("message", messages.getMessage("user.register.sentNewToken", null, "Token sent", locale));
        mv.setViewName("redirect:/message");
        return mv;
    }
	
	/******************************************/
	/*** Profile and Account change handler ***/
	/******************************************/
	@RequestMapping(value = "user/profile", method = RequestMethod.GET)
	public String getProfile(Model model) throws AccessUserException, AccessProfileException {
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
		
		return "user/profile";
	}

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
				if (currAvatar != null && !currAvatar.isEmpty()) {
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
		//TODO: move xxxREMOVExxx to a constant
		if (avatarName!= null && avatarName.startsWith("xxxREMOVExxx")) {
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
					UserDetails details = detailsService.loadUserByUsername(user.getEmail());
					Authentication auth= new UsernamePasswordAuthenticationToken(details, user.getPassword(), details.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
		} 
		catch (Exception ex) {
			throw new SaveAccountException(ex);
		}
		
        emailSender.setUser(user);
     	emailSender.setLocale(locale);
     	emailSender.setSubjectCode("user.email.accountChange");
     	emailSender.setMessageCode("user.email.profileChange");
     	emailSender.sendSimpleEmailMessage();
		
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
		UserDetails details = detailsService.loadUserByUsername(user.getEmail());
		Authentication auth= new UsernamePasswordAuthenticationToken(details, user.getPassword(), details.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		return "redirect:/user/dashboard";
	}
	
	/*************************/
	/*** Dashboard handler ***/
	/*************************/
	@RequestMapping(value = "user/dashboard", method = RequestMethod.GET)
	public String getDashboard(Model model, HttpServletRequest request) throws AccessUserException {
		logger.info("user/dashboard GET");

		User currentUser = (User)userInfo.getUserDetails();
		
		User user = null;
		try {
			//user = userService.getUserWithProfile(currentUser.getId());
			user = userService.getUser(currentUser.getId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}

		Long count = null;
		try {
			count = recipeService.getRecipeCount(user.getId());
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
		long views = 0;
		try {
			recentRecipes = recipeService.recentRecipes(user.getId());
			views = recipeService.getUserViewCount(user.getId());
		} 
		catch (Exception ex) {
			//do nothing - these are not a fatal errors
			logService.addException(ex);
		}

		model.addAttribute("user", user);
		model.addAttribute("recipeCount", count);
		model.addAttribute("viewCount", views);
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
	@RequestMapping(value = "user/changePassword", method = RequestMethod.GET)
	public String getPassword(Model model) {
		logger.info("user/changePassword GET");

		PasswordDto passwordDto = new PasswordDto();
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", PasswordDto.class); 
		model.addAttribute("sizeMap", sizeMap);
		model.addAttribute(passwordDto);
		
		return "user/changePassword";
	}

	@RequestMapping(value = "user/changePassword", method = RequestMethod.POST)
	public String postPassword(@ModelAttribute @Valid PasswordDto passwordDto, BindingResult result, Locale locale) throws SaveAccountException, AccessUserException {
		logger.info("user/changePassword POST");

		User currentUser = (User)userInfo.getUserDetails();
		
		if (result.hasErrors()) {
			logger.debug("Validation errors");
			return "user/changePassword";
		}
		
		User user = null;
		try {
			user = userService.getUser(currentUser.getId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}

		if (!userService.isPasswordValid(passwordDto.getCurrentPassword(), user)) {
			logger.debug("Validation errors");
			String msg = messages.getMessage("user.invalidPassword", null, "Invalid password", locale);
			FieldError err = new FieldError("passwordDto","currentPassword", msg);
			result.addError(err);
			return "user/changePassword";
        }
        
		try {		
			userService.changePassword(passwordDto.getPassword(), user);
		} 
		catch (Exception ex) {
			throw new SaveAccountException(ex);
		}        
		
        emailSender.setUser(user);
     	emailSender.setLocale(locale);
     	emailSender.setSubjectCode("user.email.accountChange");
     	emailSender.setMessageCode("user.email.passwordChange");
     	emailSender.sendSimpleEmailMessage();
		
		return "redirect:/home";
	}

	/************************************************/
	/*** Forgot password and new password handler ***/
	/************************************************/
	@RequestMapping(value = "user/forgotPassword", method = RequestMethod.GET)
	public String getForgotPassword(Model model) {
		logger.info("user/forgotPassword GET");
		
		UserEmail email = new UserEmail();
		model.addAttribute("userEmail", email);
		
		return "user/forgotPassword";
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

	@RequestMapping(value = "user/forgotPassword", method = RequestMethod.POST)
	public ModelAndView postForgotPassword(@ModelAttribute @Valid UserEmail userEmail, BindingResult result, HttpServletRequest request, RedirectAttributes redir, 
			Locale locale) throws AccessUserException {
		logger.info("user/forgotPassword POST: email=" + userEmail);
		
		ModelAndView mv = new ModelAndView("user/forgotPassword");
		
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
       	eventPublisher.publishEvent(new OnPasswordResetEvent(user, request.getLocale(), appUrl));
        
        redir.addFlashAttribute("title", messages.getMessage("password.success.title", null, "Success", locale));
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
        	mv.setViewName("redirect:/errors/invalidToken");
        	return mv;     	
    	}
		
        final Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        	redir.addFlashAttribute("message", messages.getMessage("user.password.expiredToken", null, "Expired token", locale));
        	redir.addFlashAttribute("password", true);
        	redir.addFlashAttribute("expired", true);
        	redir.addFlashAttribute("token", token);
        	mv.setViewName("redirect:/errors/expiredToken");
        	return mv;
        }

		//Note: setting security at this point as suggested by Baeldung opens up all menu items to the user prior to 
        //creating a new password;  alternatively, adding the user ID to the model allows for retrieving in in the POST method

        //final Authentication auth = new UsernamePasswordAuthenticationToken(user, null, userDetailsService.loadUserByUsername(user.getEmail()).getAuthorities());
        //SecurityContextHolder.getContext().setAuthentication(auth);
        
        NewPassword newPassword = new NewPassword();
		newPassword.setUserId(user.getId());
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", NewPassword.class); 
		redir.addFlashAttribute("sizeMap", sizeMap);
		redir.addFlashAttribute("newPassword", newPassword);
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
        
        final String confirmationUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/confirmPassword?id=" 
        		+ user.getId() + "&token=" + newToken.getToken();
        
        emailSender.setUser(user);
     	emailSender.setLocale(request.getLocale());
     	emailSender.setSubjectCode("user.email.resetSubject");
     	emailSender.setMessageCode("user.email.resetSuccess");
     	emailSender.sendTokenEmailMessage(confirmationUrl);
        
        redir.addFlashAttribute("title", messages.getMessage("password.success.title", null, "Success", locale));
        redir.addFlashAttribute("message", messages.getMessage("user.password.sentNewToken", null, "Token sent", locale));
        mv.setViewName("redirect:/message");
        return mv;
    }

	@RequestMapping(value = "user/newPassword", method = RequestMethod.GET)
	public String getNewPassword(Model model) {
		logger.info("user/newPassword GET");
				
		return "user/newPassword";
	}

	//two-field class (not worth creating a DTO object)
	//TODO: SECURITY: need to figure out how to make the password validator usable by different classes
	//@PasswordMatch
	public static class NewPassword {
		
		@NotBlank
		@Size(min=6,max=20)
		private String password;
		
		@NotBlank
		@Size(min=6,max=20)
		private String confirmPassword;
		
		private long userId;
		
		public NewPassword() {}

		public NewPassword(NewPassword newPassword) {
			this.password = newPassword.password;
			this.confirmPassword = newPassword.confirmPassword;
		}
		
		public NewPassword(String password, String confirmPassword) {
			this.password = password;
			this.confirmPassword = confirmPassword;
		}
		
		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
		
		public String getConfirmPassword() {
			return confirmPassword;
		}

		public void setConfirmPassword(String password) {
			this.confirmPassword = password;
		}
		
		public long getUserId() {
			return userId;
		}
		
		public void setUserId(long userId) {
			this.userId = userId;
		}
	}
	
	@RequestMapping(value = "user/newPassword", method = RequestMethod.POST)
	public String postNewPassword(Model model, @ModelAttribute @Valid NewPassword newPassword, BindingResult result, Locale locale) throws PasswordResetException {
		logger.info("user/newPassword POST");

		if (result.hasErrors()) {
			logger.debug("Validation errors");
			return "user/newPassword";
		}
		
		User user = null;
		try {
			user = userService.getUser(newPassword.getUserId());
			userService.changePassword(newPassword.getPassword(), user);
		} catch (Exception ex) {
        	throw new PasswordResetException(ex);
        }
        
        emailSender.setUser(user);
     	emailSender.setLocale(locale);
     	emailSender.setSubjectCode("user.email.accountChange");
     	emailSender.setMessageCode("user.email.passwordChange");
     	emailSender.sendSimpleEmailMessage();
		
		return "redirect:/user/login";
	}

	/********************/
	/*** Shared pages ***/
	/********************/
	@RequestMapping(value = "errors/expiredToken", method = RequestMethod.GET)
	public String getExpiredToken(Model model) {
		logger.info("errors/expiredToken GET");
		
		return "errors/expiredToken";
	}
	
	@RequestMapping(value = "errors/invalidToken", method = RequestMethod.GET)
	public String getInvalidToken(Model model) {
		logger.info("errors/invalidToken GET");
		
		return "errors/invalidToken";
	}	

	@RequestMapping(value = "message", method = RequestMethod.GET)
	public String getUserMessage(Model model) {
		logger.info("message GET");
		
		return "message";
	}	
}

/*
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
*/