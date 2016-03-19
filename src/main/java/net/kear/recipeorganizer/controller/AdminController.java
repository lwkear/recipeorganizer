package net.kear.recipeorganizer.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
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
import org.springframework.web.bind.annotation.ResponseStatus;

import net.kear.recipeorganizer.enums.FileType;
import net.kear.recipeorganizer.exception.AccessUserException;
import net.kear.recipeorganizer.exception.RestException;
import net.kear.recipeorganizer.interceptor.MaintenanceInterceptor;
import net.kear.recipeorganizer.persistence.dto.FlaggedCommentDto;
import net.kear.recipeorganizer.persistence.dto.IngredientReviewDto;
import net.kear.recipeorganizer.persistence.dto.MaintenanceDto;
import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.CategoryService;
import net.kear.recipeorganizer.persistence.service.CommentService;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.persistence.service.IngredientService;
import net.kear.recipeorganizer.persistence.service.RecipeIngredientService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.RoleService;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.solr.SolrUtil;
import net.kear.recipeorganizer.util.ResponseObject;
import net.kear.recipeorganizer.util.db.ConstraintMap;
import net.kear.recipeorganizer.util.file.FileActions;
import net.kear.recipeorganizer.util.maint.MaintAware;
import net.kear.recipeorganizer.util.maint.MaintenanceUtil;

