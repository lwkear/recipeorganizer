package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.dto.IngredientReviewDto;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
import net.kear.recipeorganizer.persistence.repository.RecipeIngredientRepository;
import net.kear.recipeorganizer.persistence.service.RecipeIngredientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@Transactional
public class RecipeIngredientServiceImpl implements RecipeIngredientService {
 
    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;
      
    public void addRecipeIngredient(RecipeIngredient recipeIngredient) {
    	recipeIngredientRepository.addRecipeIngredient(recipeIngredient);
    }
    
    public void updateRecipeIngredient(RecipeIngredient recipeIngredient) {
    	recipeIngredientRepository.updateRecipeIngredient(recipeIngredient);
    }
 
    public void deleteRecipeIngredient(Long id) {
    	recipeIngredientRepository.deleteRecipeIngredient(id);
    }
    
    public void replaceIngredient(IngredientReviewDto ingredientReviewDto) {
    	recipeIngredientRepository.replaceIngredient(ingredientReviewDto.getId(), ingredientReviewDto.getUsage());
    }

    public List<RecipeIngredient> getRecipeIngredients(Long recipeID) {
    	return recipeIngredientRepository.getRecipeIngredients(recipeID);
    }
    
    public List<String> getQualifiers(String searchStr) {
    	return recipeIngredientRepository.getQualifiers(searchStr);
    }
 
}