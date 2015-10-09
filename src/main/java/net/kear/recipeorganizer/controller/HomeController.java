package net.kear.recipeorganizer.controller;

import java.text.DateFormat;
import java.util.Date;
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

import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.security.AuthCookie;
import net.kear.recipeorganizer.util.UserInfo;

@Controller
public class HomeController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthCookie authCookie;
	
	@Autowired
	private UserInfo userInfo;
	
	@RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
	public String getHome(Locale locale, Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		logger.info("getHome");
		
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
	public String getAbout(Locale locale, Model model, HttpSession session) {
		
		logger.info("getAbout");
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate);

		return "about";
	}

	@RequestMapping(value = "/thankyou", method = RequestMethod.GET)
	public String getThankyou(Locale locale, Model model) {
		
		logger.info("getThankyou");
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate);

		return "thankyou";
	}
}