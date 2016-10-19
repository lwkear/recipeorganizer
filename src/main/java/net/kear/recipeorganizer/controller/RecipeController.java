package net.kear.recipeorganizer.controller;

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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
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

import net.kear.recipeorganizer.enums.FileType;
import net.kear.recipeorganizer.enums.SourceType;
import net.kear.recipeorganizer.exception.RecipeNotFound;
import net.kear.recipeorganizer.exception.RestException;
import net.kear.recipeorganizer.persistence.dto.CategoryDto;
import net.kear.recipeorganizer.persistence.dto.SourceTypeDto;
import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.Recipe.RecipeAllGroup;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.CategoryService;
import net.kear.recipeorganizer.persistence.service.IngredientService;
import net.kear.recipeorganizer.persistence.service.RecipeIngredientService;
import net.kear.recipeorganizer.persistence.service.SourceService;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.util.ResponseObject;
import net.kear.recipeorganizer.util.db.ConstraintMap;
import net.kear.recipeorganizer.util.file.FileActions;
import net.kear.recipeorganizer.util.file.FileConstant;
import net.kear.recipeorganizer.util.file.FileResult;
import net.kear.recipeorganizer.util.maint.MaintAware;
import net.kear.recipeorganizer.util.view.ViewReferer;

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
	private ConstraintMap constraintMap;
	
	/***************************/
	/*** Edit recipe handler ***/
	/***************************/
	@MaintAware
	@RequestMapping(value = "recipe/editRecipe/{recipeId}", method = RequestMethod.GET)
	public String editRecipe(Model model, @RequestHeader("referer") String refer, @PathVariable Long recipeId, HttpServletRequest request, Locale locale) throws RecipeNotFound {
		logger.info("recipe/editRecipe GET: recipeId=" + recipeId);

		Recipe recipe;
		try {
			recipe = recipeService.getRecipe(recipeId);
		}
		catch (Exception ex) {
			throw new RecipeNotFound(ex);
		}
		
		Map<String, Object> sizeMap = recipeService.getConstraintMap("Size", "max");
		model.addAttribute("sizeMap", sizeMap);

		List<String> tags = recipe.getTags();
		List<String> strTags = new ArrayList<String>();
		if (tags.size() > 0) {
			for (String tag : tags) {
				String str = '"' + tag + '"';
				strTags.add(str);
			}
		}
		recipe.setTags(strTags);		
		model.addAttribute("recipe", recipe);
		
		List<SourceTypeDto> typeList = sourceService.getSourceTypes(locale);
    	model.addAttribute("typeList", typeList);

		if (refer != null && !refer.contains("view"))
			viewReferer.setReferer(refer, request);
		
		return "recipe/editRecipe";
	}

	//Note: user Validated instead of Valid to include custom constraint validation, e.g., QuantityValidator
	@RequestMapping(value="recipe/editRecipe/{id}", method = RequestMethod.POST)
	public String updateRecipe(Model model, @ModelAttribute @Validated(RecipeAllGroup.class) Recipe recipe, BindingResult result, Locale locale, HttpServletRequest request,
			@RequestParam(value = "file", required = false) MultipartFile file) {		
		logger.info("recipe/editRecipe POST: recipeId=" + recipe.getId());
	
		//must re-add attribute(s) in case of an error
		Map<String, Object> sizeMap = recipeService.getConstraintMap("Size", "max");
		model.addAttribute("sizeMap", sizeMap);

		List<SourceTypeDto> typeList = sourceService.getSourceTypes(locale);
    	model.addAttribute("typeList", typeList);
		
		if (result.hasErrors()) {
			logger.debug("Validation errors");			
			return "recipe/editRecipe";
		}

		if (file != null) {
			FileResult rslt = fileAction.uploadFile(FileType.RECIPE, recipe.getId(), file);
			if (rslt == FileResult.SUCCESS) {
				String currPhoto = recipe.getPhotoName();
				if (!StringUtils.isBlank(currPhoto)) {
					String newPhoto = file.getOriginalFilename();
					if (!currPhoto.equals(newPhoto))
						fileAction.deleteFile(FileType.RECIPE, recipe.getId(), currPhoto);
				}
				recipe.setPhotoName(file.getOriginalFilename());
			}
			else {
				if (rslt != FileResult.NO_FILE) {
					String msg = fileAction.getErrorMessage(rslt, locale);
					FieldError fieldError = new FieldError("recipe", "photoName", msg);
					result.addError(fieldError);
					return "recipe/editRecipe";
				}
			}
        }
		
		String photoName = recipe.getPhotoName();
		if (photoName != null && photoName.startsWith(FileConstant.REMOVE_PHOTO_PREFIX)) {
			String name = photoName.substring(12);
			//errors are not fatal and will be logged by FileAction
			fileAction.deleteFile(FileType.RECIPE, recipe.getId(), name);
			recipe.setPhotoName("");
		}

		recipeService.saveRecipe(recipe);
		
		String uri = (String) request.getSession().getAttribute("returnUrl");
		if (uri != null && uri.contains("approval"))
			return "redirect:/admin/approval";
		
		return "redirect:/recipe/done/" + recipe.getId();
	}

	@RequestMapping(value = "recipe/done/{recipeId}", method = RequestMethod.GET)
	public String getDone(Model model, @PathVariable Long recipeId, HttpServletRequest request, Locale locale) throws RecipeNotFound {
		logger.info("recipe/done GET: recipeId=" + recipeId);
	
		Recipe recipe;
		try {
			recipe = recipeService.getRecipe(recipeId);
		}
		catch (Exception ex) {
			throw new RecipeNotFound(ex);
		}
		
		if (recipe == null) {
			throw new RecipeNotFound();
		}
		
		model.addAttribute("update", true);
		model.addAttribute("recipe", recipe);
		return "recipe/done";
	}
	
	/*****************************/
	/*** Delete recipe handler ***/
	/*****************************/
	@RequestMapping(value = "recipe/deleteRecipe", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResponseObject deleteRecipe(@RequestParam("recipeId") Long recipeId) throws RestException {
		logger.info("recipe/deleteRecipe GET: recipeId=" + recipeId);
		
		//delete the recipe
		try {
			recipeService.deleteRecipe(recipeId);
		} catch (Exception ex) {
			throw new RestException("exception.deleteRecipe", ex);
		}

		String fileName = fileAction.fileExists(FileType.RECIPE, recipeId);
		if (fileName.length() > 0)
			//errors are not fatal and will be logged by FileAction
			fileAction.deleteFile(FileType.RECIPE, recipeId, fileName);
		
		return new ResponseObject();
	}	

	/**************************/
	/*** Support functions  ***/
	/**************************/
	//get list of categories
	@RequestMapping(value = "recipe/getCategories", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public List<CategoryDto> getCategories(Locale locale) {
		logger.info("recipe/categories GET");
		
		List<CategoryDto> catList = categoryService.listCategoryDto(locale); 
		return catList;
	}
	
	//get list of source types
	@RequestMapping(value = "recipe/getSourceTypes", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public List<SourceTypeDto> getSourceTypes(Locale locale) {
		logger.info("recipe/sourceTypes GET");
		
		List<SourceTypeDto> typeList = sourceService.getSourceTypes(locale); 		
		return typeList;
	}
	
	//post a new ingredient
	@RequestMapping(value = "recipe/addIngredient", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public Ingredient addIngredient(@RequestBody @Valid Ingredient ingredient, Locale locale) throws RestException {
		logger.info("recipe/addingredient POST: name=" + ingredient.getName());
		
		if (ingredient.getName() == null)
			return ingredient;
		
		ingredient.setLang(locale.getLanguage());
		
		//add the ingredient to the DB
		try {
			ingredientService.addIngredient(ingredient);
		} catch (Exception ex) {
			throw new RestException("exception.addIngredient", ex);
		}
		
		logger.debug("Id = " + ingredient.getId());
		logger.debug("Description = " + ingredient.getName());
		
		return ingredient;
	}
	
	//request for a typeahead list of ingredients
	@RequestMapping(value = "recipe/getIngredients", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public List<Ingredient> getIngredients(@RequestParam("searchStr") String searchStr, Locale locale) {
		logger.info("recipe/getingredients GET");
		logger.debug("searchStr=" + searchStr + " language=" + locale.getLanguage()); 

		List<Ingredient> ingreds = ingredientService.getIngredients(searchStr, locale.getLanguage());
		
		return ingreds;
	}

	//request for a typeahead list of ingredient qualifiers
	@RequestMapping(value = "recipe/getQualifiers", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public List<String> getQualifiers(@RequestParam("searchStr") String searchStr) {
		logger.info("recipe/getqualifiers GET");
		logger.debug("searchStr = " + searchStr); 
		
		List<String> quals = recipeIngredientService.getQualifiers(searchStr);
		
		return quals;
	}

	//request for a typeahead list of sources
	@RequestMapping(value = "recipe/getSources", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public List<String> getSources(@RequestParam("searchStr") String searchStr, @RequestParam("type") String type) {
		logger.info("recipe/getsources GET");
		logger.debug("searchStr=" + searchStr + "; type=" + type); 
		
		SourceType sourceType = SourceType.valueOf(type);
		List<String> sources = sourceService.getSources(searchStr, sourceType);
		
		return sources;
	}
	
	//request for a typeahead list of tags
	@RequestMapping(value = "recipe/getTags", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public List<String> getTags(@RequestParam("userId") Long userId) {
		logger.info("recipe/gettags GET");
		logger.debug("userId=" + userId); 
		
		List<String> tags = recipeService.getTags(userId);
		return tags;
	}

	//request for checking name duplication
	@RequestMapping(value = "recipe/lookupRecipeName", method = RequestMethod.GET)
	@ResponseBody 
	public ResponseObject lookupRecipeName(@RequestParam("name") String lookupName, @RequestParam("userId") Long userId, HttpServletResponse response, Locale locale) 
			throws RestException {
		logger.info("recipe/lookupRecipeName GET: name/userid=" + lookupName + "/" + userId);
		
		//query the DB for the recipe name
		boolean result = false;
		try {
			result = recipeService.lookupName(lookupName, userId);
		} catch (Exception ex) {
			throw new RestException("exception.restDefault", ex);
		}

		logger.debug("lookupName result=" + result);

		ResponseObject obj = new ResponseObject();
		response.setStatus(HttpServletResponse.SC_OK);
		
		//name was found
		if (result) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			obj.setStatus(HttpServletResponse.SC_CONFLICT);
			String msg = messages.getMessage("recipe.name.duplicate", null, "Duplicate name", locale);
			obj.setMsg(msg);
		}

		return obj;
	}

	@RequestMapping(value = "recipe/userRecipeCount", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)	
	public Long getUserRecipeCount(@RequestParam("userId") Long userId, HttpServletResponse response) throws RestException {
		logger.info("recipe/getRecipeCount GET: user=" + userId);
		
		Long count = 0L;
		
		//get the count for the user
		try {
			count = recipeService.getRecipeCount(userId);
		} catch (Exception ex) {
			throw new RestException("exception.getRecipe", ex);
		}
		
		logger.debug("count result=" + count);
		
		return count;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String handleMethodArgumentNotValid(HttpServletRequest req, MethodArgumentNotValidException ex) {
		logger.debug("MethodArgumentNotValidException exception");

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

		logger.debug("errorCode: " + errorCode);
		logger.debug("errorMsg: " + errorMsg);
		
		return errorMsg;
	}
}