@Controller
public class AdminController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private IngredientService ingredientService;
	@Autowired
	private RecipeIngredientService recipeIngredientService;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private CommentService commentService;
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
	@Autowired
	private SolrUtil solrUtil;
	@Autowired
	MaintenanceUtil maintUtil;
	@Autowired
	private MaintenanceInterceptor maintInterceptor; 

	/********************************/
	/*** User maintenance handler ***/
	/********************************/
	@MaintAware
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
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject deleteUser(@RequestParam("userId") Long userId) throws RestException {
		logger.info("admin/deleteUser POST: userId=" + userId);
		
		try {
			userService.deleteUser(userId);
		} catch (Exception ex) {
			throw new RestException("exception.deleteUser", ex);
		}
		
		String fileName = fileAction.fileExists(FileType.AVATAR, userId);
		if (fileName.length() > 0)
			//errors are not fatal and will be logged by FileAction
			fileAction.deleteFile(FileType.AVATAR, userId, fileName);
		
		return new ResponseObject();
	}

	@RequestMapping(value = "/admin/getUser", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public User getUser(@RequestParam("userId") Long userId) throws RestException {
		logger.info("admin/getUser GET: userId=" + userId);
		
		User user = null;
		try {
			user = userService.getUser(userId);
		} catch (Exception ex) {
			throw new RestException("exception.getUser", ex);
		}

		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (user == null) {
			throw new RestException("exception.getUser", new AccessUserException());
		}
		
		//a user should never not have a role, but just in case...
		if (user.getRole() == null) {
			Role role = new Role();
			role.setId(0L);
			user.setRole(role);
		}
		
		logger.debug("user.toString: " + user.toString());
		
		return user;
	}

	@RequestMapping(value="admin/updateUser", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject updateUser(@RequestBody User user, HttpSession session) throws RestException {
		logger.info("admin/updateUser POST: userId=" + user.getId());

		User originalUser = userService.getUser(user.getId());
		//Role originalRole = originalUser.getRole();
		
		//update the user
		try {
			//fixes issue with the profile object not containing the userId
			if (user.getUserProfile() != null)
				user.getUserProfile().setUser(user);
			userService.updateUser(user);
		} catch (Exception ex) {
			throw new RestException("exception.updateUser", ex);
		}

		//TODO: SECURITY: sendPrivateMessage on role change
		//if (originalRole != user.getRole())
		
		//if the user no longer should have access then expire the user's session if present
		if ((originalUser.isEnabled() 			&& !user.isEnabled())		||
			(!originalUser.isLocked()			&& user.isLocked())			||
			(!originalUser.isAccountExpired()	&& user.isAccountExpired())	||
			(!originalUser.isPasswordExpired()	&& user.isPasswordExpired())) {
			List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
			if (allPrincipals != null && allPrincipals.size() > 0) {
				for (Object obj : allPrincipals) {
					UserDetails principal = (UserDetails) obj;
					if (principal.getUsername().equalsIgnoreCase(user.getEmail())) {
						List<SessionInformation> sessions = sessionRegistry.getAllSessions(obj, false);
						for (SessionInformation sessionInfo : sessions) {
							
							String sessId = sessionInfo.getSessionId();
							Object sessObj = sessionInfo.getPrincipal();
							boolean exp = sessionInfo.isExpired();
							logger.debug("Found sessionInfo id/obj/exp: " + sessId + " / " + sessObj.toString() + " / " + exp);
							
							if (!sessionInfo.isExpired()) 
								sessionInfo.expireNow();
						}
					}
				}
			}
		}
		
		return new ResponseObject();		
	}

	/************************************/
	/*** Category maintenance handler ***/
	/************************************/
	@MaintAware
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
		logger.info("admin/category POST save: id/name=" + category.getId() + "/" + category.getName());

		if (result.hasErrors()) {
			logger.debug("Validation errors");
			return "admin/category";
		}
		
		try {
			if (category.getId() == 0)
				categoryService.addCategory(category);
			else
				categoryService.updateCategory(category);
		} catch (DataIntegrityViolationException ex) {
			logService.addException(ex);
			String msg = messages.getMessage("exception.saveCategory", null, ex.getClass().getSimpleName(), locale);
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
		}

		return "redirect:category";
	}

	/**********************************/
	/*** Ingredients review handler ***/
	/**********************************/
	@MaintAware
	@RequestMapping(value = "/admin/ingredients", method = RequestMethod.GET)
	public String getIngredientReviewList(Model model) {
		logger.info("admin/ingredients GET");

		List<IngredientReviewDto> ingredients = ingredientService.listNotReviewed(); 
		
		model.addAttribute("ingredients", ingredients);
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", Ingredient.class); 
		model.addAttribute("sizeMap", sizeMap);
		return "admin/ingredients";
	}

	@RequestMapping(value="admin/deleteIngredient", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject deleteIngredient(@RequestParam("ingredId") Long ingredId) throws RestException {
		logger.info("admin/deleteIngredient POST: ingredId=" + ingredId);
		
		try {
			ingredientService.deleteIngredient(ingredId);
		} catch (Exception ex) {
			throw new RestException("exception.deleteIngredient", ex);
		}

		return new ResponseObject();
	}

	@RequestMapping(value="admin/approveIngredient", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject approveIngredient(@RequestParam("ingredId") Long ingredId) throws RestException {
		logger.info("admin/approveIngredient POST: ingredId=" + ingredId);
		
		try {
			ingredientService.setReviewed(ingredId, 1);
		} catch (Exception ex) {
			throw new RestException("exception.approveIngredient", ex);
		}
		
		return new ResponseObject();
	}

	@RequestMapping(value="admin/getIngredient", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public Ingredient getIngredient(@RequestParam("ingredId") Long ingredId) throws RestException {
		logger.info("admin/getIngredient GET: ingredId=" + ingredId);
		
		Ingredient ingredient = null;
		try {
			ingredient = ingredientService.getIngredient(ingredId);
		} catch (Exception ex) {
			throw new RestException("exception.updateIngredient", ex);
		}
		
		return ingredient;
	}

	@RequestMapping(value="admin/updateIngredient", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public Ingredient updateIngredient(@RequestBody Ingredient ingredient) throws RestException {
		logger.info("admin/updateIngredient POST: ingredId=" + ingredient.getId());
		
		try {
			ingredientService.updateIngredient(ingredient);
		} catch (DataIntegrityViolationException ex) {
			String exClass = ex.getClass().toString();
			logger.debug("Exception: " + exClass);
			throw new RestException("exception.duplicateIngredient", ex);
		} catch (Exception ex) {
			throw new RestException("exception.updateIngredient", ex);
		}
		
		return ingredient;
	}

	@RequestMapping(value="admin/replaceIngredient", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public IngredientReviewDto replaceIngredient(@RequestBody IngredientReviewDto ingredientReviewDto) throws RestException {
		logger.info("admin/replaceIngredient POST: old ingredId/new ingredId " + ingredientReviewDto.getId() + " / " + ingredientReviewDto.getUsage());
		
		try {
			recipeIngredientService.replaceIngredient(ingredientReviewDto);
			ingredientService.deleteIngredient(ingredientReviewDto.getId());
		} catch (Exception ex) {
			throw new RestException("exception.replaceIngredient", ex);
		}
				
		//return the original object so the client can remove the ingredient from the list
		return ingredientReviewDto;
	}
	
	/*******************************/
	/*** Comments review handler ***/
	/*******************************/
	@MaintAware
	@RequestMapping(value = "/admin/comments", method = RequestMethod.GET)
	public String getComments(Model model) {
		logger.info("admin/comments GET");

		List<FlaggedCommentDto> comments = commentService.getFlaggedComments(); 
		
		model.addAttribute("comments", comments);
		return "admin/comments";
	}

	@RequestMapping(value="admin/deleteComment", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject deleteComment(@RequestParam("commentId") Long commentId) throws RestException {
		logger.info("admin/deleteComment POST: commentId=" + commentId);
		
		try {
			commentService.deleteComment(commentId);
		} catch (Exception ex) {
			throw new RestException("exception.deleteComment", ex);
		}

		return new ResponseObject();
	}

	@RequestMapping(value="admin/removeCommentFlag", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject removeCommentFlag(@RequestParam("commentId") Long commentId) throws RestException {
		logger.info("admin/removeCommentFlag POST: commentId=" + commentId);
		
		try {
			commentService.setCommentFlag(commentId, 0);
		} catch (Exception ex) {
			throw new RestException("exception.unflagComment", ex);
		}
		
		return new ResponseObject();
	}

	/*******************************/
	/*** Recipe approval handler ***/
	/*******************************/
	@MaintAware
	@RequestMapping(value = "/admin/approval", method = RequestMethod.GET)
	public String getApprovalRecipes(Model model) {
		logger.info("admin/approval GET");

		List<RecipeListDto> recipes = recipeService.approveRecipesList();
		
		model.addAttribute("recipes", recipes);
		return "admin/approveRecipes";
	}

	@RequestMapping(value="admin/approveRecipe", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)	
	public ResponseObject approveRecipe(@RequestParam("recipeId") Long recipeId) throws RestException {
		logger.info("admin/approveRecipe POST: recipeId=" + recipeId);
		
		Recipe recipe; 
		
		try {
			recipe = recipeService.getRecipe(recipeId);
			recipeService.approveRecipe(recipeId);
		} catch (Exception ex) {
			throw new RestException("exception.approveRecipe", ex);
		}
		
		solrUtil.deleteRecipe(recipeId);
		solrUtil.addRecipe(recipe);
		
		return new ResponseObject();
	}
	
	/**********************************/
	/*** System Maintenance handler ***/
	/**********************************/
	@RequestMapping(value = "/admin/maintenance", method = RequestMethod.GET)
	public String getMaintenance(Model model, Locale locale) {
		logger.info("admin/maintenance GET");
				
		MaintenanceDto maintDto = new MaintenanceDto();
		maintDto.setMaintenanceEnabled(maintInterceptor.isMaintenanceEnabled());
		maintDto.setEmergencyMaintenance(maintInterceptor.isEmergencyMaintenance());
		maintDto.setEmergencyDuration(maintInterceptor.getEmergencyDuration());
		maintDto.setDuration(maintInterceptor.getDuration());
		maintDto.setStartTime(maintInterceptor.getStartHourMinute());
		maintDto.setDaysOfWeek(maintInterceptor.getDaysOfWeek());
		
		model.addAttribute("maintenanceDto", maintDto);
		model.addAttribute("dayMap", maintUtil.getWeekMap(locale));
		model.addAttribute("maintWindow", maintInterceptor.getNextStartWindow(false, "sysmaint.nextwindow", locale));
		return "admin/maintenance";
	}

	@RequestMapping(value = "/admin/maintenance", method = RequestMethod.POST)
	public String postMaintenance(Model model, @ModelAttribute @Valid MaintenanceDto maintenanceDto, BindingResult result, Locale locale) {	
		logger.info("admin/maintenance POST");
		logger.debug("MaintenanceDto: " + maintenanceDto);
		
		model.addAttribute("dayMap", maintUtil.getWeekMap(locale));
		model.addAttribute("maintWindow", maintInterceptor.getNextStartWindow(false, "sysmaint.nextwindow", locale));

		if (result.hasErrors()) {
			logger.debug("Validation errors");
			return "admin/maintenance";
		}

		if (maintInterceptor.refreshSettings(maintenanceDto))
			maintInterceptor.setNextWindow();
		
		model.addAttribute("maintWindow", maintInterceptor.getNextStartWindow(false, "sysmaint.nextwindow", locale));
		return "admin/maintenance";
	}
}