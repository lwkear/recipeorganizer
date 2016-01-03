package net.kear.recipeorganizer.persistence.service;
 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.dto.SearchResultsDto;
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
    
    //called by webflow to initialize the recipe object
	public Recipe createRecipe(String userName) {
		User user = userService.findUserByEmail(userName);
    	
		Recipe recipe = new Recipe();
		recipe.setAllowShare(true);
		recipe.setCategory(new Category());
		
		/*IngredientSection ingredSection = new IngredientSection();
		ingredSection.setRecipeIngredients(new AutoPopulatingList<RecipeIngredient>(RecipeIngredient.class));
		List<IngredientSection> ingredSections = new AutoPopulatingList<IngredientSection>(IngredientSection.class);
		ingredSections.add(ingredSection);
		recipe.setIngredSections(ingredSections);*/
		
		/*InstructionSection instructSection = new InstructionSection();
		instructSection.setInstructions(new AutoPopulatingList<Instruction>(Instruction.class));
		List<InstructionSection> instructSections = new AutoPopulatingList<InstructionSection>(InstructionSection.class);
		instructSections.add(instructSection);
		recipe.setInstructSections(instructSections);*/
		
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
    	else
    		recipe.getSource().setRecipe(recipe);
    	
    	//the NAME field in both section headers is a required field so the default value is set to "XXXX"
    	//if there is only one section, however, that name must be removed so it does not appear when
    	//when the recipe is displayed or printed
    	int ndx = 0;
    	for (IngredientSection section : recipe.getIngredSections()) {
    		if (section.getName().equalsIgnoreCase("XXXX")) {
    			section.setName("");
    			//recipe.getIngredSections().set(ndx, section);
    		}
    		
    		//convert the qty string to a float
    		for (RecipeIngredient ingred : section.getRecipeIngredients()) {
    			String value = ingred.getQuantity();
    			float amt = ingred.getQtyAmt();
    			if (!value.isEmpty() && amt == 0) {
    				Fraction fract = Fraction.getFraction(value);
    				float qty = fract.floatValue();
    				ingred.setQtyAmt(qty);
    			}    			
    		}
    		ndx++;    	
    	}
    	
    	ndx = 0;
    	for (InstructionSection section : recipe.getInstructSections()) {
    		if (section.getName().equalsIgnoreCase("XXXX")) {
    			section.setName("");
    			//recipe.getInstructSections().set(ndx, section);
    		}
    		
    		//for (Instruction instruct : section.getInstructions()) {
    		//	instruct.setInstructionSection(section);
    		//}    		
    		ndx++;
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

    public List<RecipeListDto> listRecipes(Long userId) {
    	return recipeRepository.listRecipes(userId);
    }
    
    public List<SearchResultsDto> listRecipes(List<String> ids) {
    	ArrayList<Long> recipeIds = new ArrayList<Long>();
    	for (String id : ids) {
    		recipeIds.add(Long.valueOf(id));
    	}
	    	
    	return recipeRepository.listRecipes(recipeIds);
    }
    
    public Long getRecipeCount(Long userId) {
    	return recipeRepository.getRecipeCount(userId);
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
    		for (int ndx=size;ndx>sections;ndx--) {
    			logger.info("remove section " + (ndx-1));
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
    	int numSections = 0;
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
    		
    		if (key.contains("currInstructSection"))
				sectNdx = Integer.parseInt(value);
    		if (key.contains("numInstructSections"))
    			numSections = Integer.parseInt(value);
    		if (key.contains("instructions")) {
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

				//recipe is initialized with a single section, so additional sections are added on the fly
				/*if (numSections > recipe.getInstructSections().size()) {
					List<Instruction> instructs = new AutoPopulatingList<Instruction>(Instruction.class);
					InstructionSection section = new InstructionSection();
					section.setInstructions(instructs);
					recipe.getInstructSections().add(section);
				}*/
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
    	int numSections = 0;
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

    		if (key.contains("currIngredSection"))
				sectNdx = Integer.parseInt(value);
    		if (key.contains("numIngredSections"))
    			numSections = Integer.parseInt(value);
			if (key.contains("recipeIngredients")) {
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
					else
						ingred.setQtyAmt(0);
				}
				if (key.contains("qtyType"))
					ingred.setQtyType(value);
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
				
				//recipe is initialized with a single section, so additional sections are added on the fly
				/*if (numSections > recipe.getIngredSections().size()) {
					List<RecipeIngredient> ingreds = new AutoPopulatingList<RecipeIngredient>(RecipeIngredient.class);
					IngredientSection section = new IngredientSection();
					section.setRecipeIngredients(ingreds);
					recipe.getIngredSections().add(section);
				}*/
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
