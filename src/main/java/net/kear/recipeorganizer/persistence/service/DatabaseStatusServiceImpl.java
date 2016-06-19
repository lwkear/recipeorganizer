package net.kear.recipeorganizer.persistence.service;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseStatusServiceImpl implements DatabaseStatusService {

    @Autowired
    private SessionFactory sessionFactory;

    private static volatile boolean databaseAccessible = true;
    
	public DatabaseStatusServiceImpl() {}

	@Override
	@Transactional
	public void checkStatus() {
    	SQLQuery query = (SQLQuery) getSession().createSQLQuery(
			"select 1");
    	query.uniqueResult();
    }
	
	public boolean isDatabaseAccessible() {
		return databaseAccessible;
	}
	
	public void setDatabaseStatus(boolean status) {
		databaseAccessible = status;
	}
    
	private Session getSession() {
    	Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
}
