package net.kear.recipeorganizer.persistence.service;
 
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
import net.kear.recipeorganizer.persistence.model.Source;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.repository.RecipeRepository;
import net.kear.recipeorganizer.persistence.service.RecipeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AutoPopulatingList;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.RequestContext;
 
@Service("recipeService")
@Transactional
public class RecipeServiceImpl implements RecipeService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
    @Autowired
    private RecipeRepository recipeRepository;

	@Autowired
	private UserService userService;
    
    /*public Recipe createRecipe(String userName) {*/
		/*User user = userService.findUserByEmail(userName);*/
    public Recipe createRecipe() {
    	User user = userService.findUserByEmail("kear@outlook.com");	//TODO: RECIPE: remove this after testing!!!
    	
		Recipe recipe = new Recipe();
		recipe.setAllowShare(true);
		recipe.setCategory(new Category());
		Source source = new Source();
		source.setRecipe(recipe);
		recipe.setSource(source);
		recipe.setUser(user);
		return recipe;
    }
    
    public void addRecipe(Recipe recipe) {
    	recipeRepository.addRecipe(recipe);
    }
    
    public void updateRecipe(Recipe recipe) {
    	recipeRepository.updateRecipe(recipe);
    }

    public void saveRecipe(Recipe recipe) {
	
    	String type = recipe.getSource().getType(); 
    	if (type == null || type.isEmpty()) {
			recipe.setSource(null);
		}
    	
    	//assume if the recipe has an ID then it must already exist
    	if (recipe.getId() > 0)
    		recipeRepository.updateRecipe(recipe);
    	else
    		recipeRepository.addRecipe(recipe);
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
    
    public void adjustInstructionList(Recipe recipe, RequestContext context) {
    	logger.info("adjustInstructionList");
    	
    	/*logger.info("parsing instructions");
    	int ndx = 0;
    	Iterator<Instruction> iterator1 = recipe.getInstructions().iterator();
    	while (iterator1.hasNext()) {
    		Instruction instruct = iterator1.next();
    		logger.info("ndx= " + ndx++);
    		logger.info("seq= " + instruct.getSequenceNo());
    		logger.info("desc= " + instruct.getDescription());
    	}
    	
    	String key = null;
    	Object value = null;
    	int seq = 1;
    	Instruction inst = null;
    	
    	//get the parameters
    	ParameterMap requestParameters = context.getRequestParameters();
    	//create a new empty list
    	List<Instruction> requestInstruct = new AutoPopulatingList<Instruction>(Instruction.class);

    	//get the parameters from the request and parse the set looking for instructions
    	Set<Entry<String, Object>> entries = requestParameters.asMap().entrySet();
    	for (Entry<String, Object> entry : entries) {
    		key = entry.getKey();
    		value = entry.getValue();
    		logger.info("key/value= " + key + "/" + value);
    		if (key.contains("instructions")) {
    			if (key.contains(".id")) {
	    			inst = new Instruction();
					int id = Integer.parseInt((String)value);
					inst.setId(id);
	    			inst.setSequenceNo(seq++);
    			}    			
    			if (key.contains("description")) {
    				inst = new Instruction();
	    			inst.setDescription((String)value);
	    			requestInstruct.add(inst);
	    			inst.setSequenceNo(seq++);
    			}    			
    		}
    	}
    	
    	//replace the current list with the parsed list
    	if (requestInstruct.size() > 0) {
    		recipe.getInstructions().clear();
    		recipe.setInstruction(requestInstruct);
    	}*/
    }

	public void adjustRecipeIngredientList(Recipe recipe, RequestContext context) {
		logger.info("adjustRecipeIngredientList");
		
		logger.info("parsing ingredients");
		Iterator<RecipeIngredient> iterator2 = recipe.getRecipeIngredients().iterator();
		while (iterator2.hasNext()) {
			RecipeIngredient recipeIngred = iterator2.next();
			/*logger.info("id= " + recipeIngred.getId());*/
			logger.info("seq= " + recipeIngred.getSequenceNo());
			logger.info("ingredId= " + recipeIngred.getIngredientId());
			logger.info("qty= " + recipeIngred.getQuantity());
			logger.info("qtyAmt= " + recipeIngred.getQtyAmt());
			logger.info("type= " + recipeIngred.getQtyType());			
			logger.info("qual= " + recipeIngred.getQualifier());
			logger.info("ingred= " + recipeIngred.getIngredient());
		}	
		
		int seq = 1;
		String key = null;
		Object value = null;
		RecipeIngredient ingred = null;
		
		//get the parameters
		ParameterMap requestParameters = context.getRequestParameters();
		//create a new empty list
		List<RecipeIngredient> requestIngred = new AutoPopulatingList<RecipeIngredient>(RecipeIngredient.class);
	
		//get the parameters from the request and parse the set looking for instructions
		Set<Entry<String, Object>> entries = requestParameters.asMap().entrySet();
		for (Entry<String, Object> entry : entries) {
			key = entry.getKey();
			value = entry.getValue();
			logger.info("key/value= " + key + "/" + value);
			if (key.contains("recipeIngredients")) {
				/*if (key.contains(".id")) {
					ingred = new RecipeIngredient();
					String str = (String)value;
					//int id = Integer.parseInt((String)str);
					int id = Integer.parseInt(str);
					ingred.setId(id);
					ingred.setSequenceNo(seq++);
				}
				if (key.contains("ingredientId")) {
					int id = Integer.parseInt((String)value);
					ingred.setIngredientId(id);
				}*/
				if (key.contains("ingredientId")) {
					ingred = new RecipeIngredient();
					String str = (String)value;
					int id = Integer.parseInt((String)str);
					ingred.setIngredientId(id);
					ingred.setSequenceNo(seq++);
				}
				if (key.contains("quantity")) {
					ingred.setQuantity((String)value);
				}
				if (key.contains("qtyType")) {
					ingred.setQtyType((String)value);
				}
				if (key.contains("qualifier")) {
					ingred.setQualifier((String)value);
					requestIngred.add(ingred);
				}	
			}
		}
		
		//replace the current list with the parsed list
		if (requestIngred.size() > 0) {
			recipe.getRecipeIngredients().clear();
			recipe.setRecipeIngredients(requestIngred);
		}
	}
}
