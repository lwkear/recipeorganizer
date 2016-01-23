package net.kear.recipeorganizer.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.CategoryService;
import net.kear.recipeorganizer.persistence.service.IngredientService;
import net.kear.recipeorganizer.persistence.service.RecipeIngredientService;
import net.kear.recipeorganizer.persistence.service.SourceService;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.util.FileActions;
import net.kear.recipeorganizer.util.FileTypes;
import net.kear.recipeorganizer.util.SolrUtil;
import net.kear.recipeorganizer.util.ViewReferer;

@Controller
public class RecipeController {
	
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
	private SourceService sourceService;
	@Autowired
	private UserService userService;
	@Autowired
	private MessageSource messages;
	@Autowired
	private FileActions fileAction;
	@Autowired
	private ViewReferer viewReferer;
	@Autowired
	private SolrUtil solrUtil;
	
	/***************************/
	/*** Edit recipe handler ***/
	/***************************/
	@RequestMapping("recipe/editRecipe/{id}")
	public String editRecipe(Model model, @RequestHeader("referer") String refer, @PathVariable Long id,
			HttpServletRequest request) {
		logger.info("recipe/editRecipe GET");

		Recipe recipe = recipeService.getRecipe(id);
		model.addAttribute("recipe", recipe);

		if (!refer.contains("view"))
			viewReferer.setReferer(refer, request);
		
		return "recipe/editRecipe";
	}

	@RequestMapping(value="recipe/editRecipe/{id}", method = RequestMethod.POST)
	public String updateRecipe(Model model, @ModelAttribute @Valid Recipe recipe, BindingResult result, BindingResult resultSource, Locale locale,
			@RequestParam(value = "file", required = false) MultipartFile file) {		
		logger.info("recipe/editRecipe POST save");
	
		if (result.hasErrors()) {
			logger.info("Validation errors");
			return "recipe/editRecipe";
		}

		if (file != null && !file.isEmpty()) {
			boolean rslt = fileAction.uploadFile(FileTypes.RECIPE, recipe.getId(), file);
			if (!rslt) {
				String msg = messages.getMessage("exception.file.IOException", null, locale);
				FieldError fieldError = new FieldError("recipe", "photoName", msg);
				result.addError(fieldError);
				return "recipe/editRecipe";
			}
			String currPhoto = recipe.getPhotoName();
			if (currPhoto != null && !currPhoto.isEmpty()) {
				String newPhoto = file.getOriginalFilename();
				if (!currPhoto.equalsIgnoreCase(newPhoto))
					fileAction.deleteFile(FileTypes.RECIPE, recipe.getId(), currPhoto);
			}
			recipe.setPhotoName(file.getOriginalFilename());
        }
		
		String photoName = recipe.getPhotoName();
		if (photoName.startsWith("xxxREMOVExxx")) {
			String name = photoName.substring(12);
			//errors are not fatal and will be logged by FileAction
			fileAction.deleteFile(FileTypes.RECIPE, recipe.getId(), name);
			recipe.setPhotoName("");
		}

		boolean newRecipe = recipe.getId() > 0 ? true : false;
		recipeService.saveRecipe(recipe);
		
		//errors are not fatal and will be logged by SolrUtil
		if (!newRecipe)
			solrUtil.deleteRecipe(recipe.getId());
		solrUtil.addRecipe(recipe);
		
		return "redirect:/recipe/viewRecipe/" + recipe.getId();
	}
	
	/****************************/
	/*** Navigation from view ***/
	/****************************/
	public String getReturnMessage(String referer) {

		String returnLabel = null;
		
		if (referer.contains("searchResults"))
			returnLabel = "title.searchresults";
		else
		if (referer.contains("listRecipes"))
			returnLabel = "menu.submittedrecipes";
		else
		if (referer.contains("favorites"))
			returnLabel = "menu.favorites";
		else
		if (referer.contains("dashboard"))
			returnLabel = "dashboard.head";
		else
			returnLabel = "";

		return returnLabel;
	}

	/*****************************/
	/*** Delete recipe handler ***/
	/*****************************/
	@RequestMapping(value="recipe/deleteRecipe")
	@ResponseBody
	public String deleteRecipe(@RequestParam("recipeId") Long recipeId, HttpServletResponse response) {
		logger.info("recipe/deleteRecipe");
		logger.info("recipeId=" + recipeId);
		
		//set default response
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		recipeService.deleteRecipe(recipeId);
		
		String fileName = fileAction.fileExists(FileTypes.RECIPE, recipeId);
		if (fileName.length() > 0)
			//errors are not fatal and will be logged by FileAction
			fileAction.deleteFile(FileTypes.RECIPE, recipeId, fileName);
		
		//errors are not fatal and will be logged by SolrUtil
		solrUtil.deleteRecipe(recipeId);

		return msg;
	}	

