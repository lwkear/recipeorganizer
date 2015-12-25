package net.kear.recipeorganizer.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.kear.recipeorganizer.security.AuthCookie;

@Controller
public class ErrorController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AuthCookie authCookie;
	
	@RequestMapping(value = "/errors/402", method = RequestMethod.GET)
	public ModelAndView error402(Locale locale, Model model) {
		ModelAndView view = new ModelAndView("/errors/402");

		return view;
	}
	
	@RequestMapping(value = "/errors/403", method = RequestMethod.GET)
	public ModelAndView error403(Locale locale, Model model) {
		ModelAndView view = new ModelAndView("/errors/403");

		return view;
	}

	@RequestMapping(value = "/errors/expiredSession", method = RequestMethod.GET)
	public ModelAndView expiredSession(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView view = new ModelAndView("/errors/expiredSession");

		//this page may be called from the client if a user's session times out, so need to reset the cookie
		//otherwise, the client cookie will continue to hold the user authentication and the timeout popup will be displayed repeatedly
		authCookie.setCookie(request, response, "anonymousUser");
		
		return view;
	}
	
	@RequestMapping(value = "/errors/invalidSession", method = RequestMethod.GET)
	public ModelAndView invalidSession(Locale locale, Model model) {
		ModelAndView view = new ModelAndView("/errors/invalidSession");

		return view;
	}
	
	//AJAX/JSON request for getting the timeout interval for the current user
	@RequestMapping(value="/getSessionTimeout")
	@ResponseBody 
	public Integer getSessionTimeout(HttpSession session, HttpServletResponse response) {
		
		Integer maxInactive = session.getMaxInactiveInterval();
		
		logger.info("getSessionTimeout returned " + maxInactive + " seconds");
		
		return maxInactive;
	}

	//AJAX/JSON request for resetting the session timeout
	@RequestMapping(value="/setSessionTimeout", method = RequestMethod.POST)
	@ResponseBody 
	public String setSessionTimeout(HttpSession session, HttpServletResponse response) {
		
		response.setStatus(HttpServletResponse.SC_OK);
		
		Integer maxInactive = session.getMaxInactiveInterval();
		session.setMaxInactiveInterval(maxInactive);
		
		logger.info("setSessionTimeout for " + maxInactive + " seconds");
		
		return "{}";
	}
}



/*	sample code
@RequestMapping(value = "/403", method = RequestMethod.GET)
public ModelAndView accesssDenied() {

    ModelAndView model = new ModelAndView();

    // check if user is login
    Authentication auth = SecurityContextHolder.getContext()
            .getAuthentication();
    if (!(auth instanceof AnonymousAuthenticationToken)) {
        UserDetails userDetail = (UserDetails) auth.getPrincipal();
        System.out.println(userDetail);

        model.addObject("username", userDetail.getUsername());

    }

    model.setViewName("403");
    return model;
}*/