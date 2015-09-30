package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Ingredient;
 
public interface IngredientService {
     
    public void addIngredient(Ingredient ingredient);
    public void updateIngredient(Ingredient ingredient);
    public void deleteIngredient(Long id);
    public List<Ingredient> listIngredient();
    public List<Ingredient> getIngredients(String searchStr);
}