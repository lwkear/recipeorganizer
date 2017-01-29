package net.kear.recipeorganizer.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.kear.recipeorganizer.enums.FileType;
import net.kear.recipeorganizer.enums.OptOutType;
import net.kear.recipeorganizer.exception.AccessUserException;
import net.kear.recipeorganizer.exception.RecipeNotFound;
import net.kear.recipeorganizer.exception.RestException;
import net.kear.recipeorganizer.persistence.dto.CategoryDto;
import net.kear.recipeorganizer.persistence.dto.CommentDto;
import net.kear.recipeorganizer.persistence.dto.RecipeDisplayDto;
import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.dto.ShareRecipeDto;
import net.kear.recipeorganizer.persistence.model.Favorites;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeComment;
import net.kear.recipeorganizer.persistence.model.RecipeMade;
import net.kear.recipeorganizer.persistence.model.RecipeNote;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.model.UserProfile;
import net.kear.recipeorganizer.persistence.model.Viewed;
import net.kear.recipeorganizer.persistence.model.ViewedKey;
import net.kear.recipeorganizer.persistence.service.CategoryService;
import net.kear.recipeorganizer.persistence.service.CommentService;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.report.ReportGenerator;
import net.kear.recipeorganizer.util.CookieUtil;
import net.kear.recipeorganizer.util.EncryptionUtil;
import net.kear.recipeorganizer.util.ResponseObject;
import net.kear.recipeorganizer.util.UserInfo;
import net.kear.recipeorganizer.util.db.ConstraintMap;
import net.kear.recipeorganizer.util.email.EmailDetail;
import net.kear.recipeorganizer.util.email.EmailSender;
import net.kear.recipeorganizer.util.email.ShareRecipeEmail;
import net.kear.recipeorganizer.util.file.FileActions;
import net.kear.recipeorganizer.util.maint.MaintAware;
import net.kear.recipeorganizer.util.view.ViewReferer;

