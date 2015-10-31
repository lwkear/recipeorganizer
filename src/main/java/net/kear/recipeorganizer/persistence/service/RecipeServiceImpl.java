package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.repository.RecipeRepository;
import net.kear.recipeorganizer.persistence.service.RecipeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service("recipeService")
@Transactional
public class RecipeServiceImpl implements RecipeService {
	
    @Autowired
    private RecipeRepository recipeRepository;

	@Autowired
	private UserService userService;
    
    /*public Recipe createRecipe(String userName) {
    	User user = userService.findUserByEmail(userName);
    	
		Recipe recipe = new Recipe();
		recipe.setAllowShare(true);
		recipe.setFavorite(false);
		recipe.setUser(user);
		return recipe;
    }*/
    
    public Recipe createRecipe() {
		Recipe recipe = new Recipe();
		recipe.setAllowShare(true);
		return recipe;
    }
    
    public void addRecipe(Recipe recipe) {
    	recipeRepository.addRecipe(recipe);
    }
    
    public void updateRecipe(Recipe recipe) {
    	recipeRepository.updateRecipe(recipe);
    }

    public void saveRecipe(Recipe recipe) {
    	//assume if the recipe has an ID then it must already exist
    	if (recipe.getId() > 0)
    		recipeRepository.updateRecipe(recipe);
    	else
    		recipeRepository.addRecipe(recipe);
    }
    
    public void deleteRecipe(Long id) {
    	recipeRepository.deleteRecipe(id);
    }

    public Recipe getRecipe(Long id) {
    	return recipeRepository.getRecipe(id);
    }

    public List<RecipeListDto> listRecipes() {
    	return recipeRepository.listRecipes();
    }
    
    public List<Ingredient> getIngredients(Recipe recipe) {
    	return recipeRepository.getIngredients(recipe);
    }
    
    public List<String> getTags(String searchStr, Long userId) {
    	return recipeRepository.getTags(searchStr, userId);
    }
    
    public boolean lookupName(String lookupName, Long userId) {
    	return recipeRepository.lookupName(lookupName, userId);
    }
}