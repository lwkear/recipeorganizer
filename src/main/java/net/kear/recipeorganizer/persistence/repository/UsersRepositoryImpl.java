package net.kear.recipeorganizer.persistence.repository;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Users;
import net.kear.recipeorganizer.persistence.repository.UsersRepository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UsersRepositoryImpl implements UsersRepository {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    public void addUser(Users user) {
    	
    	getSession().save(user);
    }

    public void updateUser(Users user) {
    	
        if (null != user) {
        	getSession().merge(user);
        }
    }
    
    public void deleteUser(Long id) {
    	
    	Users user = (Users) getSession().load(Users.class, id);
        if (null != user) {
        	getSession().delete(user);
        }
    }
    
    public Users findUserByEmail(String email) {
    
    	Criteria criteria = getSession().createCriteria(Users.class)
       		.add(Restrictions.eq("email", email).ignoreCase());
    	Object result = criteria.uniqueResult();
    	if (result != null)
    		return (Users) result;
    	
    	return null;
    }
    
    public boolean doesUserEmailExist(String email) {

    	Criteria criteria = getSession().createCriteria(Users.class)
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

    	Criteria criteria = getSession().createCriteria(Users.class)
    		.add(Restrictions.eq("email", email))
    		.add(Restrictions.eq("password", password));
    	List<Users> result = criteria.list();
    	return !result.isEmpty();    	
    }

    @SuppressWarnings("unchecked")
    public List<Users> listUsers() {
    	
    	return getSession().createCriteria(Users.class).list();
    }
    
    public String getUserName(Long id) {
    	
    	Users user = (Users) getSession().load(Users.class, id);
        if (null != user) {
        	return user.getFirstName() + " " + user.getLastName();
        }
        else
        	return null;
    }
    
    public Users getUser(Long id) {
    	Users user = (Users) getSession().get(Users.class, id);
    	return user;
    }
 
	private Session getSession() {
		
		Session sess = getSessionFactory().getCurrentSession();
		if (sess == null) {
			sess = getSessionFactory().openSession();
		}
		return sess;
	}

	private SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}