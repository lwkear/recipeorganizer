package net.kear.recipeorganizer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.CategoryService;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.persistence.service.RoleService;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.util.FileActions;
import net.kear.recipeorganizer.util.FileTypes;

@Controller
public class AdminController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private UserService userService;	
	@Autowired
	private RoleService roleService;
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	private FileActions fileAction;
	@Autowired
	private MessageSource messages;
	@Autowired
	private ExceptionLogService logService;

	/********************************/
	/*** User maintenance handler ***/
	/********************************/
	@RequestMapping(value = "/admin/userMaint", method = RequestMethod.GET)
	public String userMaint(Model model) {
		logger.info("admin/userMaint");
		
		List<Role> roles = roleService.getRoles();
		List<User> users = userService.getUsers();
		List<Long> userIds = new ArrayList<Long>();
		for (User u : users) {
			logger.info("userService: " + u);
			userIds.add(u.getId());
		}
		
		List<Object> allPrinc = sessionRegistry.getAllPrincipals();
		
		for (Object obj : allPrinc) {
			List<SessionInformation> sessions = sessionRegistry.getAllSessions(obj, false);	//true returns expired sessions

			for (SessionInformation sess : sessions) {
				User user = (User) sess.getPrincipal();
				String sessId = sess.getSessionId();
				Date sessDate = sess.getLastRequest();
				
				logger.info("sessReg user: " + user);
				logger.info("sessReg userId: " + user.getId());
				logger.info("sessReg sessId: " + sessId);
				logger.info("sessReg sessDate: " + sessDate.toString());
				
				if (userIds.contains(user.getId())) {
					logger.info("sessReg found principal in user list");
					
					int ndx = userIds.indexOf(user.getId());
					if (ndx >= 0) {
						User u = users.get(ndx);
						u.setLoggedIn(true);	
					}					
				}
			}
		}

		model.addAttribute("roles", roles);
		model.addAttribute("users", users);
		
		return "admin/userMaint";
	}

	@RequestMapping(value="admin/deleteUser")
	@ResponseBody 
	public String deleteUser(@RequestParam("userId") Long userId, HttpServletResponse response, Locale locale) {
		logger.info("admin/deleteUser");
		logger.info("userId=" + userId);
		
		//set default response
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		//delete the user
		try {
			userService.deleteUser(userId);
		} catch (Exception ex) {
			logService.addException(ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			msg = messages.getMessage("exception.deleteUser", null, ex.getClass().getSimpleName(), locale);
			return msg;
		}
		
		String fileName = fileAction.fileExists(FileTypes.AVATAR, userId);
		if (fileName != null && fileName.length() > 0)
			//errors are not fatal and will be logged by FileAction
			fileAction.deleteFile(FileTypes.AVATAR, userId, fileName);
		
		return msg;
	}

	@RequestMapping(value = "/admin/getUser/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public User getUser(@PathVariable Long userId, HttpServletResponse response, Locale locale) {
		logger.info("admin/getUser GET");
		
		User user = null;
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		try {
			user = userService.getUser(userId);
		} catch (Exception ex) {
			logService.addException(ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			msg = messages.getMessage("exception.getUser", null, ex.getClass().getSimpleName(), locale);
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			try {
				response.getWriter().write(msg);
			} catch (IOException e) {}
		}
		
		String userStr = user.toString();
		logger.info("user.toString: " + userStr);
		
		return user;
	}

	@RequestMapping(value="admin/updateUser", method = RequestMethod.POST)
	@ResponseBody 
	public String updateUser(@RequestBody User user, HttpServletResponse response, Locale locale) {
		logger.info("admin/updateUser");
		logger.info("user=" + user);
		
		//set default response
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		//get the count for the user
		try {
			//fixes issue with the user object not containing the profile userId
			if (user.getUserProfile() != null)
				user.getUserProfile().setUser(user);
			userService.updateUser(user);
		} catch (Exception ex) {
			logService.addException(ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			msg = messages.getMessage("exception.updateUser", null, ex.getClass().getSimpleName(), locale);
		}
		
		return msg;
	}

	/************************************/
	/*** Category maintenance handler ***/
	/************************************/
	@RequestMapping(value = "/admin/category", method = RequestMethod.GET)
	public String getCategory(Model model) {
		logger.info("admin/category GET");
		
		Category category = new Category();
		model.addAttribute("category", category);
		model.addAttribute("categoryList", categoryService.listCategory());
		
		return "admin/category";
	}
	
	@RequestMapping(value = "/admin/category", method = RequestMethod.POST, params = {"save"})	
	public String saveCategory(@ModelAttribute Category category) {
		logger.info("admin/category POST save");
		
		if (category.getId() == 0) {
			logger.info("ID = 0");
			logger.info("Name = " + category.getName());
			
			categoryService.addCategory(category);
		}
		else {
			logger.info("ID = " + category.getId());
			logger.info("Name = " + category.getName());
			
			categoryService.updateCategory(category);
		}
		
		return "redirect:category";
	}

	@RequestMapping(value = "/admin/category", method = RequestMethod.POST, params = {"delete"})	
	public String deleteCategory(@ModelAttribute Category category) {
		logger.info("admin/category POST delete");
		logger.info("ID = " + category.getId());
		logger.info("Name = " + category.getName());
		
		/* Must check both fields - the ID is hidden and doesn't get cleared when the Reset button is clicked
		 * This is the default behavior of Reset and not worth the effort to override */
		if ((category.getId() != 0) && (category.getName() != "")) {
			categoryService.deleteCategory(category.getId());
		}

		return "redirect:category";
	}
}

