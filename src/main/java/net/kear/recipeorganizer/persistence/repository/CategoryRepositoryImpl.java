package net.kear.recipeorganizer.persistence.repository;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.repository.CategoryRepository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    public void addCategory(Category category) {
        getSession().save(category);
    }

    public void updateCategory(Category category) {
        if (null != category) {
            getSession().merge(category);
        }
    }
    
    public void deleteCategory(Long id) {
    	Category category = (Category) getSession().load(Category.class, id);
        if (null != category) {
            getSession().delete(category);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Category> listCategory() {
    	Criteria criteria = getSession().createCriteria(Category.class)
    		.addOrder(Order.asc("name"));
    	return criteria.list();
    }
    
    public String getCategoryName(Long id) {
    	Category category = (Category) getSession().load(Category.class, id);
        if (null != category) {
            return category.getName();
        }
        else
        	return null;
    }
    
    public Category getCategory(Long id) {
    	Category category = (Category) getSession().get(Category.class, id);
    	return category;
    }
  
    private Session getSession() {
		Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
 }