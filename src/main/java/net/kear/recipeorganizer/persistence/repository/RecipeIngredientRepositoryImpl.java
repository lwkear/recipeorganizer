package net.kear.recipeorganizer.persistence.repository;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
import net.kear.recipeorganizer.persistence.repository.RecipeIngredientRepository;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RecipeIngredientRepositoryImpl implements RecipeIngredientRepository {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    public void addRecipeIngredient(RecipeIngredient recipeRecipeIngredient) {
        getSession().save(recipeRecipeIngredient);
    }

    public void updateRecipeIngredient(RecipeIngredient recipeRecipeIngredient) {
    	getSession().merge(recipeRecipeIngredient);
    }
    
    public void deleteRecipeIngredient(Long recipeIngredientId) {
    	RecipeIngredient recipeRecipeIngredient = (RecipeIngredient) getSession().load(RecipeIngredient.class, recipeIngredientId);
    	getSession().delete(recipeRecipeIngredient);
    }
    
    public void replaceIngredient(Long oldIngredientId, Long newIngredientId) {
    	SQLQuery query = (SQLQuery) getSession().createSQLQuery(
			"update recipe_ingredients set ingredient_id = :newId where ingredient_id = :oldId")
			.setLong("newId", newIngredientId)
    		.setLong("oldId", oldIngredientId);
   	
    	query.executeUpdate();		
    }
    
    @SuppressWarnings("unchecked")
    public List<RecipeIngredient> getRecipeIngredients(Long recipeID) {
    	Criteria criteria = getSession().createCriteria(RecipeIngredient.class)
    		.add(Restrictions.eq("recipeID", recipeID))
    		.addOrder(Order.asc("id"));
    	List<RecipeIngredient> result = criteria.list();
    	return result;
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getQualifiers(String searchStr) {
    	Criteria criteria = getSession().createCriteria(RecipeIngredient.class)
    		.add(Restrictions.ilike("qualifier", searchStr, MatchMode.ANYWHERE))
    		.setProjection(Projections.distinct(Projections.property("qualifier")));
    	List<String> result = criteria.list();
    	return result;
    }

    private Session getSession() {
    	Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
}