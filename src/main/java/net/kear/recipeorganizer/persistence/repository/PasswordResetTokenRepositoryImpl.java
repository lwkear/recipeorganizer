package net.kear.recipeorganizer.persistence.repository;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import net.kear.recipeorganizer.persistence.model.PasswordResetToken;

@Repository
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepository {

	@Autowired
	private SessionFactory sessionFactory;	
	
	public PasswordResetTokenRepositoryImpl() {}

	@Override
	public void saveToken(PasswordResetToken token) {
    	getSession().save(token);
	}

	@Override
	public PasswordResetToken findByToken(String token) {
		Criteria criteria = getSession().createCriteria(PasswordResetToken.class)
	       		.add(Restrictions.eq("token", token).ignoreCase());
	    Object result = criteria.uniqueResult();
	    if (result != null)
	    	return (PasswordResetToken) result;
	    	
	    return null;
	}

    private Session getSession() {
		Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
}
