package net.kear.recipeorganizer.persistence.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import net.kear.recipeorganizer.persistence.model.UserProfile;

@Repository
public class UserProfileRepositoryImpl implements UserProfileRepository {

    @Autowired
    private SessionFactory sessionFactory;
	
	@Override
	public void addUserProfile(UserProfile userProfile) {
		getSession().save(userProfile);
	}

	public void updateUserProfile(UserProfile userProfile) {
		getSession().merge(userProfile);
	}
	
    private Session getSession() {
		Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
}
