package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import org.springframework.webflow.execution.RequestContext;

import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.dto.SearchResultsDto;
import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.model.Recipe;
 
public interface RecipeService {
     
	public Recipe createRecipe(String userName);
	//public Recipe createRecipe();
	public void addRecipe(Recipe recipe);
    public void updateRecipe(Recipe recipe);
    public void saveRecipe(Recipe recipe);
    public void deleteRecipe(Long id);
    public Recipe getRecipe(Long id);
    public List<RecipeListDto> listRecipes(Long userId);
    public List<SearchResultsDto> listRecipes(List<String> ids);
    public Long getRecipeCount(Long userId);
    public List<Ingredient> getIngredients(Recipe recipe, int sectionNdx);
    public void getAllIngredients(Recipe recipe);
    public List<String> getTags(String searchStr, Long userId);
    public boolean lookupName(String lookupName, Long userId);
    public void checkArraySizes(Recipe recipe);
    public void adjustInstructionList(Recipe recipe, RequestContext context);
    public void adjustRecipeIngredientList(Recipe recipe, RequestContext context);
}