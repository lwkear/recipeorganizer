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
import org.springframework.util.AutoPopulatingList;

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
    		.addOrder(Order.asc("name"));
    	List<Ingredient> ingredList = criteria.list();
    	List<Ingredient> ingredResults = new AutoPopulatingList<Ingredient>(Ingredient.class);

    	String str = searchStr.toLowerCase();
    	
		//loop through the results looking for ingredients that start with the searchStr;
    	for (Ingredient ingred : ingredList) {
			String lowerName = ingred.getName().toLowerCase();
			if (lowerName.startsWith(str))
				ingredResults.add(ingred);
			if (ingredResults.size() >= 20)
				break;
		}
			
		//loop again looking for other words in the ingredient that start with the searchStr and add them to the list
    	if (ingredResults.size() < 20) {
			for (Ingredient ingred : ingredList) {
				String lowerName = ingred.getName().toLowerCase();
				//split the ingredient into separate words, if any
				String[] splitStr = lowerName.split("\\s+");
				if (splitStr.length > 1) {
					for (int i=1;i<splitStr.length;i++) {
						if (splitStr[i].startsWith(str)) {
							ingredResults.add(ingred);
							break;
						}
					}
				}
				if (ingredResults.size() >= 20)
					break;
			}
		}
    	
    	return ingredResults;
    }

    private Session getSession() {
		Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
}