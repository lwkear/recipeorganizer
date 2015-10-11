package net.kear.recipeorganizer.controller;

import java.util.Calendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.kear.recipeorganizer.event.OnRegistrationCompleteEvent;
import net.kear.recipeorganizer.persistence.dto.UserDto;
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
	private MessageSource messages;

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
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
		model.addAttribute("user", user);		
		
		return "user/signup";
	}
	
	@RequestMapping(value = "user/signup", method = RequestMethod.POST)
	public String postSignup(Model model, @ModelAttribute @Valid UserDto userDto, BindingResult result, HttpServletRequest request) {
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
        
		return "redirect:/messages/emailSent";
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
				
		return "user/password";
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
	
	@RequestMapping(value = "user/resetPassword", method = RequestMethod.GET)
	public String getResetPassword(Model model) {
		logger.info("resetPassword GET");
		
		String email = "";
		model.addAttribute("email", email);
		
		return "user/resetPassword";
	}
	
	@RequestMapping(value = "user/resetPassword", method = RequestMethod.POST)
    //@ResponseBody
    //public String postforgotPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail) {
	public String postResetPassword(Model model, @ModelAttribute @Valid String email, BindingResult result, HttpServletRequest request) {

		final User user = userService.findUserByEmail(email);

		if (result.hasErrors()) {
			logger.info("Validation errors");
			return "user/forgotPassword";
		}

		try {
        	logger.info("password reset - publishing event");
        	final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        	eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
        } catch (Exception ex) {
        	//TODO: GUI: redisplay the reset page with an error message?
        	logger.debug("error encountered: " + ex.getMessage());
        }
        
		return "redirect:/messages/emailSent";		
    }

	@RequestMapping(value = "/confirmRegistration", method = RequestMethod.GET)
    public String confirmRegistration(final Locale locale, final Model model, @RequestParam("token") final String token) {
		logger.info("confirmRegistration");		
		
        final VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
        	//TODO: GUI: figure out how to set messages on the generic errorData page
            //final String message = messages.getMessage("auth.message.invalidToken", null, locale);
            //model.addAttribute("message", message);
            //return "redirect:/badUser.html?lang=" + locale.getLanguage();
        	return "redirect:/errors/errorData";        	
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        	//TODO: GUI: figure out how to set messages on the generic errorData page
            //model.addAttribute("message", messages.getMessage("auth.message.expired", null, locale));
            //model.addAttribute("expired", true);
            //model.addAttribute("token", token);
            //return "redirect:/badUser.html?lang=" + locale.getLanguage();
        	return "redirect:/errors/errorData";        	
        }

        user.setEnabled(1);
        userService.updateUser(user);
        
        logger.info("user updated");
        
        //TODO: GUI: figure out how to set messages on the login page
        //model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));
        //return "redirect:/login.html?lang=" + locale.getLanguage();
        return "redirect:/login";
    }	

	@RequestMapping(value = "/confirmPasswordReset", method = RequestMethod.GET)
    public String confirmPasswordReset(final Locale locale, final Model model, @RequestParam("token") final String token) {
		logger.info("confirmPasswordReset");		
		
        final PasswordResetToken passwordResetToken = userService.getPasswordResetToken(token);
        if (passwordResetToken == null) {
        	//TODO: GUI: figure out how to set messages on the generic errorData page
            //final String message = messages.getMessage("auth.message.invalidToken", null, locale);
            //model.addAttribute("message", message);
            //return "redirect:/badUser.html?lang=" + locale.getLanguage();
        	return "redirect:/errors/errorData";        	
        }

        final User user = passwordResetToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        	//TODO: GUI: figure out how to set messages on the generic errorData page
            //model.addAttribute("message", messages.getMessage("auth.message.expired", null, locale));
            //model.addAttribute("expired", true);
            //model.addAttribute("token", token);
            //return "redirect:/badUser.html?lang=" + locale.getLanguage();
        	return "redirect:/errors/errorData";        	
        }

        //TODO: GUI: figure out how to set messages on the login page
        //model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));
        //return "redirect:/login.html?lang=" + locale.getLanguage();
        return "redirect:/login";
    }	
	
	@RequestMapping(value = "/messages/emailSent", method = RequestMethod.GET)
	public ModelAndView emailSent(Locale locale, Model model) {
		ModelAndView view = new ModelAndView("/messages/emailSent");

        String msg = "An email has been sent to you with a link to complete your signup.";
        model.addAttribute("signupMessage", msg);
		
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
}
