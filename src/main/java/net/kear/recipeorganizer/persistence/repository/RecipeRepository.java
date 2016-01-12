package net.kear.recipeorganizer.persistence.repository;

import java.util.List;

import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.dto.SearchResultsDto;
import net.kear.recipeorganizer.persistence.model.Favorites;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeMade;
import net.kear.recipeorganizer.persistence.model.RecipeNote;
 
public interface RecipeRepository {

    public void addRecipe(Recipe recipe);
    public void updateRecipe(Recipe recipe);
    public void deleteRecipe(Long id);
    public Recipe getRecipe(Long id);
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
    public List<Favorites> getFavorites(Long userId);
    public List<RecipeListDto> listRecipes(Long userId);
    public List<SearchResultsDto> listRecipes(List<Long> ids);
    public List<SearchResultsDto> recentRecipes(Long userId);
    public List<RecipeListDto> favoriteRecipes(List<Long> ids);
    public Long getRecipeCount(Long userId);
    public List<String> getTags(String searchStr, Long userId);
    public boolean lookupName(String lookupName, Long userId);
}
