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
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.CategoryService;
import net.kear.recipeorganizer.persistence.service.IngredientService;
import net.kear.recipeorganizer.persistence.service.RecipeIngredientService;
import net.kear.recipeorganizer.persistence.service.SourceService;
import net.kear.recipeorganizer.persistence.service.UserService;

@Controller
public class SaveRecipeController {
	
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
	
	/**************************/
	/*** Add recipe handler ***/
	/**************************/
	/* obsolete - replaced by webflow */
	/*@RequestMapping("recipe/addRecipe")
	public String addRecipe(Model model, HttpSession session) {
		logger.info("recipe/addRecipe GET");

		//check if a recipe object is present as a result of an error
		Recipe recipe = (Recipe)session.getAttribute("recipe");
		
		if (recipe == null) {
			recipe = new Recipe();
			recipe.setAllowShare(true);
			model.addAttribute("recipe", recipe);
			logger.info("addRecipe: session.getAttribute: recipe is null!!!");
		}
		else {
			//get the ingredient names (lazy loaded)
			model.addAttribute("ingredientList", recipeService.getIngredients(recipe));
			model.addAttribute("recipe", recipe);
			logger.info("addRecipe: session.getAttribute: recipe is NOT null!!!");
		}
					
		return "recipe/addRecipe";
	}*/

	/* obsolete - replaced by webflow */
	//save a recipe	
	/*@RequestMapping(value= {"recipe/addRecipe", "recipe/editRecipe"}, method = RequestMethod.POST)
	public String saveRecipe(Model model, @ModelAttribute @Valid Recipe recipe, BindingResult result,
			@RequestParam(value = "file", required = false) MultipartFile file, HttpSession session) {		
			HttpSession session) {
		logger.info("recipe/addRecipe POST save");

		//check for validation errors
		if (result.hasErrors()) {
			logger.info("Validation errors");

			//get the ingredient names and return the completed form with error messages
			model.addAttribute("ingredientList", recipeService.getIngredients(recipe));			
			return "recipe/addRecipe";
		}*/
		
		//TODO: EXCEPTION: need to return an error message if the file upload is unsuccessful
		//handle the file upload
		/*if (!file.isEmpty()) {
            try {
            	String filePath = System.getProperty("java.io.tmpdir") + file.getOriginalFilename();
            	logger.info("originalname = " + file.getOriginalFilename());
            	logger.info("name = " + file.getName());
            	logger.info("filePath = " + filePath);
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                stream.write(file.getBytes());
                stream.close();
                logger.info("Successful upload");
                recipe.setPhoto(file.getOriginalFilename());
            } catch (Exception e) {
            	logger.info("Exception: " + e.getMessage());
            }
        } else {
        	logger.info("Empty file");
        }*/
		
		//empty the array if the user did not enter a source		
		/*if (recipe.getSources().size() > 0) {
			String sourceType = recipe.getSources().get(0).getType();
			if (sourceType == null || sourceType.isEmpty()) {
				recipe.getSources().clear();
			}
		}*/
		/*		
		//add the object to the session in case of error
		session.setAttribute("recipe", recipe);
		
		recipeService.saveRecipe(recipe);
		
		//no error so remove the object from the session		
		session.removeAttribute("recipe");
		
		return "redirect:viewRecipe/" + recipe.getId();
	}*/

	//update a recipe
	/*@RequestMapping(value="recipe/editRecipe/{id}", method = RequestMethod.POST)
	public String updateRecipe(Model model, @ModelAttribute @Valid Recipe recipe, BindingResult result, BindingResult resultSource,
			@RequestParam(value = "file", required = false) MultipartFile file, HttpSession session) {		
		logger.info("recipe/editRecipe POST save");
	
		if (result.hasErrors()) {
			logger.info("Validation errors");
			
			//get the ingredient names and return the completed form with error messages
			model.addAttribute("ingredientList", recipeService.getIngredients(recipe));			
			return "recipe/editRecipe";
		}
		
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
                recipe.setPhoto(file.getOriginalFilename());
            } catch (Exception e) {
            	logger.info("Exception: " + e.getMessage());
            }
        } else {
        	logger.info("Empty file");
        }
		
		//empty the array if the user did not enter a source
		if (recipe.getSources().size() > 0) {
			String sourceType = recipe.getSources().get(0).getType();
			if (sourceType == null || sourceType.isEmpty()) {
				recipe.getSources().clear();
			}
		}

		//add the object to the session in case of error
		session.setAttribute("recipe", recipe);
		
		recipeService.updateRecipe(recipe);
		
		logger.info("Recipe ID = " + recipe.getId());
		
		//no error so remove the object from the session		
		session.removeAttribute("recipe");
		
		return "redirect:/viewRecipe/" + recipe.getId();
		//return "redirect:listRecipes";
	}*/

