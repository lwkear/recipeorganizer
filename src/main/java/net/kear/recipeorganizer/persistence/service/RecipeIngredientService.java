package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.dto.IngredientReviewDto;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
 
public interface RecipeIngredientService {
     
    public void addRecipeIngredient(RecipeIngredient recipeIngredient);
    public void updateRecipeIngredient(RecipeIngredient recipeIngredient);
    public void deleteRecipeIngredient(Long id);
    public void replaceIngredient(IngredientReviewDto ingredientReviewDto);
    public List<RecipeIngredient> getRecipeIngredients(Long recipeID);
    public List<String> getQualifiers(String searchStr);
}