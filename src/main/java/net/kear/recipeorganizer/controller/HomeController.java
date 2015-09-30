package net.kear.recipeorganizer.controller;

import java.security.Principal;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.kear.recipeorganizer.persistence.model.Users;
import net.kear.recipeorganizer.persistence.service.UsersService;
import net.kear.recipeorganizer.security.CurrentUser;
import net.kear.recipeorganizer.util.AuthCookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	//@Autowired
	//private CurrentUser currentUser;
	
	@RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
	public String loadHome(Locale locale, Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		logger.info("HomeController: loadHome");
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate);

		String username = "";
		String password = "";
		String auth = "";
		Object principal;
		
		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {
		
			Authentication authentication = context.getAuthentication();
			if (authentication != null) {
				
				/*for (GrantedAuthority grantAuth : authentication.getAuthorities()) {
					auth += grantAuth.getAuthority() + " / ";
		        }*/
			
				principal = authentication.getPrincipal();
				if (principal != null) {
					
					if (principal instanceof UserDetails) {
						UserDetails userDetails = (UserDetails)principal;
						
						username = userDetails.getUsername();
						password = userDetails.getPassword();

						@SuppressWarnings("unchecked")
						Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>)userDetails.getAuthorities();
						for (SimpleGrantedAuthority element : authorities) {
							auth += element.getAuthority() + " / ";
						}
					} 
					else {
						username = principal.toString();
					}
				}
			}
		}
		
		//session.setMaxInactiveInterval(120);
		
		Date createTime = new Date(session.getCreationTime());
		Date lastAccess = new Date(session.getLastAccessedTime());
		int maxInactive = session.getMaxInactiveInterval();
		String sess = session.toString();
		String sessID = session.getId();
		
		String sCreate = "Session created on: " + createTime;
		String sLast = "Session last accessed on: " + lastAccess;
		String sInactive = "Session expires after: " + maxInactive + " seconds";
		String sID = "Session ID: " + sessID;
		
		model.addAttribute("username", username);
		model.addAttribute("password", password);
		model.addAttribute("auth", auth);
		model.addAttribute("create", sCreate);
		model.addAttribute("last", sLast);
		model.addAttribute("inactive", sInactive);
		model.addAttribute("sess", sess);
		model.addAttribute("sessID", sID);
		
		/*boolean foundAuth = false;
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (int i=0;i<cookies.length;i++) {
				Cookie cookie = cookies[i];
				logger.info("Home: cookieName = " + cookie.getName() + " cookieValue = " + cookie.getValue());
				if (cookie.getName().equalsIgnoreCase("authUser")) {
					foundAuth = true;
					break;					
				}
			}
		}
		
		if (!foundAuth ) {
			logger.info("Home: setting cookie.authUser = " + username);
			Cookie cookie = new Cookie("authUser", username);
			cookie.setHttpOnly(true);
			//cookie.setMaxAge(60);
			cookie.setPath(request.getContextPath());
			response.addCookie(cookie);
		}*/
		
		/*String attr = (String) session.getAttribute("authUser");
		logger.info("Home: session.authUser = " + attr);*/
		
		/*String userName = "anonymousUser";
		
		principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal != null) {
			UserDetails userDetails = (UserDetails)principal;
			userName = userDetails.getUsername();
			
			principal.getClass().getName();
		}*/
		
		authCookie.retrieveCookie(request, response);
		authCookie.setCookie(username);
		
		return "home";
	}

	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String loadAbout(Locale locale, Model model, HttpSession session) {
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate);

		//session.setMaxInactiveInterval(120);
		
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
	public ModelAndView expiredSession(Locale locale, Model model) {
		ModelAndView view = new ModelAndView("/errors/expiredSession");

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
		return maxInactive;
	}

	//AJAX/JSON request for resetting the session timeout
	//TODO: this should be a POST
	@RequestMapping(value="/setSessionTimeout") //, method = RequestMethod.POST)
	@ResponseBody 
	public String setSessionTimeout(HttpSession session, HttpServletResponse response) {
		
		response.setStatus(HttpServletResponse.SC_OK);
		
		Integer maxInactive = session.getMaxInactiveInterval();
		session.setMaxInactiveInterval(maxInactive);
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