package net.kear.recipeorganizer.persistence.repository;

import java.util.List;

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
	
	@SuppressWarnings("unchecked")
	public void deleteToken(long userId) {
		Criteria criteria = getSession().createCriteria(PasswordResetToken.class)
				.createAlias("user", "u")
	       		.add(Restrictions.eq("user.id", userId));
		List<PasswordResetToken> passwordTokens = (List<PasswordResetToken>) criteria.list();
	    if (!passwordTokens.isEmpty()) {
	    	for (PasswordResetToken token : passwordTokens) {
	    		getSession().delete(token);
	    	}
	    }
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
