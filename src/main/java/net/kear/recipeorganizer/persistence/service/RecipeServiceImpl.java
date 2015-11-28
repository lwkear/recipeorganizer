package net.kear.recipeorganizer.persistence.service;
 
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.model.IngredientSection;
import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.model.InstructionSection;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
import net.kear.recipeorganizer.persistence.model.Source;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.repository.RecipeRepository;
import net.kear.recipeorganizer.persistence.service.RecipeService;

import org.apache.commons.lang.math.Fraction;
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
	//TODO: RECIPE: replace with above after testing!!!
    public Recipe createRecipe() {
    	User user = userService.findUserByEmail("lwk@outlook.com");
    	
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
    
    public List<Ingredient> getIngredients(Recipe recipe, int sectionNdx) {
    	return recipeRepository.getIngredients(recipe, sectionNdx);
    }

    public void getAllIngredients(Recipe recipe) {
    	recipeRepository.getAllIngredients(recipe);
    }
    
    public List<String> getTags(String searchStr, Long userId) {
    	return recipeRepository.getTags(searchStr, userId);
    }
    
    public boolean lookupName(String lookupName, Long userId) {
    	return recipeRepository.lookupName(lookupName, userId);
    }
    
    public void checkArraySizes(Recipe recipe) {
    	int sections = recipe.getNumInstructSections();
    	int size = recipe.getInstructSections().size();
    	logger.info("numInstructSections=" + sections);
    	logger.info("instructArraySize=" + size); 
    	if (size > sections) {
    		logger.info("need to adjust size");
    		for (int ndx=size;ndx>sections;ndx--) {
    			recipe.getInstructSections().remove(ndx-1);
			}    		
    	}

    	sections = recipe.getNumIngredSections();
    	size = recipe.getIngredSections().size();
    	logger.info("numIngredientSections=" + sections);
    	logger.info("ingredArraySize=" + size); 
    	if (size > sections) {
    		logger.info("need to adjust size");
    		for (int ndx=size;ndx>sections;ndx--) {
    			recipe.getIngredSections().remove(ndx-1);
			}    		
    	}
    }
    
    public void adjustInstructionList(Recipe recipe, RequestContext context) {
    	logger.info("adjustInstructionList");
    	logger.info("parsing instructions");

    	int size = recipe.getInstructSections().size();
		int ndx = 0;
		if (size > 0) {
			Iterator<InstructionSection> iterator1 = recipe.getInstructSections().iterator();
			while (iterator1.hasNext()) {
				InstructionSection instructSection = iterator1.next();
				logger.info("ndx= " + ndx++);
				logger.info("id= " + instructSection.getId()); 
				logger.info("seq= " + instructSection.getSequenceNo());
				logger.info("name= " + instructSection.getName());
				size = instructSection.getInstructions().size();
				if (size > 0) {
					Iterator<Instruction> iterator2 = instructSection.getInstructions().iterator();
					while (iterator2.hasNext()) {
						Instruction instruct = iterator2.next();
						logger.info("id = " + instruct.getId()); 
						logger.info("desc= " + instruct.getDescription());
						logger.info("seq= " + instruct.getSequenceNo());			
					}					
				}
			}			
		}
    	
    	String key = null;
    	String value = null;
    	Instruction inst = null;
    	int seq = 1;
    	int sectNdx = 0;
    	boolean forward = false;
    	boolean back = false;
    	
    	//get the parameters
    	ParameterMap requestParameters = context.getRequestParameters();
    	//create a new empty list
    	List<Instruction> requestInstruct = new AutoPopulatingList<Instruction>(Instruction.class);

    	//get the parameters from the request and parse the set looking for instructions
    	Set<Entry<String, Object>> entries = requestParameters.asMap().entrySet();
    	for (Entry<String, Object> entry : entries) {
    		key = entry.getKey();
    		value = requestParameters.get(key);
    		logger.info("key/value= " + key + "/" + value);
    		//get the index of the instruction section
    		
    		//code to get the section index with a regex
    		/*if (key.contains("name")) {    			
    			Pattern arrayNdx = Pattern.compile("\\d+");
    			Matcher makeMatch = arrayNdx.matcher(key);
    			makeMatch.find();
    			String index = makeMatch.group();
    			sectNdx = Integer.parseInt(index);   			
    		}*/
    		
    		if (key.contains("currInstructSection")) {
				sectNdx = Integer.parseInt(value);
    		}
    		
    		if (key.contains("instructions")) {
    			/*if (key.contains(".id")) {
	    			inst = new Instruction();
					int id = Integer.parseInt((String)value);
					inst.setId(id);
	    			inst.setSequenceNo(seq++);
    			}*/    			
    			if (key.contains("description")) {
    				inst = new Instruction();
	    			inst.setDescription(value);
	    			inst.setSequenceNo(seq++);
	    			requestInstruct.add(inst);	    			
    			}    			
    		}
    		if (key.contains("_proceed"))
    			forward = true;
    		if (key.contains("_back"))
    			back = true;
    	}

    	//replace the current list with the parsed list
    	if (requestInstruct.size() > 0) {
    		recipe.getInstructionSection(sectNdx).getInstructions().clear();
    		recipe.getInstructionSection(sectNdx).setInstructions(requestInstruct);
	    	if (forward) {
		    	recipe.getInstructionSection(sectNdx).setSequenceNo(sectNdx+1);
				recipe.setCurrInstructSection(sectNdx+1);
			}
			if (back)
				recipe.setCurrInstructSection(sectNdx-1);	
    	}
    	else {
    		//back to previous page, but no instructions so remove the section
    		if (back) {
    			if (sectNdx < recipe.getInstructSections().size())
    				recipe.getInstructSections().remove(sectNdx);
    			recipe.setCurrInstructSection(sectNdx-1);
    		}
    	}
    }

	public void adjustRecipeIngredientList(Recipe recipe, RequestContext context) {
		logger.info("adjustRecipeIngredientList");
		logger.info("parsing ingredients");

		int size = recipe.getIngredSections().size();
		int ndx = 0;
		if (size > 0) {
			Iterator<IngredientSection> iterator1 = recipe.getIngredSections().iterator();
			while (iterator1.hasNext()) {
				IngredientSection ingredSection = iterator1.next();
				logger.info("ndx= " + ndx++);
				logger.info("id= " + ingredSection.getId()); 
				logger.info("seq= " + ingredSection.getSequenceNo());
				logger.info("name= " + ingredSection.getName());
				size = ingredSection.getRecipeIngredients().size();
				if (size > 0) {
					Iterator<RecipeIngredient> iterator2 = ingredSection.getRecipeIngredients().iterator();
					while (iterator2.hasNext()) {
						RecipeIngredient recipeIngred = iterator2.next();
						logger.info("id = " + recipeIngred.getId()); 
						logger.info("seq= " + recipeIngred.getSequenceNo());
						logger.info("qty= " + recipeIngred.getQuantity());
						logger.info("qtyAmt= " + recipeIngred.getQtyAmt());
						logger.info("type= " + recipeIngred.getQtyType());			
						logger.info("qual= " + recipeIngred.getQualifier());
						logger.info("ingredId= " + recipeIngred.getIngredientId());
					}					
				}
			}			
		}

    	String key = null;
    	String value = null;
    	RecipeIngredient ingred = null;
    	int seq = 1;
    	int sectNdx = 0;
    	boolean forward = false;
    	boolean back = false;
		
		//get the parameters
		ParameterMap requestParameters = context.getRequestParameters();
		//create a new empty list
		List<RecipeIngredient> requestIngred = new AutoPopulatingList<RecipeIngredient>(RecipeIngredient.class);
	
		//get the parameters from the request and parse the set looking for ingredients
		Set<Entry<String, Object>> entries = requestParameters.asMap().entrySet();
		for (Entry<String, Object> entry : entries) {
			key = entry.getKey();
			value = requestParameters.get(key);
			logger.info("key/value= " + key + "/" + value);

    		if (key.contains("currIngredSection")) {
				sectNdx = Integer.parseInt(value);
    		}
			
			if (key.contains("recipeIngredients")) {
				/*if (key.contains(".id")) {
					ingred = new RecipeIngredient();
					String str = (String)value;
					//int id = Integer.parseInt((String)str);
					int id = Integer.parseInt(str);
					ingred.setId(id);
					ingred.setSequenceNo(seq++);
				}
				*/
				if (key.contains("ingredientId")) {
					ingred = new RecipeIngredient();
					int id = Integer.parseInt(value);
					ingred.setIngredientId(id);
					ingred.setSequenceNo(seq++);
				}
				if (key.contains("quantity")) {
					ingred.setQuantity(value);
					if (!value.isEmpty()) {
						Fraction fract = Fraction.getFraction(ingred.getQuantity());
						float qty = fract.floatValue();
						ingred.setQtyAmt(qty);
					}
				}
				if (key.contains("qtyType")) {
					ingred.setQtyType(value);
				}
				if (key.contains("qualifier")) {
					ingred.setQualifier(value);
					requestIngred.add(ingred);
				}	
			}
    		if (key.contains("_proceed"))
    			forward = true;
    		if (key.contains("_back"))
    			back = true;
		}

    	//replace the current list with the parsed list
    	if (requestIngred.size() > 0) {
    		recipe.getIngredientSection(sectNdx).getRecipeIngredients().clear();
			recipe.getIngredientSection(sectNdx).setRecipeIngredients(requestIngred);
    		
    		if (forward) {
		    	recipe.getIngredientSection(sectNdx).setSequenceNo(sectNdx+1);
				recipe.setCurrIngredSection(sectNdx+1);
			}
			if (back)
				recipe.setCurrIngredSection(sectNdx-1);	
    	}
    	else {
    		//back to previous page, but no ingredients so remove the section
    		if (back) {
    			if (sectNdx < recipe.getIngredSections().size())
    				recipe.getIngredSections().remove(sectNdx);
    			recipe.setCurrIngredSection(sectNdx-1);
    		}
    	}
	}
}
