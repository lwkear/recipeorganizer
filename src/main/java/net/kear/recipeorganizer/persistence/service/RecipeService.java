package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.model.Recipe;
 
public interface RecipeService {
     
	public Recipe createRecipe(String userName);
	public void addRecipe(Recipe recipe);
    public void updateRecipe(Recipe recipe);
    public void saveRecipe(Recipe recipe);
    public void deleteRecipe(Long id);
    public Recipe getRecipe(Long id);
    public List<RecipeListDto> listRecipes();
    public List<Ingredient> getIngredients(Recipe recipe);
    public List<String> getTags(String searchStr, Long userId);
    public boolean lookupName(String lookupName, Long userId);
}