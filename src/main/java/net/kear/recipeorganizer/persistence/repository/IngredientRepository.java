package net.kear.recipeorganizer.persistence.repository;

import java.util.List;

import net.kear.recipeorganizer.persistence.dto.IngredientReviewDto;
import net.kear.recipeorganizer.persistence.model.Ingredient;
 
public interface IngredientRepository {

    public void addIngredient(Ingredient ingredient);
    public void updateIngredient(Ingredient ingredient);
    public void deleteIngredient(Long id);
    public Ingredient getIngredient(Long id);
    public void setReviewed(long id, boolean reviewed);
    public List<Ingredient> listIngredients();
    public List<Ingredient> getIngredients(String searchStr, String lang);
    public long getNotReviewedCount();
    public List<IngredientReviewDto> listNotReviewed();
}
