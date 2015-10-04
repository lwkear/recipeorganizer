package net.kear.recipeorganizer.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import net.kear.recipeorganizer.persistence.model.Users;
import net.kear.recipeorganizer.persistence.service.UsersService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles requests for the signup page.
 */
@Controller
public class SignupController {
	
	private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

	@Autowired
	private UsersService usersService;

	@Autowired
	private MessageSource messages;
	
	@RequestMapping(value = "user/signup", method = RequestMethod.GET)
	public String loadSignup(Model model) {
		logger.info("signup GET");
		
		Users user = new Users();
		model.addAttribute("users", user);		
		
		return "user/signup";
	}
	
	@RequestMapping(value = "user/signup", method = RequestMethod.POST)
	public String submitSignup(Model model, @ModelAttribute @Valid Users user, BindingResult result) {
		logger.info("login POST");

		if (result.hasErrors()) {
			logger.info("Validation errors");
			return "user/signup";
		}
		
        usersService.addUser(user);
		
		return "redirect:/home";
	}

	//TODO: EXCEPTION: consider using an exception handler instead?
	//AJAX/JSON request for checking user (email) duplication
	@RequestMapping(value="/ajax/all/lookupUser")
	@ResponseBody 
	public String lookupUser(@RequestParam("email") String lookupEmail, HttpServletResponse response) {
		
		logger.info("user/lookupUser");
		logger.info("email =" + lookupEmail);
		
		//set default response, incl. empty JSON msg
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		//add the ingredient to the DB
		boolean result = usersService.doesUserEmailExist(lookupEmail);
		
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