@Controller
public class DisplayController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UserService userService;
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private UserInfo userInfo;
	@Autowired
	private MessageSource messages;
	@Autowired
	private FileActions fileAction;
	@Autowired
	private CookieUtil cookieUtil;
	@Autowired
	private ViewReferer viewReferer;
	@Autowired
	private ExceptionLogService logService;
	@Autowired
	private ConstraintMap constraintMap;
	@Autowired
	private ReportGenerator reportGenerator; 
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private ShareRecipeEmail shareRecipeEmail;
	@Autowired
	EncryptionUtil encryptUtil;
	
	/****************************/
	/*** List recipes handler ***/
	/****************************/
	@MaintAware
	@RequestMapping(value = "recipe/recipeList", method = RequestMethod.GET)
	public String listRecipes(ModelMap model, Locale locale) {
		logger.info("recipe/listRecipes GET");
	
		String title = messages.getMessage("menu.submittedrecipes", null, "Submitted Recipes", locale);
		
		User user = (User)userInfo.getUserDetails();
		List<RecipeListDto> recipes = recipeService.listRecipes(user.getId());
		model.addAttribute("title", title);
		model.addAttribute("fav", false);
		model.addAttribute("recipes", recipes);

		return "recipe/recipeList";
	}

	@MaintAware
	@RequestMapping(value = "recipe/favorites", method = RequestMethod.GET)
	public String favoriteRecipes(ModelMap model, Locale locale) {
		logger.info("recipe/favoriteRecipes GET");
		
		String title = messages.getMessage("menu.favorites", null, "Favorites", locale);

		User user = (User)userInfo.getUserDetails();
		List<RecipeListDto> recipes = recipeService.favoriteRecipes(user.getId());
		model.addAttribute("userId", user.getId());
		model.addAttribute("title", title);
		model.addAttribute("fav", true);
		model.addAttribute("recipes", recipes);

		return "recipe/recipeList";
	}

	@MaintAware
	@RequestMapping(value = "recipe/browseRecipes", method = RequestMethod.GET)
	public String browseRecipes(ModelMap model, Locale locale) {
		logger.info("recipe/browseRecipes GET");
		
		long defaultCatId = 7L;
		List<CategoryDto> catList = categoryService.listCategoryDto(locale);
		
		model.addAttribute("defaultCatId", defaultCatId);
		model.addAttribute("catList", catList);
		
		return "recipe/browseRecipes";
	}
	
	@RequestMapping(value = "recipe/categoryRecipes", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public List<RecipeDisplayDto> getCategoryRecipes(@RequestParam("categoryId") Long categoryId) throws RestException {
		logger.info("categoryRecipes/category GET: categoryId=" + categoryId);

		List<RecipeDisplayDto> recipeList = recipeService.categoryRecipes(categoryId);
		
		return recipeList;
	}
	
	/***************************/
	/*** View recipe handler ***/
	/***************************/
	@MaintAware
	@RequestMapping(value = "recipe/viewRecipe/{recipeId}", method = RequestMethod.GET)
	public String viewRecipe(ModelMap model, @RequestHeader(value="referer", required=false) String refer, @PathVariable Long recipeId, 
			HttpServletResponse response, HttpServletRequest request, Locale locale) throws RecipeNotFound {
		logger.info("recipe/viewRecipe GET: recipeId=" + recipeId);

		User user = (User)userInfo.getUserDetails();
		Recipe recipe = null;
		
		try {
			recipe = recipeService.getRecipe(recipeId);
		}
		catch (Exception ex) {
			throw new RecipeNotFound(ex);
		}

		UserProfile profile = recipe.getUser().getUserProfile();
		
		if (user != null) {
			ViewedKey key = new ViewedKey(user.getId(), recipe.getId());
			try {
				Viewed viewed = recipeService.getViewed(key);
				if (viewed == null) {
					viewed = new Viewed();
					viewed.setId(key);
					recipeService.addViewed(viewed);
				}
				else {
					viewed.setDateViewed(null);
					recipeService.updateViewed(viewed);
				}
			} catch (Exception ex) {
				//do nothing - not a fatal error
			}
		}
		
		if (refer != null && !refer.contains("edit")) {
			viewReferer.setReferer(refer, request);
		}
		
		boolean fav = recipeService.isFavorite(user.getId(), recipeId);
		RecipeMade recipeMade = recipeService.getRecipeMade(user.getId(), recipeId);
		RecipeNote recipeNote = recipeService.getRecipeNote(user.getId(), recipeId);
		long commentCount = commentService.getCommentCount(recipeId);
		List<CommentDto> commentList = commentService.listComments(recipeId);
		Map<String, Object> sizeMap = constraintMap.getModelConstraint("Size", "max", RecipeComment.class); 

		String jsonNote = null;
		if (recipeNote != null) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				jsonNote = mapper.writeValueAsString(recipeNote);
			} catch (JsonProcessingException ex) {
				logService.addException(ex);
			}
		}
		
		ShareRecipeDto shareDto = new ShareRecipeDto();
		
		model.addAttribute("madeCount", recipeMade.getMadeCount());
		model.addAttribute("lastMade", recipeMade.getLastMade());
		model.addAttribute("recipeNote", recipeNote);
		model.addAttribute("jsonNote", jsonNote);
		model.addAttribute("favorite", fav);
		model.addAttribute("commentCount", commentCount);
		model.addAttribute("commentList", commentList);
		model.addAttribute("sizeMap", sizeMap);
		model.addAttribute("recipe", recipe);
		model.addAttribute("submitJoin", recipe.getUser().getDateAdded());
		model.addAttribute("profile", profile);
		model.addAttribute("recipeShareDto", shareDto);
		model.addAttribute("allowEmail",user.isEmailRecipe());
		
		if (user.getId() != recipe.getUser().getId())
			recipeService.addView(recipe);
		if (refer != null && !refer.contains("edit"))
			viewReferer.setReferer(refer, request);

		return "recipe/viewRecipe";
	}
	
	@RequestMapping(value = "recipe/photo", method = RequestMethod.GET)
	public void getPhoto(@RequestParam("id") final long id, @RequestParam("filename") final String fileName, HttpServletResponse response) {
		logger.info("recipe/photo GET: fileName=" + fileName);

		if (!StringUtils.isBlank(fileName))
			//errors are not fatal and will be logged by FileAction
			fileAction.downloadFile(FileType.RECIPE, id, fileName, response);
	}

	@RequestMapping(value = "/report/getHtmlRpt", method = RequestMethod.GET)
	public void getHtmlRpt(@RequestParam("uid") final Long uid, @RequestParam("rid") final Long rid, HttpServletResponse response, Locale locale) {

		logger.info("recipe/getHtmlRpt GET: userId=" + uid + " recipeId=" + rid);
	
		reportGenerator.createRecipeHtml(uid, rid, response, locale);
	}

	/*************************/
	/*** Favorites handler ***/
	/*************************/
	@RequestMapping(value = "/recipe/addFavorite", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject addFavorite(@RequestBody Favorites favorite) throws RestException {
		logger.info("recipe/addFavorite POST: user/recipe=" + favorite.getId().getUserId() + "/" + favorite.getId().getRecipeId());
		
		try {
			recipeService.addFavorite(favorite);
		} catch (Exception ex) {
			throw new RestException("exception.addFavorite", ex);
		}
		
		return new ResponseObject();
	}
	
	@RequestMapping(value = "/recipe/removeFavorite", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject removeFavorite(@RequestBody Favorites favorite) throws RestException {
		logger.info("recipe/removeFavorite POST: user/recipe=" + favorite.getId().getUserId() + "/" + favorite.getId().getRecipeId());
		
		try {
			recipeService.removeFavorite(favorite);
		} catch (DataAccessException ex) {
			throw new RestException("exception.removeFavorite", ex);
		}
		
		return new ResponseObject();
	}

	/************************/
	/*** LastMade handler ***/
	/************************/
	@RequestMapping(value = "/recipe/recipeMade", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject updateRecipeMade(@RequestBody RecipeMade recipeMade) throws RestException {
		logger.info("recipe/recipeMade POST: user/recipe=" + recipeMade.getId().getUserId() + "/" + recipeMade.getId().getRecipeId());
		
		try {
			recipeService.updateRecipeMade(recipeMade);
		} catch (DataAccessException ex) {
			throw new RestException("exception.recipeMade", ex);
		}
		
		return new ResponseObject();
	}

	/**************************/
	/*** RecipeNote handler ***/
	/**************************/
	@RequestMapping(value = "/recipe/recipeNote", method = RequestMethod.POST)
	@ResponseStatus(value=HttpStatus.OK)
	public String updateRecipeNote(Model model, @RequestBody RecipeNote recipeNote, HttpServletResponse response) throws RestException {
		logger.info("recipe/recipeNote POST: user/recipe=" + recipeNote.getId().getUserId() + "/" + recipeNote.getId().getRecipeId());
		
		try {
			recipeService.updateRecipeNote(recipeNote);
		} catch (DataAccessException ex) {
			throw new RestException("exception.recipeNote", ex);
		}

		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		model.addAttribute("recipeNote", recipeNote);
		
		return "recipe/privateNotes";
	}
	
	/*****************************/
	/*** RecipeComment handler ***/
	/*****************************/
	//NOTE: do NOT add @ResponseBody to this method since that will return a string instead of HTML
	@RequestMapping(value = "/recipe/recipeComment", method = RequestMethod.POST)
	@ResponseStatus(value=HttpStatus.OK)
	public String addRecipeComment(Model model, @RequestBody RecipeComment recipeComment, HttpServletResponse response) throws RestException {
		logger.info("recipe/recipeComment POST: user/recipe=" + recipeComment.getUserId() + "/" + recipeComment.getRecipeId());
		
		try {
			commentService.addComment(recipeComment);
		} catch (Exception ex) {
			throw new RestException("exception.recipeComment", ex);
		}

		long userId = recipeComment.getUserId();
		long recipeId = recipeComment.getRecipeId();
		long commentCount = commentService.getCommentCount(recipeId);
		List<CommentDto> commentList = commentService.listComments(recipeId);
	
		Recipe recipe = new Recipe();
		recipe.setId(recipeId);
		
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		model.addAttribute("commentCount", commentCount);
		model.addAttribute("commentList", commentList);
		model.addAttribute("viewerId", userId);
		model.addAttribute("recipe", recipe);
		
		return "recipe/comments";
	}

	@RequestMapping(value = "/recipe/flagComment", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject flagComment(@RequestParam("commentId") Long commentId) throws RestException {
		logger.info("recipe/flagComment POST: commentId=" + commentId);
		
		try {
			commentService.setCommentFlag(commentId, 1);
		} catch (DataAccessException ex) {
			throw new RestException("exception.flagComment", ex);
		}
		
		return new ResponseObject();
	}

	/***************************/
	/*** ShareRecipe handler ***/
	/***************************/
	//Note: this is an example of using validation with AJAX in a Bootstrap modal dialog
	//Two aspects of normal validation do not appear to work:
	//	- the .jsp doesn't recognize the binding errors, because Spring attaches errors to the model/view which doesn't apply in this case
	//	- the i18n error messages must be inserted manually
	@RequestMapping(value = "/recipe/shareRecipe", method = RequestMethod.POST)
	@ResponseBody
	public ResponseObject shareRecipe(@RequestBody @Valid ShareRecipeDto shareRecipeDto, BindingResult result, HttpServletResponse response, Locale locale) throws RestException {
		logger.info("recipe/shareRecipe POST: user/recipe=" + shareRecipeDto.getUserId() + "/" + shareRecipeDto.getRecipeId());
		
		if (result.hasErrors()) {
			logger.debug("Validation errors");
			//need to translate the errors - validation doesn't pick them up automatically in this instance
			List<FieldError> errors = result.getFieldErrors();
			List<FieldError> fieldErrors = new ArrayList<FieldError>();
			for (FieldError error : errors) {
				String[] codes = error.getCodes();
				String defaultMsg = error.getDefaultMessage();
				String msg = messages.getMessage(codes[0], null, defaultMsg, locale);
				String field = error.getField();
				FieldError err = new FieldError("shareRecipeDto", field, msg);
				fieldErrors.add(err);
			}			
			
			ResponseObject obj = new ResponseObject();
			obj.setResult(fieldErrors);
			obj.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			return obj;
		}
		
		User user = null;
		try {
			user = userService.getUser(shareRecipeDto.getUserId());
		} 
		catch (Exception ex) {
			throw new RestException("exception.getUser", ex);
		}
		
		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (user == null) {
			throw new RestException("exception.getUser", new AccessUserException());
		}
		
		User recipient = null;
		String recipientName = "";
		String recipientEmail = "";
		String optoutUrl = "";
		boolean recipientIsUser = false;
		if (shareRecipeDto.getRecipientId() != 0) {
			try {
				recipient = userService.getUser(shareRecipeDto.getRecipientId());
			} 
			catch (Exception ex) {
				throw new RestException("exception.shareRecipeUser", ex);
			}

			//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
			if (recipient == null) {
				throw new RestException("exception.getUser", new AccessUserException());
			}
			
			recipientIsUser = true;
			recipientName = recipient.getFirstName() + " " + recipient.getLastName();
			recipientEmail = recipient.getEmail();
			String idStr = String.valueOf(recipient.getId());
			String recipIdStr = encryptUtil.encryptURLParam(idStr);
			String msgTypeStr = encryptUtil.encryptURLParam(OptOutType.RECIPE.name());
			optoutUrl = "/user/optout?id=" + recipIdStr + "&type=" + msgTypeStr;
		}
		else {
			recipientName = shareRecipeDto.getRecipientName();
			recipientEmail = shareRecipeDto.getRecipientEmail(); 
		}
		
		if (recipientIsUser) {
			if (!recipient.isEmailRecipe()) {
				String msg = messages.getMessage("email.recipe.noemail", null, "Unable to send recipe.", locale);
				ResponseObject obj = new ResponseObject(msg, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setCharacterEncoding(StandardCharsets.UTF_8.name());
				return obj;
			}
		}
		
    	String pdfFileName = "";
    	
    	try {
    		pdfFileName = reportGenerator.createRecipePDF(shareRecipeDto.getRecipeId(), locale);
    	} 
		catch (Exception ex) {
    		throw new RestException("exception.generatePDF", ex);
    	}
    	
    	String userName = user.getFirstName() + " " + user.getLastName();
    	
    	EmailDetail emailDetail = new EmailDetail(recipientName, recipientEmail, locale);
		emailDetail.setUserName(userName);
		emailDetail.setUserFirstName(user.getFirstName());
		emailDetail.setUserMessage(shareRecipeDto.getEmailMsg());
		emailDetail.setRecipeName(shareRecipeDto.getRecipeName());
		emailDetail.setPdfAttached(true);
		emailDetail.setPdfFileName(pdfFileName);
		emailDetail.setOptoutUrl(optoutUrl);
	
    	try {
    		shareRecipeEmail.constructEmail(emailDetail);
			emailSender.sendHtmlEmail(emailDetail);
		} catch (Exception ex) {
			throw new RestException("exception.sendEmail", ex);
		}

    	response.setStatus(HttpServletResponse.SC_OK);
		return new ResponseObject();
	}
}
