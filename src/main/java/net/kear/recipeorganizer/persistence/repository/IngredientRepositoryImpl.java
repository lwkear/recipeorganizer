package net.kear.recipeorganizer.persistence.repository;
 
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.repository.IngredientRepository;

@Repository
public class IngredientRepositoryImpl implements IngredientRepository {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    public void addIngredient(Ingredient ingredient) {
    	//check to make sure the ingredient is not already in the DB - set the ID if it is.
    	Criteria criteria = getSession().createCriteria(Ingredient.class)
    		.add(Restrictions.ilike("name", ingredient.getName(), MatchMode.EXACT));
    	Ingredient ingred = (Ingredient)criteria.uniqueResult();
    	if (ingred != null) {
    		ingredient.setId(ingred.getId());
    		return;
    	}
    	
    	ingredient.setRank(99);
    	getSession().save(ingredient);
    }

    public void updateIngredient(Ingredient ingredient) {
    	getSession().merge(ingredient);
    }
    
    public void deleteIngredient(Long id) {
    	Ingredient ingredient = (Ingredient) getSession().load(Ingredient.class, id);
    	sessionFactory.getCurrentSession().delete(ingredient);
    }
    
    @SuppressWarnings("unchecked")
    public List<Ingredient> listIngredient() {
    	Criteria criteria = getSession().createCriteria(Ingredient.class)
    		.addOrder(Order.asc("name"));
    	return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<Ingredient> getIngredients(String searchStr) {
    	Criteria criteria = getSession().createCriteria(Ingredient.class)
    		.add(Restrictions.ilike("name", searchStr, MatchMode.ANYWHERE))
    		.add(Restrictions.gt("rank", 1))
    		.addOrder(Order.asc("name"));    	
    	return criteria.list();
    }

    private Session getSession() {
		Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
}