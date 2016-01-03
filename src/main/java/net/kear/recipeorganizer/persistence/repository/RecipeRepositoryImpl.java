package net.kear.recipeorganizer.persistence.repository;
 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.AutoPopulatingList;

import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.dto.SearchResultsDto;
import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.model.IngredientSection;
import net.kear.recipeorganizer.persistence.model.InstructionSection;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
import net.kear.recipeorganizer.persistence.repository.RecipeRepository;

@Repository
public class RecipeRepositoryImpl implements RecipeRepository {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    public void addRecipe(Recipe recipe) {
    	getSession().save(recipe);
    }

    public void updateRecipe(Recipe recipe) {
        if (recipe != null) {
        	Session sess = getSession();
        	if (sess.contains(recipe))
        		sess.clear();
        	sess.merge(recipe);
        }
    }
    
    public void deleteRecipe(Long id) {
    	Recipe recipe = (Recipe) getSession().load(Recipe.class, id);
        if (recipe != null) {
        	getSession().delete(recipe);
        }
    }

    public Recipe getRecipe(Long id) {
    	Recipe recipe = (Recipe) getSession().load(Recipe.class, id);
    	
    	//all of the collections are LAZY-loaded, so it's necessary to initialize each one
    	Hibernate.initialize(recipe.getIngredSections());
    	List<IngredientSection> ingredSections = recipe.getIngredSections();
    	for (IngredientSection section : ingredSections)
    		Hibernate.initialize(section.getRecipeIngredients());
    	
    	Hibernate.initialize(recipe.getInstructSections());
    	/*List<InstructionSection> instructSections = recipe.getInstructSections();*/
    	for (InstructionSection section : recipe.getInstructSections())
    		Hibernate.initialize(section.getInstructions());
    	
    	Hibernate.initialize(recipe.getSource());
    	
    	recipe.setNumIngredSections(recipe.getIngredSections().size());
    	recipe.setNumInstructSections(recipe.getInstructSections().size());
        return recipe;
    }
    
    @SuppressWarnings("unchecked")
    public List<RecipeListDto> listRecipes(Long userId) {
    	Criteria criteria = getSession().createCriteria(Recipe.class, "r")
    		.createAlias("category", "c")
    		.createAlias("source", "s", JoinType.LEFT_OUTER_JOIN)
    		.add(Restrictions.eq("user.id", userId))
    		.setProjection(Projections.projectionList()
    			.add(Projections.property("r.id").as("id"))
    			.add(Projections.property("r.name").as("name"))
    			.add(Projections.property("r.description").as("desc"))
    			.add(Projections.property("r.dateAdded").as("submitted"))
    			.add(Projections.property("c.name").as("category"))
    			.add(Projections.property("s.type").as("sourcetype")))
    		.addOrder(Order.asc("name"))
    		.setResultTransformer(Transformers.aliasToBean(RecipeListDto.class));

    	List<RecipeListDto> recipes = (List<RecipeListDto>) criteria.list();
    	return recipes;
    }

    @SuppressWarnings("unchecked")
    public List<SearchResultsDto> listRecipes(List<Long> ids) {
    	Criteria criteria = getSession().createCriteria(Recipe.class, "r")
    		.add(Restrictions.in("id", ids))
    		.setProjection(Projections.projectionList()
    			.add(Projections.property("r.id").as("id"))
    			.add(Projections.property("r.name").as("name"))
    			.add(Projections.property("r.description").as("description"))
    			.add(Projections.property("r.photoName").as("photo")))
    		.setResultTransformer(Transformers.aliasToBean(SearchResultsDto.class));

    	List<SearchResultsDto> recipes = (List<SearchResultsDto>) criteria.list();
    	return recipes;
    }
    
    public Long getRecipeCount(Long userId) {
    	Criteria criteria = getSession().createCriteria(Recipe.class)
   			.add(Restrictions.eq("user.id", userId))
    		.setProjection(Projections.rowCount());
    	return (Long)criteria.uniqueResult();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Ingredient> getIngredients(Recipe recipe, int sectionNdx) {

    	List<Ingredient> ingreds = new ArrayList();
    	
    	int size = recipe.getIngredSections().size();
    	if (size > sectionNdx) {

	    	Iterator<RecipeIngredient> iter = recipe.getIngredientSection(sectionNdx).getRecipeIngredients().iterator();
			while (iter.hasNext()) {
				RecipeIngredient recipeIngred = iter.next();
		    	Criteria criteria = getSession().createCriteria(Ingredient.class)
		    		.add(Restrictions.eq("id", recipeIngred.getIngredientId()));
				ingreds.add((Ingredient)criteria.uniqueResult());
			}
    	}
		
		return ingreds;
    }
    
    public void getAllIngredients(Recipe recipe) {
    	List<IngredientSection> sections = recipe.getIngredSections();
    	for (IngredientSection section : sections) {
    		List<RecipeIngredient> ingreds = section.getRecipeIngredients();
    		for (RecipeIngredient recipeIngred : ingreds) {
	    		Criteria criteria = getSession().createCriteria(Ingredient.class)
			    		.add(Restrictions.eq("id", recipeIngred.getIngredientId()));
	    		recipeIngred.setIngredient((Ingredient)criteria.uniqueResult());
    		}
    	}	
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getTags(String searchStr, Long userId) {
    	SQLQuery query = (SQLQuery) getSession().createSQLQuery(
			"select distinct t.column_value as tag from recipe r, table(r.tags) t where r.user_id = :id and lower(t.column_value) like :str")
			.addScalar("tag", StringType.INSTANCE)
			.setLong("id", userId)
			.setString("str", "%" + searchStr + "%");
    	
    	List<String> result = query.list();
    	return result;
    }

    @SuppressWarnings("unchecked")
    public boolean lookupName(String lookupName, Long userId) {
    	//check if the recipe name has already been entered by the user
    	Criteria criteria = getSession().createCriteria(Recipe.class)
    		.add(Restrictions.eq("name", lookupName).ignoreCase())
    		.add(Restrictions.eq("user.id", userId))
    		.setProjection(Projections.property("name"));
    	List<Recipe> result = criteria.list();
    	if (!result.isEmpty()) {
    		return true;
    	}
    	
    	return false;
    }
        
    private Session getSession() {
		Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}

}
