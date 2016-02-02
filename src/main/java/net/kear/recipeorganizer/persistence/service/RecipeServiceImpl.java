package net.kear.recipeorganizer.persistence.service;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.dto.SearchResultsDto;
import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.model.Favorites;
import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.model.IngredientSection;
import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.model.InstructionSection;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
import net.kear.recipeorganizer.persistence.model.RecipeMade;
import net.kear.recipeorganizer.persistence.model.RecipeNote;
import net.kear.recipeorganizer.persistence.model.Source;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.repository.RecipeRepository;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.util.ConstraintMap;
import net.kear.recipeorganizer.util.FileActions;
import net.kear.recipeorganizer.util.FileType;
import net.kear.recipeorganizer.util.SolrUtil;

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
	@Autowired
	private FileActions fileAction;
	@Autowired
	private SolrUtil solrUtil;
	@Autowired
	private ConstraintMap constraintMap;
    
    //called by webflow to initialize the recipe object
	public Recipe createRecipe(String userName) {
		User user = userService.findUserByEmail(userName);
    	
		Recipe recipe = new Recipe();
		recipe.setAllowShare(true);
		recipe.setViews(0);
		recipe.setPhotoName("");
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
    	else
    		recipe.getSource().setRecipe(recipe);
    	
    	//the NAME field in both section headers is a required field so the default value is set to "XXXX"
    	//if there is only one section, however, that name must be removed so it does not appear when
    	//when the recipe is displayed or printed
    	for (IngredientSection section : recipe.getIngredSections()) {
    		if (section.getName().equals("XXXX")) {
    			section.setName("");
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
    	}
    	
    	for (InstructionSection section : recipe.getInstructSections()) {
    		if (section.getName().equals("XXXX"))
    			section.setName("");
    	}
    	
    	//assume if the recipe has an ID then it must already exist
    	if (recipe.getId() > 0)
    		recipeRepository.updateRecipe(recipe);
    	else {
    		recipeRepository.addRecipe(recipe);
    		
    		String photoName = recipe.getPhotoName(); 
    		if (photoName!= null && !photoName.isEmpty()) {
    			String newName = recipe.getId() + "." + photoName;
    			//errors are not fatal and will be logged by FileAction
    			fileAction.renameFile(FileType.RECIPE, recipe.getPhotoName(), newName);
    		}

    		//errors are not fatal and will be logged by SolrUtil
    		solrUtil.addRecipe(recipe);
    	}    		
    }
    
    public void deleteRecipe(Long id) {
    	recipeRepository.deleteRecipe(id);
    }

    public Recipe getRecipe(Long id) {
    	return recipeRepository.getRecipe(id);
    }

    @SuppressWarnings("rawtypes")
    public Map<String, Object> getConstraintMap(String constraintName, String property) {
		Class[] classes = {Recipe.class, RecipeIngredient.class, Ingredient.class, Source.class, InstructionSection.class, IngredientSection.class};
		Map<String, Object> constraints = constraintMap.getModelConstraints(constraintName, property, classes);
		return constraints;    	
    }
    
    public void addFavorite(Favorites favorite) {
    	recipeRepository.addFavorite(favorite);
    }
    
    public void removeFavorite(Favorites favorite) {
    	recipeRepository.removeFavorite(favorite);
    }
    
    public boolean isFavorite(Long userId, Long recipeId) {
    	return recipeRepository.isFavorite(userId, recipeId);
    }
    
    public void updateRecipeMade(RecipeMade recipeMade) {
    	recipeRepository.updateRecipeMade(recipeMade);
    }
    
    public RecipeMade getRecipeMade(Long userId, Long recipeId) {
    	return recipeRepository.getRecipeMade(userId, recipeId);
    }
    
    public void updateRecipeNote(RecipeNote recipeNote) {
    	recipeRepository.updateRecipeNote(recipeNote);
    }
    
    public RecipeNote getRecipeNote(Long userId, Long recipeId) {
    	return recipeRepository.getRecipeNote(userId, recipeId);
    }    
    
    public void addView(Recipe recipe) {
    	int views = recipe.getViews() + 1;
    	recipe.setViews(views);
    	recipeRepository.addView(recipe);
    }
    
    public Long getRecipeViewCount(Long recipeId) {
    	return recipeRepository.getRecipeViewCount(recipeId);
    }
    
    public Long getUserViewCount(Long userId) {
    	return recipeRepository.getUserViewCount(userId);
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

    public List<SearchResultsDto> recentRecipes(Long userId) {
    	return recipeRepository.recentRecipes(userId);
    }

    public List<RecipeListDto> favoriteRecipes(Long userId) {
    	List<Favorites> favorites = recipeRepository.getFavorites(userId);
    	List<Long> recipeIds = new AutoPopulatingList<Long>(Long.class);
    	List<RecipeListDto> recipes = null;
    	
    	if (favorites.size() > 0) {
    		for (Favorites fav : favorites) {
    			recipeIds.add(fav.getId().getRecipeId());
    		}
    		recipes = recipeRepository.favoriteRecipes(recipeIds);     		
    	}
    	
    	return recipes;
    }
    
    public Long getRecipeCount(Long userId) {
    	return recipeRepository.getRecipeCount(userId);
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
  	
    	String key = null;
    	String value = null;
    	Instruction inst = null;
    	int seq = 1;
    	int sectNdx = 0;
    	boolean forward = false;
    	boolean back = false;
    	boolean hasStep = false;
    	
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
    		if (key.contains("instructions")) {
    			if (key.contains("description")) {
    				inst = new Instruction();
	    			inst.setDescription(value);
	    			inst.setSequenceNo(seq++);
	    			requestInstruct.add(inst);
	    			hasStep = true;
    			}    			
    		}
    		if (key.contains("_proceed"))
    			forward = true;
    		if (key.contains("_back"))
    			back = true;
    	}

    	//replace the current list with the parsed list
    	if (requestInstruct.size() > 0) {
    		if (hasStep) {
    			recipe.getInstructionSection(sectNdx).getInstructions().clear();
    			recipe.getInstructionSection(sectNdx).setInstructions(requestInstruct);
    		}
    		else
    			//if the user didn't fill in any steps need to remove the section;
    			//otherwise, a validation error is thrown when then come back to this section 
    			recipe.getInstructSections().remove(sectNdx);
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

    	String key = null;
    	String value = null;
    	RecipeIngredient recipeIngred = null;
    	Ingredient ingred = null;
    	int seq = 1;
    	int sectNdx = 0;
    	boolean forward = false;
    	boolean back = false;
    	boolean hasQty = false;
    	boolean hasName = false;
		
		//get the parameters
		ParameterMap requestParameters = context.getRequestParameters();
		//create a new empty list
		List<RecipeIngredient> ingredList = new AutoPopulatingList<RecipeIngredient>(RecipeIngredient.class);
	
		//get the parameters from the request and parse the set looking for ingredients
		Set<Entry<String, Object>> entries = requestParameters.asMap().entrySet();
		for (Entry<String, Object> entry : entries) {
			key = entry.getKey();
			value = requestParameters.get(key);
			logger.info("key/value= " + key + "/" + value);

    		if (key.contains("currIngredSection"))
				sectNdx = Integer.parseInt(value);
			if (key.contains("recipeIngredients")) {
				//ingredient.id signals a new ingredient
				if (key.contains("ingredient.id")) {
					recipeIngred = new RecipeIngredient();
					ingred = new Ingredient();
					recipeIngred.setIngredient(ingred);
					//add to list
					ingredList.add(recipeIngred);
					recipeIngred.setSequenceNo(seq++);
					int id = Integer.parseInt(value);					
					ingred.setId(id);					
				}
				if (key.contains("ingredient.name")) {
					ingred.setName(value);
					if (!value.isEmpty())
						hasName = true;
				}
				if (key.contains("quantity")) {
					recipeIngred.setQuantity(value);
					if (!value.isEmpty()) {
						Fraction fract = Fraction.getFraction(recipeIngred.getQuantity());
						float qty = fract.floatValue();
						recipeIngred.setQtyAmt(qty);
						hasQty = true;
					}
					else						
						recipeIngred.setQtyAmt(0);
				}
				if (key.contains("qtyType"))
					recipeIngred.setQtyType(value);
				if (key.contains("qualifier")) {
					recipeIngred.setQualifier(value);
				}	
			}
    		if (key.contains("_proceed"))
    			forward = true;
    		if (key.contains("_back"))
    			back = true;
		}

    	//replace the current list with the parsed list, if present
    	if (ingredList.size() > 0) {
    		if (hasQty && hasName) {
    			recipe.getIngredientSection(sectNdx).getRecipeIngredients().clear(); 
    			recipe.getIngredientSection(sectNdx).setRecipeIngredients(ingredList);
    		}
    		else
    			//if the user didn't fill in any ingredients need to remove the section;
    			//otherwise, a validation error is thrown when then come back to this section 
    			recipe.getIngredSections().remove(sectNdx);
    		
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