	/***************************/
	/*** Edit recipe handler ***/
	/***************************/
	/*
	/*@RequestMapping("recipe/editRecipe/{id}")
	public String editRecipe(Model model, @PathVariable Long id, HttpSession session) {
		logger.info("recipe/editRecipe GET");

		//check if a recipe object is present as a result of an error
		Recipe recipe = (Recipe)session.getAttribute("recipe");
		
		if (recipe == null) {
			logger.info("editRecipe: session.getAttribute: recipe is null!!!");
			recipe = recipeService.getRecipe(id);
		}
		else {
			//get the ingredient names
			logger.info("editRecipe: session.getAttribute: recipe is NOT null!!!");
		}

		if (recipe.getSources().size() == 0) {
			Source source = new Source(); 
			recipe.getSources().add(source);
		}

		model.addAttribute("ingredientList", recipeService.getIngredients(recipe));
		model.addAttribute("recipe", recipe);
		
		return "recipe/editRecipe";
	}*/

	
	@RequestMapping(value="recipe/deleteRecipe/{id}")
	public String deleteRecipe(@PathVariable Long id) {
		logger.info("recipe/deleteRecipe");
		
		recipeService.deleteRecipe(id);
		
		return "redirect:/recipe/listRecipes";
	}	

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
	
	//Exception handler for ingredient validation errors
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String handleMethodArgumentNotValid(HttpServletRequest req, MethodArgumentNotValidException ex) {
		
		logger.info("Caught exception");

		BindingResult result = ex.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();
		
		Locale locale = LocaleContextHolder.getLocale();
		String errorCode = "";
		String defaultMsg = "";
		String errorMsg = "";
		
		if (fieldErrors.size() > 0) {
			
			FieldError fieldErr = fieldErrors.get(0);
			defaultMsg = fieldErr.getDefaultMessage();
			errorCode = fieldErr.getCode() + "." + fieldErr.getObjectName() + "." + fieldErr.getField();
	
			//getMessage throws an error if the message is not found
			try {
				errorMsg = messages.getMessage(errorCode, null, locale);
			}
			catch (NoSuchMessageException e) {
				errorMsg = "";
			};

			if (errorMsg.isEmpty()) {
				errorMsg = defaultMsg;
			}
		}

		if (errorMsg.isEmpty()) {
			errorMsg = "Unknown error occurred";
		}
		
		logger.info("errorCode: " + errorCode);
		logger.info("errorMsg: " + errorMsg);
		
		return errorMsg;
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

	//TODO: EXCEPTION: consider using an exception handler instead?
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
}


//add/recipe debugging stuff

/*logger.info("User = " + recipe.getUser());
logger.info("Name = " + recipe.getName());
logger.info("Description = " + recipe.getDescription());
logger.info("Background = " + recipe.getBackground());
logger.info("category = " + recipe.getCategory());
logger.info("Instructions = " + recipe.getInstructions());
logger.info("Ingredients= " + recipe.getRecipeIngredients());
logger.info("Source = " + recipe.getSources());
logger.info("Tags = " + recipe.getTags());*/

/*Iterator<Instruction> iterator1 = recipe.getInstructions().iterator();
while (iterator1.hasNext()) {
	Instruction instruct = iterator1.next();
	logger.info("instruct= " + instruct); 
	logger.info("desc= " + instruct.getDescription());
	logger.info("seq= " + instruct.getSequenceNo());			
}*/

/*Iterator<RecipeIngredient> iterator2 = recipe.getRecipeIngredients().iterator();
while (iterator2.hasNext()) {
	RecipeIngredient recipeIngred = iterator2.next();
	logger.info("recipeIngred= " + recipeIngred); 
	logger.info("id= " + recipeIngred.getIngredientId());
	logger.info("qty= " + recipeIngred.getQuantity());
	logger.info("type= " + recipeIngred.getQtyType());			
	logger.info("qual= " + recipeIngred.getQualifier());
	logger.info("ingred= " + recipeIngred.getIngredient());
}*/

/*Iterator<FieldError> iterator3 = result.getFieldErrors().iterator();
while (iterator3.hasNext()) {
	FieldError err = iterator3.next();
	logger.info("field: " +  err.getField());
	logger.info("code: " +  err.getCode());
	logger.info("string: " +  err.toString());
}*/
			
/*Iterator<ObjectError> iterator4 = result.getAllErrors().iterator();
while (iterator4.hasNext()) {
	ObjectError objErr = iterator4.next();
	logger.info("objname: " +  objErr.getObjectName());
	logger.info("default error: " +  objErr.getDefaultMessage());
	logger.info("code: " +  objErr.getCode());
}*/
