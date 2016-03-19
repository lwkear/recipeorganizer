package net.kear.recipeorganizer.persistence.repository;

import java.util.List;

import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
 
public interface RecipeIngredientRepository {

    public void addRecipeIngredient(RecipeIngredient recipeIngredient);
    public void updateRecipeIngredient(RecipeIngredient recipeIngredient);
    public void deleteRecipeIngredient(Long recipeIngredientId);
    public void replaceIngredient(Long oldIngredientId, Long newIngredientId);
    public List<RecipeIngredient> getRecipeIngredients(Long recipeID);
    public List<String> getQualifiers(String searchStr);

}