	/**************************/
	/*** Support functions  ***/
	/**************************/
	//JSON request for a list of categories
	@RequestMapping("recipe/getCategories")
	@ResponseBody
	public List<Category> getCategories() {
		logger.info("recipe/categories GET");
		
		return categoryService.listCategory();
	}

	//AJAX post a new ingredient
	@RequestMapping(value="recipe/addIngredient", method = RequestMethod.POST)
	@ResponseBody
	public Ingredient addIngredient(@RequestBody @Valid Ingredient ingredient) {
		logger.info("recipe/addingredient POST");
		
		logger.info("Id = " + ingredient.getId());
		logger.info("Description = " + ingredient.getName());
		
		if (ingredient.getName() == null)
			return ingredient;
		
		//add the ingredient to the DB
		ingredientService.addIngredient(ingredient);
		
		logger.info("Id = " + ingredient.getId());
		logger.info("Description = " + ingredient.getName());
		
		return ingredient;
	}
	
	//AJAX/JSON request for a typeahead list of ingredients
	@RequestMapping("recipe/getIngredients")
	@ResponseBody
	public List<Ingredient> getIngredients(@RequestParam("searchStr") String searchStr) {
		logger.info("recipe/getingredients GET");
		logger.info("searchStr = " + searchStr); 
		
		List<Ingredient> ingreds = ingredientService.getIngredients(searchStr);
		
		return ingreds;
	}

	//AJAX/JSON request for a typeahead list of ingredient qualifiers
	@RequestMapping("recipe/getQualifiers")
	@ResponseBody
	public List<String> getQualifiers(@RequestParam("searchStr") String searchStr) {
		logger.info("recipe/getqualifiers GET");
		logger.info("searchStr = " + searchStr); 
		
		List<String> quals = recipeIngredientService.getQualifiers(searchStr);
		
		return quals;
	}

	//AJAX/JSON request for a typeahead list of sources
	@RequestMapping("recipe/getSources")
	@ResponseBody
	public List<String> getSources(@RequestParam("searchStr") String searchStr, @RequestParam("type") String type) {
		logger.info("recipe/getsources GET");
		logger.info("searchStr=" + searchStr + "; type=" + type); 
		
		List<String> sources = sourceService.getSources(searchStr, type);
		
		return sources;
	}
	
	//AJAX/JSON request for a typeahead list of tags
	@RequestMapping("recipe/getTags")
	@ResponseBody
	public List<String> getTags(@RequestParam("searchStr") String searchStr, @RequestParam("userId") Long userId) {
		logger.info("recipe/gettags GET");
		logger.info("searchStr=" + searchStr + "; userId=" + userId); 
		
		List<String> tags = recipeService.getTags(searchStr, userId);
		
		return tags;
	}

	//AJAX/JSON request for checking name duplication
	@RequestMapping(value="recipe/lookupRecipeName", produces="text/javascript")
	@ResponseBody 
	public String lookupRecipeName(@RequestParam("name") String lookupName, @RequestParam("userId") Long userId, HttpServletResponse response) {
		
		logger.info("recipe/lookupRecipeName");
		logger.info("recipeName=" + lookupName + "; userId=" + userId);
		
		//set default response, incl. empty JSON msg
		String msg = "{}";
		response.setStatus(HttpServletResponse.SC_OK);
		
		//add the ingredient to the DB
		boolean result = recipeService.lookupName(lookupName, userId);
		
		logger.info("lookupName result=" + result);
		
		//name was found
		if (result) {
			Locale locale = LocaleContextHolder.getLocale();
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			msg = messages.getMessage("recipe.name.duplicate", null, "Duplicate name", locale);
		}

		return msg;
	}

	@RequestMapping(value="recipe/getRecipeCount")
	@ResponseBody 
	public Long getRecipeCount(@RequestParam("userId") Long userId, HttpServletResponse response) {
		
		logger.info("recipe/getRecipeCount");
		logger.info("userId=" + userId);
		
		//set default response
		response.setStatus(HttpServletResponse.SC_OK);
		
		//get the count for the user
		Long count = recipeService.getRecipeCount(userId);
		
		logger.info("count result=" + count);
		
		return count;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String handleMethodArgumentNotValid(HttpServletRequest req, MethodArgumentNotValidException ex) {
		logger.info("MethodArgumentNotValidException exception!");

		BindingResult result = ex.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();
		
		Locale locale = LocaleContextHolder.getLocale();
		String errorCode = "";
		String defaultMsg = "Unknown error";
		String errorMsg = "";
		
		if (fieldErrors.size() > 0) {
			FieldError fieldErr = fieldErrors.get(0);
			defaultMsg = fieldErr.getDefaultMessage();
			errorCode = fieldErr.getCode() + "." + fieldErr.getObjectName() + "." + fieldErr.getField();
			errorMsg = messages.getMessage(errorCode, null, defaultMsg, locale);
		}

		logger.info("errorCode: " + errorCode);
		logger.info("errorMsg: " + errorMsg);
		
		return errorMsg;
	}
}