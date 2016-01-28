package net.kear.recipeorganizer.persistence.repository;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.repository.UserRepository;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    public void addUser(User user) {
    	getSession().save(user);
    }

    public void updateUser(User user) {
       	getSession().merge(user);
    }
    
    public void deleteUser(Long id) {
    	User user = (User) getSession().load(User.class, id);
       	getSession().delete(user);
    }
    
    public User findUserByEmail(String email) {
    	Criteria criteria = getSession().createCriteria(User.class)
       		.add(Restrictions.eq("email", email).ignoreCase());
    	Object result = criteria.uniqueResult();
    	if (result != null)
    		return (User) result;
    	
    	return null;
    }
    
    public boolean doesUserEmailExist(String email) {
    	Criteria criteria = getSession().createCriteria(User.class)
           		.add(Restrictions.eq("email", email).ignoreCase())
           		.setProjection(Projections.projectionList()
           			.add(Projections.property("email")));
       	String userEmail = (String) criteria.uniqueResult();
       	if (userEmail == null || userEmail.isEmpty())
       		return false;
       	return true;
    }
        
    @SuppressWarnings("unchecked")
    public boolean validateUser(String email, String password) {
    	Criteria criteria = getSession().createCriteria(User.class)
    		.add(Restrictions.eq("email", email))
    		.add(Restrictions.eq("password", password));
    	List<User> result = criteria.list();
    	return !result.isEmpty();    	
    }

    @SuppressWarnings("unchecked")
    public List<User> getUsers() {
    	return getSession().createCriteria(User.class).list();
    }
    
    public String getUserFullName(Long id) {
    	User user = (User) getSession().load(User.class, id);
        if (null != user) {
        	return user.getFirstName() + " " + user.getLastName();
        }
        else
        	return null;
    }
    
    public User getUser(Long id) {
    	User user = (User) getSession().get(User.class, id);
    	return user;
    }
    
    public User getUserWithProfile(Long id) {
    	User user = (User) getSession().get(User.class, id);
    	Hibernate.initialize(user.getUserProfile());
    	return user;
    }
    
    private Session getSession() {
		Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
}