package net.kear.recipeorganizer.persistence.repository;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import net.kear.recipeorganizer.persistence.model.Role;
 
@Repository
public class RoleRepositoryImpl implements RoleRepository {
	
    @Autowired
    private SessionFactory sessionFactory;

    public String getRoleName(Long id) {
    	Role role = (Role) getSession().load(Role.class, id);
    	return role.getName();
    }
    
    public Role getRole(String name) {
    	Role role = (Role) getSession().load(Role.class, name);
    	return role;
    }
    
    public Role getDefaultRole()
    {
    	Criteria criteria = getSession().createCriteria(Role.class)
          	.add(Restrictions.eq("defaultRole", 1));
       	Object result = criteria.uniqueResult();
       	if (result != null)
       		return (Role) result;
        	
       	return null;
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
