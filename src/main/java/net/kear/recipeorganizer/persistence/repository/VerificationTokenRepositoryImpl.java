package net.kear.recipeorganizer.persistence.repository;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import net.kear.recipeorganizer.persistence.model.VerificationToken;

@Repository
public class VerificationTokenRepositoryImpl implements VerificationTokenRepository {

	@Autowired
	private SessionFactory sessionFactory;	
	
	@Override
	public void saveToken(VerificationToken token) {
    	getSession().save(token);
	}
	
	public VerificationToken findByToken(String token) {
		Criteria criteria = getSession().createCriteria(VerificationToken.class)
	       		.add(Restrictions.eq("token", token).ignoreCase());
	    Object result = criteria.uniqueResult();
	    if (result != null)
	    	return (VerificationToken) result;
	    	
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
