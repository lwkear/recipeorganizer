package net.kear.recipeorganizer.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.kear.recipeorganizer.persistence.service.UsersService;
import net.kear.recipeorganizer.security.AuthCookie;
import net.kear.recipeorganizer.util.UserInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private AuthCookie authCookie;
	
	@Autowired
	private UserInfo userInfo;
	
	@RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
	public String loadHome(Locale locale, Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		logger.info("HomeController: loadHome");
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate);

		Date createTime = new Date(session.getCreationTime());
		Date lastAccess = new Date(session.getLastAccessedTime());
		int maxInactive = session.getMaxInactiveInterval();
		String sess = session.toString();
		String sessID = session.getId();
		
		String sCreate = "Session created on: " + createTime;
		String sLast = "Session last accessed on: " + lastAccess;
		String sInactive = "Session expires after: " + maxInactive + " seconds";
		String sID = "Session ID: " + sessID;
		
		model.addAttribute("create", sCreate);
		model.addAttribute("last", sLast);
		model.addAttribute("inactive", sInactive);
		model.addAttribute("sess", sess);
		model.addAttribute("sessID", sID);
		
		if (!authCookie.cookieExists(request))
			authCookie.setCookie(request, response, userInfo.getName());
		
		return "home";
	}

	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String loadAbout(Locale locale, Model model, HttpSession session) {
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate);

		return "about";
	}

	@RequestMapping(value = "/thankyou", method = RequestMethod.GET)
	public String loadThankyou(Locale locale, Model model) {
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate);

		return "thankyou";
	}

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
	@RequestMapping(value="/ajax/all/getSessionTimeout")
	@ResponseBody 
	public Integer getSessionTimeout(HttpSession session, HttpServletResponse response) {
		
		Integer maxInactive = session.getMaxInactiveInterval();
		
		logger.info("getSessionTimeout returned" + maxInactive + " seconds");
		
		return maxInactive;
	}

	//AJAX/JSON request for resetting the session timeout
	//TODO: this should be a POST
	@RequestMapping(value="/ajax/auth/setSessionTimeout", method = RequestMethod.POST)
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