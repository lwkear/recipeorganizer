package net.kear.recipeorganizer.controller;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.service.CategoryService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.UserService;

@Controller
public class ViewRecipeController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private UserService userService;
	
	@RequestMapping("recipe/viewRecipe/{id}")
	public String displayRecipe(ModelMap model, @PathVariable Long id) {
		logger.info("recipe/viewRecipe GET");

		Recipe recipe = recipeService.getRecipe(id);
		model.addAttribute("recipe", recipe);

		return "recipe/viewRecipe";
	}
	
	@RequestMapping("recipe/listRecipes")
	public String listRecipeS(ModelMap model) {
		logger.info("recipe/listRecipes");

		List<RecipeListDto> recipes = recipeService.listRecipes();
		model.addAttribute("recipes", recipes);

		return "recipe/listRecipes";
	}
	
	@ExceptionHandler(DataAccessException.class)
	public ModelAndView handleDataAccessException(DataAccessException ex) {
		ModelAndView view = new ModelAndView("/errors/errorData");

		logger.info("Recipe: DataAccessException exception!");
		
		String errorMsg = ExceptionUtils.getRootCauseMessage(ex);
		
		view.addObject("errorMessage", errorMsg);
		return view;
	}

	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(Exception ex) {
		ModelAndView view = new ModelAndView("/errors/errorData");

		logger.info("Recipe: Exception exception!");
		
		String errorMsg = ExceptionUtils.getRootCauseMessage(ex);
		
		view.addObject("errorMessage", errorMsg);
		return view;
	}
}
