package net.kear.recipeorganizer.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import net.kear.recipeorganizer.persistence.model.Recipe;
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
	public String getHome(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		logger.info("getHome");

		//tell the page to not include the white vertical filler
		model.addAttribute("vertFiller", "1");
		
		if (!authCookie.cookieExists(request))
			authCookie.setCookie(request, response, userInfo.getName());
		
		return "home";
	}

	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String getAbout(Model model, HttpSession session) {
		logger.info("getAbout");
		
		return "about";
	}

	@RequestMapping(value = "/about", method = RequestMethod.POST)
	/*public String postAbout(@RequestParam(value = "file", required = false) MultipartFile file) {*/
	public String postAbout(MultipartHttpServletRequest request) {
		logger.info("postAbout");

		MultipartFile file = request.getFile("file");
		
		if (!file.isEmpty()) {
            try {
            	String filePath = System.getProperty("java.io.tmpdir") + file.getOriginalFilename();
            	logger.info("originalname = " + file.getOriginalFilename());
            	logger.info("name = " + file.getName());
            	logger.info("filePath = " + filePath);
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                stream.write(file.getBytes());
                stream.close();
                logger.info("Successful upload");
                //recipe.setPhoto(file.getOriginalFilename());
            } catch (Exception e) {
            	logger.info("Exception: " + e.getMessage());
            }
        } else {
        	logger.info("Empty file");
        }
		
		return "about";
	}
	
	@RequestMapping(value = "/thankyou", method = RequestMethod.GET)
	public String getThankyou(Model model) {
		logger.info("getThankyou");

		//tell the page to not include the white vertical filler
		model.addAttribute("vertFiller", "1");
		
		return "thankyou";
	}
	
	@RequestMapping(value = "/faq", method = RequestMethod.GET)
	public String getFaq(Model model) {
		logger.info("getFaq");
		
		return "faq";
	}

	@RequestMapping(value = "/contact", method = RequestMethod.GET)
	public String getContact(Model model) {
		logger.info("getContact");
		
		return "contact";
	}

	@RequestMapping(value = "/testpage", method = RequestMethod.GET)
	public String getTestpage(Model model) {
		logger.info("getTestpage");

		Recipe recipe = new Recipe();
		recipe.setAllowShare(true);
		model.addAttribute("recipe", recipe);
		
		return "testpage";
	}

	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public String getStartpage(Model model) {
		logger.info("getStartpage");

		return "start";
	}
}

/*Date date = new Date();
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

String country = locale.getCountry();
String language = locale.getLanguage();
String sCountry = "Request country: " + country;
String sLanguage = "Request language: " + language;

model.addAttribute("create", sCreate);
model.addAttribute("last", sLast);
model.addAttribute("inactive", sInactive);
model.addAttribute("sess", sess);
model.addAttribute("sessID", sID);
model.addAttribute("country", sCountry);
model.addAttribute("language", sLanguage);*/
