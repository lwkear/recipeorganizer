package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.repository.RecipeRepository;
import net.kear.recipeorganizer.persistence.service.RecipeService;







//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@Transactional
public class RecipeServiceImpl implements RecipeService {
	
    @Autowired
    private RecipeRepository recipeRepository;
      
    public void addRecipe(Recipe recipe) {
    	recipeRepository.addRecipe(recipe);
    }
    
    public void updateRecipe(Recipe recipe) {
    	recipeRepository.updateRecipe(recipe);
    }
 
    public void deleteRecipe(Long id) {
    	recipeRepository.deleteRecipe(id);
    }

    public Recipe getRecipe(Long id) {
    	return recipeRepository.getRecipe(id);
    }

    public List<RecipeListDto> listRecipes() {
    	return recipeRepository.listRecipes();
    }
    
    public List<Ingredient> getIngredients(Recipe recipe) {
    	return recipeRepository.getIngredients(recipe);
    }
    
    public List<String> getTags(String searchStr, Long userId) {
    	return recipeRepository.getTags(searchStr, userId);
    }
    
    public boolean lookupName(String lookupName, Long userId) {
    	return recipeRepository.lookupName(lookupName, userId);
    }
}