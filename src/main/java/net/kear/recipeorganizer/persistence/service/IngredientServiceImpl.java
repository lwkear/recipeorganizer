package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.repository.IngredientRepository;
import net.kear.recipeorganizer.persistence.service.IngredientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@Transactional
public class IngredientServiceImpl implements IngredientService {
 
    @Autowired
    private IngredientRepository ingredientRepository;
      
    public void addIngredient(Ingredient ingredient) {
    	ingredientRepository.addIngredient(ingredient);
    }
    
    public void updateIngredient(Ingredient ingredient) {
    	ingredientRepository.updateIngredient(ingredient);
    }
 
    public void deleteIngredient(Long id) {
    	ingredientRepository.deleteIngredient(id);
    }

    public List<Ingredient> listIngredient() {
    	return ingredientRepository.listIngredient();
    }
 
    public List<Ingredient> getIngredients(String searchStr) {
    	return ingredientRepository.getIngredients(searchStr);
    }
}