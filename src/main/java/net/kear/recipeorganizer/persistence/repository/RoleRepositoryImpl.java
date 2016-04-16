package net.kear.recipeorganizer.persistence.repository;

import java.util.List;

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

    public Role getRole(Long id) {
    	Role role = (Role) getSession().get(Role.class, id);
    	return role;
    }
    
    public String getRoleName(Long id) {
    	Role role = (Role) getSession().load(Role.class, id);
    	return role.getName();
    }
    
    public Role getRole(String name) {
    	Criteria criteria = getSession().createCriteria(Role.class)
              	.add(Restrictions.eq("name", name));
       	Object result = criteria.uniqueResult();
       	if (result != null)
       		return (Role) result;
        	
       	return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<Role> getRoles() {
    	return getSession().createCriteria(Role.class).list();
    }
    
    private Session getSession() {
		Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
}
