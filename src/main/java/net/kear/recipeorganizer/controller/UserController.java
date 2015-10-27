package net.kear.recipeorganizer.controller;

import java.util.Calendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import net.kear.recipeorganizer.event.OnPasswordResetEvent;
import net.kear.recipeorganizer.event.OnRegistrationCompleteEvent;
import net.kear.recipeorganizer.persistence.dto.PasswordDto;
import net.kear.recipeorganizer.persistence.dto.UserDto;
import net.kear.recipeorganizer.persistence.dto.UserDto.UserDtoSequence;
import net.kear.recipeorganizer.persistence.model.PasswordResetToken;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.model.UserProfile;
import net.kear.recipeorganizer.persistence.model.VerificationToken;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.util.EmailSender;

@Controller
public class UserController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserService userService;

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private MessageSource messages;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

	@Autowired
	private EmailSender emailSender;
    
    /*********************/
    /*** Login handler ***/
    /*********************/
	@RequestMapping(value = "user/login", method = RequestMethod.GET)
	public String getLogin(Model model) {
		logger.info("login GET");
		
		return "user/login";
	}
		
	/****************************/
	/*** Registration handler ***/
	/****************************/
	@RequestMapping(value = "user/signup", method = RequestMethod.GET)
	public String getSignup(Model model) {
		logger.info("signup GET");
		
		UserDto user = new UserDto();
		model.addAttribute("userDto", user);		
		
		return "user/signup";
	}
	
	@RequestMapping(value = "user/signup", method = RequestMethod.POST)
	public ModelAndView postSignup(@ModelAttribute @Validated(UserDtoSequence.class) UserDto userDto, 
			BindingResult result, HttpServletRequest request, RedirectAttributes redir) {
		logger.info("login POST");

		ModelAndView mv = new ModelAndView("user/signup");
		
		if (result.hasErrors()) {
			logger.info("Validation errors");
			return mv;
		}
		
		//double-check the email isn't in use in case user ignored the AJAX error
		boolean exists = userService.doesUserEmailExist(userDto.getEmail());
		if (exists) {
			logger.info("Validation errors");
			String msg = messages.getMessage("user.duplicateEmail", null, LocaleContextHolder.getLocale());
			FieldError err = new FieldError("userDto","email", msg);
			result.addError(err);
			return mv;
		}
		
        User user = userService.addUser(userDto);
		
       	logger.info("user added - publishing event");
       	final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
       	eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
        
        redir.addFlashAttribute("title", messages.getMessage("registration.success.title", null, LocaleContextHolder.getLocale()));
        redir.addFlashAttribute("message", messages.getMessage("user.register.sentToken", null, LocaleContextHolder.getLocale()));
        mv.setViewName("redirect:/messages/userMessage");
        return mv;
	}

	//AJAX/JSON request for checking user (email) duplication
	@RequestMapping(value="/ajax/anon/lookupUser", produces="text/javascript")
	@ResponseBody 
	public String lookupUser(@RequestParam("email") String lookupEmail, HttpServletResponse response) {
		logger.info("ajax/anon/lookupUser");
		logger.info("email =" + lookupEmail);
		
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		//query the DB for the user
		boolean result = userService.doesUserEmailExist(lookupEmail);
		
		logger.info("lookupEmail result=" + result);
		
		//name was found
		if (result) {
			Locale locale = LocaleContextHolder.getLocale();
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			msg = messages.getMessage("user.duplicateEmail", null, "This email is not available", locale);
		}

		return msg;
	}

	//respond to user click on email link
	@RequestMapping(value = "/confirmRegistration", method = RequestMethod.GET)
	public ModelAndView confirmRegistration(@RequestParam("token") final String token, RedirectAttributes redir) {
		logger.info("confirmRegistration GET");		
		
		ModelAndView mv = new ModelAndView();
	
        final VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
        	redir.addFlashAttribute("message", messages.getMessage("user.register.invalidToken", null, LocaleContextHolder.getLocale()));
        	redir.addFlashAttribute("register", true);
        	mv.setViewName("redirect:/errors/invalidToken");
        	return mv;     	
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        	redir.addFlashAttribute("message", messages.getMessage("user.register.expiredToken", null, LocaleContextHolder.getLocale()));
        	redir.addFlashAttribute("register", true);
        	redir.addFlashAttribute("expired", true);
        	redir.addFlashAttribute("token", token);
        	mv.setViewName("redirect:/errors/expiredToken");
        	return mv;
        }

        user.setEnabled(1);
        userService.updateUser(user);
        logger.info("user updated");
        
        mv.setViewName("redirect:/user/login");
        return mv;
	}
	
	//resend a registration email
	@RequestMapping(value = "/user/resendRegistrationToken", method = RequestMethod.GET)
    public ModelAndView resendRegistrationToken(final HttpServletRequest request, @RequestParam("token") final String token,
    		RedirectAttributes redir) {
		logger.info("resendRegistrationToken GET");
		
		ModelAndView mv = new ModelAndView();
		
        final VerificationToken newToken = userService.recreateUserVerificationToken(token);
        final User user = userService.getVerificationUser(newToken.getToken());
        final String confirmationUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/confirmRegistration?token=" 
        		+ newToken.getToken();
        
        emailSender.setUser(user);
     	emailSender.setLocale(request.getLocale());
     	emailSender.setSubjectCode("user.email.signupSubject");
     	emailSender.setMessageCode("user.email.signupSuccess");
     	emailSender.sendTokenEmailMessage(confirmationUrl);
        
        redir.addFlashAttribute("title", messages.getMessage("registration.success.title", null, LocaleContextHolder.getLocale()));
        redir.addFlashAttribute("message", messages.getMessage("user.register.sentNewToken", null, LocaleContextHolder.getLocale()));
        mv.setViewName("redirect:/messages/userMessage");
        return mv;
    }
	
	/***********************/
	/*** Profile handler ***/
	/***********************/
	@RequestMapping(value = "user/profile", method = RequestMethod.GET)
	public String getProfile(Model model) {
		logger.info("profile GET");
		
		User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userService.getUserWithProfile(currentUser.getId());
		UserProfile userProfile = user.getUserProfile(); 
		if (userProfile == null) {
			userProfile = new UserProfile();
			userProfile.setUser(user);
		}
		
		model.addAttribute("userProfile", userProfile);
		
		return "user/profile";
	}

	@RequestMapping(value = "user/profile", method = RequestMethod.POST)
	public String postProfile(@ModelAttribute @Valid UserProfile userProfile, BindingResult result, Locale locale) {
		logger.info("profile POST");

		if (result.hasErrors()) {
			logger.info("Validation errors");
			return "user/profile";
		}
		
		//.jsp page saves off user.id and userprofile.id
		userService.saveUserProfile(userProfile);
		
		User user = userService.getUser(userProfile.getUser().getId());
		
        emailSender.setUser(user);
     	emailSender.setLocale(locale);
     	emailSender.setSubjectCode("user.email.accountChange");
     	emailSender.setMessageCode("user.email.profileChange");
     	emailSender.sendSimpleEmailMessage();
		
		return "redirect:/home";
	}

	/*******************************/
	/*** Change password handler ***/
	/*******************************/
	@RequestMapping(value = "user/changePassword", method = RequestMethod.GET)
	public String getPassword(Model model) {
		logger.info("password GET");

		PasswordDto passwordDto = new PasswordDto();
		model.addAttribute(passwordDto);
		
		return "user/changePassword";
	}

	@RequestMapping(value = "user/changePassword", method = RequestMethod.POST)
	public String postPassword(@ModelAttribute @Valid PasswordDto passwordDto, BindingResult result, Locale locale) {
		logger.info("changePassword POST");

		if (result.hasErrors()) {
			logger.info("Validation errors");
			return "user/changePassword";
		}
		
		//TODO: SECURITY: consider throwing an error (see Baeldung example)
		User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!userService.isPasswordValid(passwordDto.getCurrentPassword(), user)) {
			logger.info("Validation errors");
			String msg = messages.getMessage("user.invalidPassword", null, locale);
			FieldError err = new FieldError("passwordDto","currentPassword", msg);
			result.addError(err);
			return "user/changePassword";
        }
        
        userService.changePassword(passwordDto.getPassword(), user);
		
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
		logger.info("forgotPassword GET");
		
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
	public ModelAndView postForgotPassword(@ModelAttribute @Valid UserEmail userEmail, BindingResult result, HttpServletRequest request, RedirectAttributes redir) {
		logger.info("forgotPassword POST");
		
		ModelAndView mv = new ModelAndView("user/forgotPassword");
		
		if (result.hasErrors()) {
			logger.info("Validation errors");
			return mv;
		}

		final User user = userService.findUserByEmail(userEmail.getEmail());

		if (user == null) {
			logger.info("Validation errors");
			String msg = messages.getMessage("user.userNotFound", null, LocaleContextHolder.getLocale());
			FieldError err = new FieldError("userEmail","email", msg);
			result.addError(err);
			return mv;
		}
		
       	logger.info("password reset - publishing event");
       	final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
       	eventPublisher.publishEvent(new OnPasswordResetEvent(user, request.getLocale(), appUrl));
        
        redir.addFlashAttribute("title", messages.getMessage("password.success.title", null, LocaleContextHolder.getLocale()));
        redir.addFlashAttribute("message", messages.getMessage("user.password.sentToken", null, LocaleContextHolder.getLocale()));
        mv.setViewName("redirect:/messages/userMessage");
        return mv;
    }

	//respond to user click on email link
	@RequestMapping(value = "/confirmPassword", method = RequestMethod.GET)
    public ModelAndView confirmPassword(@RequestParam("id") final long id, @RequestParam("token") final String token,
    		RedirectAttributes redir) {
		logger.info("confirmPassword GET");		
		
		ModelAndView mv = new ModelAndView();

		User user = null;
        final PasswordResetToken passwordResetToken = userService.getPasswordResetToken(token);
        if (passwordResetToken != null) {
        	user = passwordResetToken.getUser();
        }
        
    	if (passwordResetToken == null || user == null || user.getId() != id) {
        	redir.addFlashAttribute("message", messages.getMessage("user.password.invalidToken", null, LocaleContextHolder.getLocale()));
        	redir.addFlashAttribute("password", true);
        	mv.setViewName("redirect:/errors/invalidToken");
        	return mv;     	
    	}
		
        final Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        	redir.addFlashAttribute("message", messages.getMessage("user.password.expiredToken", null, LocaleContextHolder.getLocale()));
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
		redir.addFlashAttribute("newPassword", newPassword);
		mv.setViewName("redirect:/user/newPassword");
        return mv;
    }	

	//resend a password reset email
	@RequestMapping(value = "/user/resendPasswordToken", method = RequestMethod.GET)
    public ModelAndView resendPasswordToken(final HttpServletRequest request, @RequestParam("token") final String token, RedirectAttributes redir) {
		logger.info("resendPasswordToken GET");

		ModelAndView mv = new ModelAndView();
		
		final PasswordResetToken newToken = userService.recreatePasswordResetTokenForUser(token);
        final User user = userService.getPasswordResetUser(newToken.getToken());
        final String confirmationUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/confirmPassword?id=" 
        		+ user.getId() + "&token=" + newToken.getToken();
        
        emailSender.setUser(user);
     	emailSender.setLocale(request.getLocale());
     	emailSender.setSubjectCode("user.email.resetSubject");
     	emailSender.setMessageCode("user.email.resetSuccess");
     	emailSender.sendTokenEmailMessage(confirmationUrl);
        
        redir.addFlashAttribute("title", messages.getMessage("password.success.title", null, LocaleContextHolder.getLocale()));
        redir.addFlashAttribute("message", messages.getMessage("user.password.sentNewToken", null, LocaleContextHolder.getLocale()));
        mv.setViewName("redirect:/messages/userMessage");
        return mv;
    }

	@RequestMapping(value = "user/newPassword", method = RequestMethod.GET)
	public String getNewPassword(Model model) {
		logger.info("newPassword GET");
				
		return "user/newPassword";
	}

	//two-field class (not worth creating a DTO object)
	//TODO: need to figure out how to make the password validator usable by different classes
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
	public String postNewPassword(Model model, @ModelAttribute @Valid NewPassword newPassword, BindingResult result, Locale locale) {
		logger.info("newPassword POST");

		if (result.hasErrors()) {
			logger.info("Validation errors");
			return "user/newPassword";
		}
		
		User user = userService.getUser(newPassword.getUserId());
		
        userService.changePassword(newPassword.getPassword(), user);
        
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
		logger.info("expiredToken GET");
		
		return "errors/expiredToken";
	}
	
	@RequestMapping(value = "errors/invalidToken", method = RequestMethod.GET)
	public String getInvalidToken(Model model) {
		logger.info("invalidToken GET");
		
		return "errors/invalidToken";
	}	

	@RequestMapping(value = "messages/userMessage", method = RequestMethod.GET)
	public String getUserMessage(Model model) {
		logger.info("userMessage GET");
		
		return "messages/userMessage";
	}	
}