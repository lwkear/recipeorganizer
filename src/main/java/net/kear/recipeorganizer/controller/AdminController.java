package net.kear.recipeorganizer.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.ResponseStatus;

import net.kear.recipeorganizer.enums.ApprovalAction;
import net.kear.recipeorganizer.enums.ApprovalReason;
import net.kear.recipeorganizer.enums.ApprovalStatus;
import net.kear.recipeorganizer.enums.FileType;
import net.kear.recipeorganizer.enums.MessageType;
import net.kear.recipeorganizer.enums.OptOutType;
import net.kear.recipeorganizer.event.UpdateSolrRecipeEvent;
import net.kear.recipeorganizer.exception.AccessUserException;
import net.kear.recipeorganizer.exception.RestException;
import net.kear.recipeorganizer.interceptor.MaintenanceInterceptor;
import net.kear.recipeorganizer.persistence.dto.EmailDto;
import net.kear.recipeorganizer.persistence.dto.FlaggedCommentDto;
import net.kear.recipeorganizer.persistence.dto.IngredientReviewDto;
import net.kear.recipeorganizer.persistence.dto.InvitationDto;
import net.kear.recipeorganizer.persistence.dto.MaintenanceDto;
import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.dto.RecipeMessageDto;
import net.kear.recipeorganizer.persistence.dto.UserDto;
import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.model.UserMessage;
import net.kear.recipeorganizer.persistence.service.CategoryService;
import net.kear.recipeorganizer.persistence.service.CommentService;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.persistence.service.IngredientService;
import net.kear.recipeorganizer.persistence.service.RecipeIngredientService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.RoleService;
import net.kear.recipeorganizer.persistence.service.UserMessageService;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.security.UserSecurityService;
import net.kear.recipeorganizer.solr.SolrUtil;
import net.kear.recipeorganizer.util.EncryptionUtil;
import net.kear.recipeorganizer.util.ResponseObject;
import net.kear.recipeorganizer.util.UserInfo;
import net.kear.recipeorganizer.util.db.ConstraintMap;
import net.kear.recipeorganizer.util.email.EmailDetail;
import net.kear.recipeorganizer.util.email.EmailReceiver;
import net.kear.recipeorganizer.util.email.EmailSender;
import net.kear.recipeorganizer.util.email.InvitationEmail;
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
	private UserSecurityService userSecurityService;
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
	MaintenanceUtil maintUtil;
	@Autowired
	private MaintenanceInterceptor maintInterceptor;
	@Autowired
	private UserInfo userInfo;	
	@Autowired
	private UserMessageService userMessageService;
	@Autowired
	private SolrUtil solrUtil;
	@Autowired
    private ApplicationEventPublisher eventPublisher;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private EmailReceiver emailReceiver;
	@Autowired
	private InvitationEmail invitationEmail; 
	@Autowired
	EncryptionUtil encryptUtil;

	/********************************/
	/*** User maintenance handler ***/
	/********************************/
	@MaintAware
	@RequestMapping(value = "/admin/users", method = RequestMethod.GET)
	public String userMaint(Model model) {
		logger.info("admin/users GET");
		
		List<Role> roles = roleService.getRoles();
		List<User> users = userService.getUsers();
		
		for (User u : users) {
			if (userSecurityService.isUserLoggedIn(u))
				u.setLoggedIn(true);
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
	public ResponseObject updateUser(@RequestBody User user, HttpSession session, Locale locale) throws RestException {
		logger.info("admin/updateUser POST: userId=" + user.getId());

		User originalUser = null;
		try {
			originalUser = userService.getUser(user.getId());
		} catch (Exception ex) {
			throw new RestException("exception.getUser", ex);
		}

		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (originalUser == null) {
			throw new RestException("exception.getUser", new AccessUserException());
		}

		Role originalRole = originalUser.getRole();
		if (originalUser.isAccountExpired() && !user.isAccountExpired()) {
			userService.setLastLogin(user);
		}
		
		if (originalUser.isPasswordExpired() && !user.isPasswordExpired()) {
			user.setPasswordExpiryDate();
		}
		
		//update the user
		try {
			//fixes issue with the profile object not containing the userId
			if (user.getUserProfile() != null)
				user.getUserProfile().setUser(user);
			if (originalRole.getId() != user.getRole().getId()) {
				Role newRole = roleService.getRole(user.getRole().getId());
				user.setRole(newRole);
			}				
			userService.updateUser(user);
		} catch (Exception ex) {
			throw new RestException("exception.updateUser", ex);
		}

		if (!originalRole.equals(user.getRole())) {
			if ((originalRole.isType(Role.TYPE_GUEST) &&
				((user.getRole().isType(Role.TYPE_AUTHOR))	||
				 (user.getRole().isType(Role.TYPE_EDITOR))	||
				 (user.getRole().isType(Role.TYPE_ADMIN))))	||	
				(originalRole.isType(Role.TYPE_AUTHOR) &&
				((user.getRole().isType(Role.TYPE_EDITOR))	||
				 (user.getRole().isType(Role.TYPE_ADMIN))))	||
				(originalRole.isType(Role.TYPE_EDITOR) && 
				 user.getRole().isType(Role.TYPE_ADMIN)))
			sendUpdateRoleMessage(user, originalRole, locale);
		}		
		
		//if the user no longer should have access then expire the user's session if present
		if ((originalUser.isEnabled() 			&& !user.isEnabled())		||
			(!originalUser.isLocked()			&& user.isLocked())			||
			(!originalUser.isAccountExpired()	&& user.isAccountExpired())	||
			(!originalUser.isPasswordExpired()	&& user.isPasswordExpired())) {
			userSecurityService.expireUserSession(user);
		}
		
		return new ResponseObject();
	}

	private void sendUpdateRoleMessage(User user, Role originalRole, Locale locale) {
		User admin = (User)userInfo.getUserDetails();
		UserMessage userMsg = new UserMessage();
		userMsg.setFromUserId(admin.getId());
		userMsg.setToUserId(user.getId());
		userMsg.setRecipeId(null);
		userMsg.setViewed(false);
		
		String subject = messages.getMessage("account.upgrade.title", null, "", locale);
		userMsg.setSubject(subject);
		
		String htmlMsg = "";
		String msg = messages.getMessage("common.congrats", null, "", locale);
		htmlMsg = "<h5><strong>" + msg + "</strong></h5>";
		Object[] obj = new String[2];
		obj[0] = messages.getMessage("roles." + user.getRole().getName(), null, "", locale);
		obj[1] = messages.getMessage("roles." + originalRole.getName(), null, "", locale);
		msg = messages.getMessage("account.upgrade.congrats", obj, "", locale);
		htmlMsg += "<p>" + msg + "</p>";
		
		if (user.isLoggedIn()) {
			msg = messages.getMessage("account.upgrade.loggedin", null, "", locale);
			htmlMsg += "<p>" + msg + "</p>";
		}
		
		msg = messages.getMessage("account.upgrade.thankyou", null, "", locale);
		htmlMsg += "<p>" + msg + "</p>";
		
		userMsg.setMessage(subject);
		userMsg.setHtmlMessage(htmlMsg);
		
		userMessageService.addMessage(userMsg, MessageType.ADMIN, locale);
	}

	/************************************/
	/*** Category maintenance handler ***/
	/************************************/
	//get list of categories
	@RequestMapping(value = "admin/getCategories", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public List<Category> getCategories(Locale locale) {
		logger.info("recipe/categories GET");
		
		return categoryService.listCategory();
	}

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

		//must re-add attribute(s) in case of an error
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", Category.class); 
		model.addAttribute("sizeMap", sizeMap);
		
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

		//must re-add attribute(s) in case of an error
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", Category.class); 
		model.addAttribute("sizeMap", sizeMap);
		
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
	public String getIngredientReviewList(Model model, Locale locale) {
		logger.info("admin/ingredients GET");

		List<IngredientReviewDto> ingredients = ingredientService.listNotReviewed(); 
		
    	for (IngredientReviewDto ingred : ingredients) {
    		Locale ingredLocale = new Locale(ingred.getLang());
    		String language = ingredLocale.getDisplayLanguage(locale); 
    		ingred.setDisplayLang(language);
    	}		
		
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
			ingredientService.setReviewed(ingredId, true);
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
		
		List<RecipeListDto> resultsList;
		Long id = ingredientReviewDto.getId();
		try {
			resultsList = solrUtil.searchIngredients(Long.toString(id));
		} catch (SolrServerException | IOException ex) {
			throw new RestException("exception.replaceIngredient", ex);
		}
		
		try {
			recipeIngredientService.replaceIngredient(ingredientReviewDto);
			ingredientService.deleteIngredient(ingredientReviewDto.getId());
		} catch (Exception ex) {
			throw new RestException("exception.replaceIngredient", ex);
		}
		
		for (RecipeListDto recipeDto : resultsList) {
			Recipe recipe = recipeService.getRecipe(recipeDto.getId());
			eventPublisher.publishEvent(new UpdateSolrRecipeEvent(recipe, true));
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
		RecipeMessageDto recipeMessageDto = new RecipeMessageDto();
		model.addAttribute("recipeMessageDto", recipeMessageDto);
		model.addAttribute("approvalActions", ApprovalAction.list());
		model.addAttribute("approvalReasons", ApprovalReason.list());
		return "admin/approveRecipes";
	}

	@RequestMapping(value="admin/approveRecipe", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)	
	public ResponseObject approveRecipe(@RequestBody RecipeMessageDto recipeMessageDto, Locale locale) throws RestException {
		logger.info("admin/approveRecipe POST: recipeId=" + recipeMessageDto.getRecipeId());
		
		if (recipeMessageDto.getAction() != ApprovalAction.DELETE) {
			//Recipe recipe;
			
			ApprovalAction action = recipeMessageDto.getAction();
			//assume most recipes will be approved
			ApprovalStatus status = ApprovalStatus.APPROVED;
			
			if (action == ApprovalAction.PENDING)
				status = ApprovalStatus.PENDING;
			else
			if (action == ApprovalAction.BLOCK)
				status = ApprovalStatus.BLOCKED;
		
			logger.info("admin/approveRecipe calling recipeService.approveRecipe"); 
			try {
				recipeService.approveRecipe(recipeMessageDto.getRecipeId(), status);
			} catch (Exception ex) {
				throw new RestException("exception.approveRecipe", ex);
			}
		}
		
		if (recipeMessageDto.getAction() == ApprovalAction.DELETE) {
			try {
				recipeService.deleteRecipe(recipeMessageDto.getRecipeId());
			} catch (Exception ex) {
				throw new RestException("exception.approveRecipe", ex);
			}
		}

		logger.info("admin/approveRecipe sending message");
		
		try {
			sendRecipeMessage(recipeMessageDto, locale);
		} catch (Exception ex) {
			throw new RestException("exception.approveRecipe", ex);
		}
		
		logger.info("admin/approveRecipe done");
		
		return new ResponseObject();
	}
	
	private void sendRecipeMessage(RecipeMessageDto recipeMessageDto, Locale locale) {
		User user = (User)userInfo.getUserDetails();
		
		UserMessage userMsg = new UserMessage();
		userMsg.setFromUserId(user.getId());
		userMsg.setToUserId(recipeMessageDto.getToUserId());
		if (recipeMessageDto.getAction() != ApprovalAction.DELETE)
			userMsg.setRecipeId(recipeMessageDto.getRecipeId());
		else {
			userMsg.setRecipeId(null);
			userMsg.setSubject(recipeMessageDto.getRecipeName());
		}
		userMsg.setViewed(false);
		
		String msg = "";
		String heading = "";
		String reasonMsg = "";
		ApprovalAction action = recipeMessageDto.getAction();
		
		heading = messages.getMessage("approvalmsg." + action.name().toLowerCase(), null, "", locale);
		reasonMsg = messages.getMessage("approvalmsg."  + action.name().toLowerCase() + ".reason", null, "", locale);
			
		msg = "<h5><strong>" + heading + "</strong></h5>";

		if (action == ApprovalAction.APPROVE)
			msg += "<p>" + reasonMsg + "</p>";
		
		String reasonList = "<ul>";
		ApprovalReason reasons[] = recipeMessageDto.getReasons();
		if (reasons != null) {
			msg += "<p>" + reasonMsg + "</p>";
			for (ApprovalReason reason : reasons) {
				reasonList += "<li>" + messages.getMessage("approvalmsg." + reason.name().toLowerCase(), null, "", locale) + "</li>";
			}
			reasonList += "</ul>";
			msg += reasonList;
		}

		String adminMsg = recipeMessageDto.getMessage();
		if (!StringUtils.isBlank(adminMsg)) {
			String notes = messages.getMessage("approvalmsg.editornotes", null, "", locale);
			msg += "<p><strong>" + notes + "</strong></p><p>" + adminMsg + "</p>";
		}
		
		userMsg.setMessage(heading);
		userMsg.setHtmlMessage(msg);
		
		userMessageService.addMessage(userMsg, MessageType.RECIPE, locale);
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
	public String postMaintenance(Model model, @ModelAttribute @Valid MaintenanceDto maintenanceDto, BindingResult result, HttpServletRequest request, Locale locale) {	
		logger.info("admin/maintenance POST");
		logger.debug("MaintenanceDto: " + maintenanceDto);
		
		//must re-add attribute(s) in case of an error
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

	/*******************************/
	/*** User invitation handler ***/
	/*******************************/
	@RequestMapping(value = "/admin/invitation", method = RequestMethod.GET)
	public String getInvitation(Model model) {
		logger.info("admin/invitation GET");

		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", InvitationDto.class); 
		model.addAttribute("sizeMap", sizeMap);
		
		return "admin/invitation";
	}

	//Note: this is an example of using validation with AJAX
	//Two aspects of normal validation do not appear to work:
	//	- the .jsp doesn't recognize the binding errors, because Spring attaches errors to the model/view which doesn't apply in this case
	//	- the i18n error messages must be inserted manually
	@RequestMapping(value="admin/sendInvitation", method = RequestMethod.POST)
	@ResponseBody
	public ResponseObject sendInvitation(@RequestBody @Valid InvitationDto invitationDto, BindingResult result, HttpServletResponse response, Locale locale) throws RestException {
		logger.info("admin/sendInvitation POST");

		boolean exists = false;
		if (!result.hasErrors()) {
			//double-check the email isn't in use in case user ignored the AJAX error
			try {
				exists = userService.doesUserEmailExist(invitationDto.getEmail());
			} catch (Exception ex) {
				//do nothing - if there is a problem with the database the user will be notifed when they submit the form
				logService.addException(ex);
			}
		}
		
		if (result.hasErrors() || exists) {
			logger.debug("Validation errors");
			
			ResponseObject obj = new ResponseObject("Validation errors", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			List<FieldError> fieldErrors = new ArrayList<FieldError>();
			
			if (exists) {
				logger.debug("Email exists error");
				String msg = messages.getMessage("user.duplicateEmail", null, "Duplicate email", locale);
				FieldError err = new FieldError("invitationDto","email", msg);
				fieldErrors.add(err);
			}
			else {
				List<FieldError> errors = result.getFieldErrors();
				for (FieldError error : errors) {
					String[] codes = error.getCodes();
					String defaultMsg = error.getDefaultMessage();
					String msg = messages.getMessage(codes[0], null, defaultMsg, locale);
					String field = error.getField();
					FieldError err = new FieldError("invitationDto", field, msg);
					fieldErrors.add(err);
				}
			}
			
			obj.setResult(fieldErrors);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			return obj;
		}

		User user = null;
		String password = messages.getMessage("invite.temppassword", null, "recipe", locale);
		
		UserDto userDto = new UserDto();
		userDto.setFirstName(invitationDto.getFirstName());
		userDto.setLastName(invitationDto.getLastName());
		userDto.setEmail(invitationDto.getEmail());
		userDto.setPassword(password);
		userDto.setSubmitRecipes(false);
		userDto.setInvited(true);
		
		logger.debug("adding user: " + invitationDto.getEmail());
		
		try {
			user = userService.addUser(userDto);
		} catch (Exception ex) {
			logService.addException(ex);
	        String msg = messages.getMessage("invite.usererror", null, "Create user failed", locale);
	        ResponseObject obj = new ResponseObject(msg, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        return obj;
		}

		logger.debug("sending email: " + invitationDto.getEmail());
		
        String newToken = UUID.randomUUID().toString();
    	userService.createUserVerificationToken(user, newToken);
        
    	String userName = user.getFirstName() + " " + user.getLastName();        
    	String confirmationUrl = "/confirmRegistration?token=" + newToken;
    	
    	EmailDetail emailDetail = new EmailDetail(userName, user.getEmail(), locale);
    	emailDetail.setTokenUrl(confirmationUrl);
        String idStr = String.valueOf(user.getId());
		String userIdStr = encryptUtil.encryptURLParam(idStr);
		String msgTypeStr = encryptUtil.encryptURLParam(OptOutType.ACCOUNT.name());
		String optoutUrl = "/user/optout?id=" + userIdStr + "&type=" + msgTypeStr;
		emailDetail.setOptoutUrl(optoutUrl);

        try {
        	invitationEmail.constructEmail(emailDetail);
        	emailSender.sendHtmlEmail(emailDetail);
		} catch (Exception ex) {
			logService.addException(ex);
	        String msg = messages.getMessage("invite.emailerror", null, "Send email failed", locale);
	        ResponseObject obj = new ResponseObject(msg, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        return obj;
		}
		
        String msg = messages.getMessage("invite.msgsent", null, "Email sent", locale);
        ResponseObject obj = new ResponseObject(msg, HttpServletResponse.SC_OK);
        response.setStatus(HttpServletResponse.SC_OK);
		return obj;
	}

	/*********************/
	/*** Email handler ***/
	/*********************/
	@MaintAware
	@RequestMapping(value = "/admin/emails", method = RequestMethod.GET)
	public String getEmails(Model model, HttpServletRequest request) {
		logger.info("admin/emails GET");

		String contextPath = request.getServletContext().getContextPath();
		logger.info("contextPath: " + contextPath);
		
		List<EmailDto> emails = emailReceiver.getMessages(contextPath);
		model.addAttribute("emails", emails);
		
		return "admin/emails";
	}
	
	@RequestMapping(value = "admin/attachment", method = RequestMethod.GET)
	public void getAttachment(@RequestParam("id") final long id, @RequestParam("filename") final String fileName, HttpServletResponse response) {
		logger.info("admin/attachment GET: filename=" + fileName);
		
		//errors are not fatal and will be logged by FileAction
		fileAction.downloadFile(FileType.EMAIL, id, fileName, response);
	}	
}