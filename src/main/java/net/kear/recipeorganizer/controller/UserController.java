package net.kear.recipeorganizer.controller;

import java.util.Calendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.groups.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.hibernate.validator.constraints.Email;

import net.kear.recipeorganizer.event.OnPasswordResetEvent;
import net.kear.recipeorganizer.event.OnRegistrationCompleteEvent;
import net.kear.recipeorganizer.persistence.dto.PasswordDto;
import net.kear.recipeorganizer.persistence.dto.UserDto;
import net.kear.recipeorganizer.persistence.dto.UserDto.Sequence;
import net.kear.recipeorganizer.persistence.model.PasswordResetToken;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.model.UserProfile;
import net.kear.recipeorganizer.persistence.model.VerificationToken;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.util.UserInfo;

@Controller
public class UserController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserService userService;

	@Autowired
	private UserInfo userInfo;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private MessageSource messages;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private Environment env;

    @Autowired
    private JavaMailSender mailSender;
    
    /*** Login handler ***/
	@RequestMapping(value = "user/login", method = RequestMethod.GET)
	public String getLogin(Model model) {
		logger.info("login GET");
		
		return "user/login";
	}
		
	@RequestMapping(value = "user/signup", method = RequestMethod.GET)
	public String getSignup(Model model) {
		logger.info("signup GET");
		
		UserDto user = new UserDto();
		model.addAttribute("userDto", user);		
		
		return "user/signup";
	}
	
	@RequestMapping(value = "user/signup", method = RequestMethod.POST)
	public String postSignup(Model model, @ModelAttribute @Validated(Sequence.class) UserDto userDto, 
			BindingResult result, HttpServletRequest request) {
		logger.info("login POST");

		if (result.hasErrors()) {
			logger.info("Validation errors");
			return "user/signup";
		}
		
        User user = userService.addUser(userDto);
		
        try {
        	logger.info("user added - publishing event");
        	final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        	eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
        } catch (Exception ex) {
        	//TODO: GUI: redisplay the signup page with an error message
        	logger.debug("error encountered: " + ex.getMessage());
        }
        
		return "redirect:/messages/signupMessage";
	}

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
	public String postProfile(Model model, @ModelAttribute @Valid UserProfile userProfile, BindingResult result) {
		logger.info("profile POST");

		if (result.hasErrors()) {
			logger.info("Validation errors");
			return "user/profile";
		}
		
		//.jsp page saves off user.id and userprofile.id, so no need to retrieve the user again
		userService.saveUserProfile(userProfile);
		
		//TODO: SECURITY: send an account change email
		
		return "redirect:/home";
	}

	@RequestMapping(value = "user/changePassword", method = RequestMethod.GET)
	public String getPassword(Model model) {
		logger.info("password GET");
				
		return "user/changePassword";
	}

	//TODO: SECURITY: consider changing this to a standard form POST; 
	//advantage: server validation instead of client; ease of sending internationalized messages back to client
	//disadvantage: must create a DTO for just 3 fields
	@RequestMapping(value = "user/changepassword", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String postPassword(@RequestParam("oldpassword") final String oldPassword, @RequestParam("newpassword") final String newPassword, HttpServletResponse response) {
		logger.info("password POST");
		
		String msg = "Success";
		response.setStatus(HttpServletResponse.SC_OK);
		
		if ((oldPassword == null) || (newPassword == null)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			msg = "Empty password";
			return msg;
		}
		
		//TODO: SECURITY: consider throwing an error (see Baeldung example)
		User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!userService.isPasswordValid(oldPassword, user)) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            msg = "Current password is incorrect";
            return msg;
        }
        
        userService.changePassword(newPassword, user);
        
        //TODO: SECURITY: send an account change email
				
		return msg;
	}
	
	@RequestMapping(value = "user/forgotPassword", method = RequestMethod.GET)
	public String getForgotPassword(Model model) {
		logger.info("forgotPassword GET");
		
		UserEmail email = new UserEmail();
		model.addAttribute("userEmail", email);
		
		return "user/forgotPassword";
	}
	
	@RequestMapping(value = "user/forgotPassword", method = RequestMethod.POST)
	public String postForgotPassword(Model model, @ModelAttribute @Valid UserEmail email, BindingResult result, HttpServletRequest request) {

		final User user = userService.findUserByEmail(email.getEmail());

		if (result.hasErrors()) {
			logger.info("Validation errors");
			return "user/forgotPassword";
		}

		try {
        	logger.info("password reset - publishing event");
        	final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        	eventPublisher.publishEvent(new OnPasswordResetEvent(user, request.getLocale(), appUrl));
        } catch (Exception ex) {
        	//TODO: GUI: redisplay the reset page with an error message?
        	logger.debug("error encountered: " + ex.getMessage());
        }
        
		return "redirect:/messages/forgotMessage";		
    }
	
	@RequestMapping(value = "user/newPassword", method = RequestMethod.GET)
	public String getNewPassword(Model model) {
		logger.info("newPassword GET");
		
		PasswordDto passwordDto = new PasswordDto();
		model.addAttribute("passwordDto", passwordDto);
		
		return "user/newPassword";
	}

	@RequestMapping(value = "user/newPassword", method = RequestMethod.POST)
	public String postNewPassword(Model model, @ModelAttribute @Valid PasswordDto passwordDto, BindingResult result) 
		throws Exception {
		logger.info("newPassword POST");

		if (result.hasErrors()) {
			logger.info("Validation errors");
			return "user/newPassword";
		}
		
		//String email = SecurityContextHolder.getContext().getAuthentication().getName();
		//User user = userService.findUserByEmail(email);
		
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (user == null)
			throw new Exception("could not retrieve user");
		
		//user = userService.findUserByEmail(user.getEmail());
			
        userService.changePassword(passwordDto.getNewPassword(), user);
        
        //TODO: SECURITY: send an account change email
		
		return "redirect:/user/login";
	}

	@RequestMapping(value = "/confirmRegistration", method = RequestMethod.GET)
    public String confirmRegistration(final Locale locale, final Model model, @RequestParam("token") final String token) 
    		throws Exception {
		logger.info("confirmRegistration");		
		
        final VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
        	//TODO: GUI: figure out how to set messages on the generic errorData page
            //final String message = messages.getMessage("auth.message.invalidToken", null, locale);
            //model.addAttribute("message", message);
        	model.addAttribute("register", true);
            //return "redirect:/badUser.html?lang=" + locale.getLanguage();
        	//return "redirect:/errors/errorData";
        	//throw new Exception("invalid registration token");
        	//TODO: either throw exception or fix this jsp to work for both registration and password
        	return "redirect:/errors/invalidToken.html";
        	
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        	//TODO: GUI: figure out how to set messages on the generic errorData page
            //model.addAttribute("message", messages.getMessage("auth.message.expired", null, locale));
            //model.addAttribute("expired", true);
        	model.addAttribute("register", true);
            model.addAttribute("token", token);
            //return "redirect:/badUser.html?lang=" + locale.getLanguage();
        	//return "redirect:/errors/errorData";
        	//throw new Exception("registration token expired");
        	//TODO: either throw exception or fix this jsp to work for both registration and password
        	return "redirect:/errors/expiredToken.html";
        	
        }

        user.setEnabled(1);
        userService.updateUser(user);
        
        logger.info("user updated");
        
        //TODO: GUI: figure out how to set messages on the login page
        //model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));
        //return "redirect:/login.html?lang=" + locale.getLanguage();
        return "redirect:/user/login";
    }
	
	@RequestMapping(value = "/user/resendRegistrationToken", method = RequestMethod.GET)
    public String resendRegistrationToken(final HttpServletRequest request, @RequestParam("token") final String token) {
        final VerificationToken newToken = userService.recreateUserVerificationToken(token);
        final User user = userService.getVerificationUser(newToken.getToken());
        final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        final SimpleMailMessage email = constructResendVerificationTokenEmail(appUrl, request.getLocale(), newToken, user);
        mailSender.send(email);

        return "redirect:/messages/signupMessage";
    }

	@RequestMapping(value = "/confirmPassword", method = RequestMethod.GET)
    public String confirmPassword(final Locale locale, final Model model, @RequestParam("id") final long id, @RequestParam("token") final String token) 
    		throws Exception {
		logger.info("confirmPasswordReset");		
		
		User user = null;
        final PasswordResetToken passwordResetToken = userService.getPasswordResetToken(token);
        if (passwordResetToken != null) {
        	user = passwordResetToken.getUser();
        }

    	if (passwordResetToken == null || user == null || user.getId() != id) {
        	//TODO: GUI: figure out how to set messages on the generic errorData page
            //final String message = messages.getMessage("auth.message.invalidToken", null, locale);
            //model.addAttribute("message", message);
    		model.addAttribute("password", true);
            //return "redirect:/badUser.html?lang=" + locale.getLanguage();
        	//return "redirect:/errors/errorData";
        	//throw new Exception("invalid reset password token");
        	//TODO: either throw exception or fix this jsp to work for both registration and password
        	return "redirect:/errors/invalidToken.html";
        }
        
        final Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        	//TODO: GUI: figure out how to set messages on the generic errorData page
            //model.addAttribute("message", messages.getMessage("auth.message.expired", null, locale));
        	model.addAttribute("password", true);
            model.addAttribute("token", token);
            //return "redirect:/badUser.html?lang=" + locale.getLanguage();
        	//return "redirect:/errors/errorData";
        	//throw new Exception("reset password token expired");
        	//TODO: either throw exception or fix this jsp to work for both registration and password
        	return "redirect:/errors/expiredToken.html";
        }

        //TODO: GUI: figure out how to set messages on the login page
        //model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));
        //return "redirect:/login.html?lang=" + locale.getLanguage();
        
        final Authentication auth = new UsernamePasswordAuthenticationToken(user, null, userDetailsService.loadUserByUsername(user.getEmail()).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        return "redirect:/user/newPassword";
    }	

	@RequestMapping(value = "/user/resendPasswordToken", method = RequestMethod.GET)
    public String resendPasswordToken(final HttpServletRequest request, @RequestParam("token") final String token) {
        final PasswordResetToken newToken = userService.recreatePasswordResetTokenForUser(token);
        final User user = userService.getPasswordResetUser(newToken.getToken());
        final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        final SimpleMailMessage email = constructResendPasswordTokenEmail(appUrl, request.getLocale(), newToken, user);
        mailSender.send(email);

        return "redirect:/messages/forgotMessage";
    }

	@RequestMapping(value = "/messages/signupMessage", method = RequestMethod.GET)
	public ModelAndView signupEmailSent(Locale locale, Model model) {
		ModelAndView view = new ModelAndView("/messages/userMessage");

        String title = "Thanks for signing up for RecipeOrganizer!";
		String msg = "An email has been sent to you with a link to complete your signup.";
		model.addAttribute("title", title);
		model.addAttribute("message", msg);
		
		return view;
	}

	@RequestMapping(value = "/messages/forgotMessage", method = RequestMethod.GET)
	public ModelAndView passwordEmailSent(Locale locale, Model model) {
		ModelAndView view = new ModelAndView("/messages/userMessage");

        String title = "Password reset";
		String msg = "An email has been sent to you with a link to change your password.";
		model.addAttribute("title", title);
		model.addAttribute("message", msg);
		
		return view;
	}
	
	//TODO: EXCEPTION: consider using an exception handler instead?
	//AJAX/JSON request for checking user (email) duplication
	@RequestMapping(value="/ajax/anon/lookupUser")
	@ResponseBody 
	public String lookupUser(@RequestParam("email") String lookupEmail, HttpServletResponse response) {
		
		logger.info("ajax/anon/lookupUser");
		logger.info("email =" + lookupEmail);
		
		//set default response, incl. empty JSON msg
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		//query the DB for the user
		boolean result = userService.doesUserEmailExist(lookupEmail);
		
		logger.info("lookupEmail result=" + result);
		
		//name was found
		if (result) {
			
			Locale locale = LocaleContextHolder.getLocale();
			response.setStatus(HttpServletResponse.SC_CONFLICT);

			//getMessage throws an error if the message is not found
			try {
				msg = messages.getMessage("Duplicate.user.email", null, locale);
			}
			catch (NoSuchMessageException e) {
				msg = "";
			};

			if (msg.isEmpty()) {
				msg = "Duplicate email.";
			}
		}

		return msg;
	}
	
	public static class UserEmail {
		
		@Email
		private String email;
		
		public UserEmail() {}
		
		public String getEmail() {
			return email;
		}
		
		public void setEmail(String email) {
			this.email = email;
		}
	}

	//TODO: SECURITY: move this elsewhere; combine with the methods in the two relevant listeners
	private final SimpleMailMessage constructResendVerificationTokenEmail(final String contextPath, final Locale locale, final VerificationToken newToken, final User user) {
        final String confirmationUrl = contextPath + "/confirmRegistration.html?token=" + newToken.getToken();
        final String message = messages.getMessage("signupSuccess", null, locale);
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject("Resend Registration Token");
        email.setText(message + " \r\n" + confirmationUrl);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

	//TODO: SECURITY: move this elsewhere; combine with the methods in the two relevant listeners
	private final SimpleMailMessage constructResendPasswordTokenEmail(final String contextPath, final Locale locale, final PasswordResetToken token, final User user) {
		final String confirmationUrl = contextPath + "/confirmPassword?id=" + user.getId() + "&token=" + token.getToken();
        final String message = messages.getMessage("passwordReset", null, locale);
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject("Resend Password Token");
        email.setText(message + " \r\n" + confirmationUrl);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }
}

