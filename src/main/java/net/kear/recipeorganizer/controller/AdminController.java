package net.kear.recipeorganizer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.kear.recipeorganizer.persistence.dto.FlaggedCommentDto;
import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.CategoryService;
import net.kear.recipeorganizer.persistence.service.CommentService;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.RoleService;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.util.ConstraintMap;
import net.kear.recipeorganizer.util.FileActions;
import net.kear.recipeorganizer.util.FileType;

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
	private CommentService commentService;
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	private FileActions fileAction;
	@Autowired
	private MessageSource messages;
	@Autowired
	private ExceptionLogService logService;
	@Autowired
	private ConstraintMap constraintMap;

	/********************************/
	/*** User maintenance handler ***/
	/********************************/
	@RequestMapping(value = "/admin/users", method = RequestMethod.GET)
	public String userMaint(Model model) {
		logger.info("admin/users GET");
		
		List<Role> roles = roleService.getRoles();
		List<User> users = userService.getUsers();
		List<Long> userIds = new ArrayList<Long>();
		for (User u : users) {
			logger.debug("userService: " + u);
			userIds.add(u.getId());
		}
		
		List<Object> allPrinc = sessionRegistry.getAllPrincipals();
		
		for (Object obj : allPrinc) {
			List<SessionInformation> sessions = sessionRegistry.getAllSessions(obj, false);	//true returns expired sessions

			for (SessionInformation sess : sessions) {
				User user = (User) sess.getPrincipal();
				String sessId = sess.getSessionId();
				Date sessDate = sess.getLastRequest();
				
				logger.debug("sessReg user: " + user);
				logger.debug("sessReg userId: " + user.getId());
				logger.debug("sessReg sessId: " + sessId);
				logger.debug("sessReg sessDate: " + sessDate.toString());
				
				if (userIds.contains(user.getId())) {
					logger.debug("sessReg found principal in user list");
					
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
		
		return "admin/users";
	}

	@RequestMapping(value="admin/deleteUser", method = RequestMethod.POST)
	@ResponseBody 
	public String deleteUser(@RequestParam("userId") Long userId, HttpServletResponse response, Locale locale) {
		logger.info("admin/deleteUser POST: userId=" + userId);
		
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
		
		String fileName = fileAction.fileExists(FileType.AVATAR, userId);
		if (fileName != null && fileName.length() > 0)
			//errors are not fatal and will be logged by FileAction
			fileAction.deleteFile(FileType.AVATAR, userId, fileName);
		
		return msg;
	}

	@RequestMapping(value = "/admin/getUser", method = RequestMethod.GET)
	@ResponseBody
	public User getUser(@RequestParam("userId") Long userId, HttpServletResponse response, Locale locale) {
		logger.info("admin/getUser GET: userId=" + userId);
		
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
		logger.debug("user.toString: " + userStr);
		
		return user;
	}

	@RequestMapping(value="admin/updateUser", method = RequestMethod.POST)
	@ResponseBody 
	public String updateUser(@RequestBody User user, HttpServletResponse response, Locale locale) {
		logger.info("admin/updateUser POST: userId=" + user.getId());
		
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
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", Category.class); 
		model.addAttribute("sizeMap", sizeMap);
	
		return "admin/category";
	}
	
	@RequestMapping(value = "/admin/category", method = RequestMethod.POST, params = {"save"})	
	public String saveCategory(Model model, @ModelAttribute @Valid Category category, BindingResult result, Locale locale) {
		logger.info("admin/category POST save: cat=" + category.getName());

		if (result.hasErrors()) {
			logger.debug("Validation errors");
			return "admin/category";
		}
		
		try {
			if (category.getId() == 0) {
				logger.debug("ID = 0");
				logger.debug("Name = " + category.getName());
				categoryService.addCategory(category);
			}
			else {
				logger.debug("ID = " + category.getId());
				logger.debug("Name = " + category.getName());
				categoryService.updateCategory(category);
			}
		} catch (DataIntegrityViolationException ex) {
			logService.addException(ex);
			String msg = messages.getMessage("exception.saveCategory", null, ex.getClass().getSimpleName(), locale);
			FieldError fieldError = new FieldError("category", "name", msg);
			result.addError(fieldError);
			return "admin/category";
		} catch (Exception ex) {
			logService.addException(ex);
			String msg = messages.getMessage("exception.categoryError", null, ex.getClass().getSimpleName(), locale);
			FieldError fieldError = new FieldError("category", "name", msg);
			result.addError(fieldError);
			return "admin/category";
		}
		
		return "redirect:category";
	}

	@RequestMapping(value = "/admin/category", method = RequestMethod.POST, params = {"delete"})	
	public String deleteCategory(Model model, @ModelAttribute @Valid Category category, BindingResult result, Locale locale) {
		logger.info("admin/category POST delete: catId/name=" + category.getId() + "/" + category.getName());

		if (result.hasErrors()) {
			logger.debug("Validation errors");
			return "admin/category";
		}
		
		try {
			categoryService.deleteCategory(category.getId());
		} catch (DataIntegrityViolationException ex) {
			logService.addException(ex);
			String msg = messages.getMessage("exception.deleteCategory", null, ex.getClass().getSimpleName(), locale);
			FieldError fieldError = new FieldError("category", "name", msg);
			result.addError(fieldError);
			return "admin/category";
		} catch (Exception ex) {
			logService.addException(ex);
			String msg = messages.getMessage("exception.categoryError", null, ex.getClass().getSimpleName(), locale);
			FieldError fieldError = new FieldError("category", "name", msg);
			result.addError(fieldError);
			return "admin/category";
		}

		return "redirect:category";
	}

	/*******************************/
	/*** Comments review handler ***/
	/*******************************/
	@RequestMapping(value = "/admin/comments", method = RequestMethod.GET)
	public String getComments(Model model) {
		logger.info("admin/comments GET");

		List<FlaggedCommentDto> comments = commentService.getFlaggedComments(); 
		
		model.addAttribute("comments", comments);
		return "admin/comments";
	}

	@RequestMapping(value="admin/deleteComment", method = RequestMethod.POST)
	@ResponseBody 
	public String deleteComment(@RequestParam("commentId") Long commentId, HttpServletResponse response, Locale locale) {
		logger.info("admin/deleteComment POST: commentId=" + commentId);
		
		//set default response
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		//delete the user
		try {
			commentService.deleteComment(commentId);
		} catch (Exception ex) {
			logService.addException(ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			msg = messages.getMessage("exception.deleteComment", null, ex.getClass().getSimpleName(), locale);
			return msg;
		}
		
		return msg;
	}

	@RequestMapping(value="admin/removeCommentFlag", method = RequestMethod.POST)
	@ResponseBody 
	public String removeCommentFlag(@RequestParam("commentId") Long commentId, HttpServletResponse response, Locale locale) {
		logger.info("admin/removeCommentFlag POST: commentId=" + commentId);
		
		//set default response
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		//delete the user
		try {
			commentService.setCommentFlag(commentId, 0);
		} catch (Exception ex) {
			logService.addException(ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			msg = messages.getMessage("exception.unflagComment", null, ex.getClass().getSimpleName(), locale);
			return msg;
		}
		
		return msg;
	}

	/*******************************/
	/*** Recipe approval handler ***/
	/*******************************/
	@RequestMapping(value = "/admin/approval", method = RequestMethod.GET)
	public String getApprovalRecipes(Model model) {
		logger.info("admin/approval GET");

		List<RecipeListDto> recipes = recipeService.approveRecipesList();
		
		model.addAttribute("recipes", recipes);
		return "admin/approveRecipes";
	}

	@RequestMapping(value="admin/approveRecipe", method = RequestMethod.POST)
	@ResponseBody 
	public String approveRecipe(@RequestParam("recipeId") Long recipeId, HttpServletResponse response, Locale locale) {
		logger.info("admin/approveRecipe POST: recipeId=" + recipeId);
		
		//set default response
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		//delete the user
		try {
			recipeService.approveRecipe(recipeId);
		} catch (Exception ex) {
			logService.addException(ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			msg = messages.getMessage("exception.approveRecipe", null, ex.getClass().getSimpleName(), locale);
			return msg;
		}
		
		return msg;
	}
}
