package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;
import java.util.Map;

import org.springframework.webflow.execution.RequestContext;

import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.dto.SearchResultsDto;
import net.kear.recipeorganizer.persistence.model.Favorites;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeMade;
import net.kear.recipeorganizer.persistence.model.RecipeNote;
 
public interface RecipeService {
     
	public Recipe createRecipe(String userName);
	public void addRecipe(Recipe recipe);
    public void updateRecipe(Recipe recipe);
    public void saveRecipe(Recipe recipe);
    public void deleteRecipe(Long id);
    public void approveRecipe(Long id);
    public Recipe getRecipe(Long id);
    public Recipe loadRecipe(Long id);
    public Map<String, Object> getConstraintMap(String constraintName, String property);
    public void addFavorite(Favorites favorite);
    public void removeFavorite(Favorites favorite);
    public boolean isFavorite(Long userId, Long recipeId);
    public void updateRecipeMade(RecipeMade recipeMade);
    public RecipeMade getRecipeMade(Long userId, Long recipeId);
    public void updateRecipeNote(RecipeNote recipeNote);
    public RecipeNote getRecipeNote(Long userId, Long recipeId);
    public void addView(Recipe recipe);
    public Long getRecipeViewCount(Long recipeId);
    public Long getUserViewCount(Long userId);
    public List<RecipeListDto> approveRecipesList();
    public List<RecipeListDto> listRecipes(Long userId);
    public List<SearchResultsDto> listRecipes(List<String> ids);
    public List<SearchResultsDto> recentRecipes(Long userId);
    public List<RecipeListDto> favoriteRecipes(Long userId);
    public Long getRecipeCount(Long userId);
    public List<String> getTags(Long userId);
    public boolean lookupName(String lookupName, Long userId);
    public void checkArraySizes(Recipe recipe);
    public void adjustInstructionList(Recipe recipe, RequestContext context);
    public void adjustRecipeIngredientList(Recipe recipe, RequestContext context);
    
}