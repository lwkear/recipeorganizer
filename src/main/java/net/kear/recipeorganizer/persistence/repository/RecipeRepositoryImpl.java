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
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.model.Ingredient;
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
        	getSession().merge(recipe);
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
    	Hibernate.initialize(recipe.getInstructSections());
        Hibernate.initialize(recipe.getIngredSections());
        Hibernate.initialize(recipe.getSource());
        return recipe;
    }
    
    @SuppressWarnings("unchecked")
    public List<RecipeListDto> listRecipes() {
    	Criteria criteria = getSession().createCriteria(Recipe.class, "r")
    		.createAlias("category", "c")
    		.createAlias("user", "u")
    		.setProjection(Projections.projectionList()
    			.add(Projections.property("r.id").as("id"))
    			.add(Projections.property("u.id").as("userId"))
    			.add(Projections.property("u.firstName").as("firstName"))
    			.add(Projections.property("u.lastName").as("lastName"))
    			.add(Projections.property("r.name").as("name"))
    			.add(Projections.property("c.name").as("category"))
    			.add(Projections.property("r.allowShare").as("allowShare")))
    		.addOrder(Order.asc("name"))
    		.setResultTransformer(Transformers.aliasToBean(RecipeListDto.class));

    	List<RecipeListDto> recipes = (List<RecipeListDto>) criteria.list();
    	return recipes;
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
