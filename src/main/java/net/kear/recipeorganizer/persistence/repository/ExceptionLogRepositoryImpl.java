package net.kear.recipeorganizer.persistence.repository;
 
import java.math.BigDecimal;

import net.kear.recipeorganizer.persistence.model.ExceptionLog;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ExceptionLogRepositoryImpl implements ExceptionLogRepository {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    public void addException(ExceptionLog exceptionLog) {
        getSession().save(exceptionLog);
    }

    public Long getEventId() {
    	SQLQuery query = (SQLQuery) getSession().createSQLQuery(
    			"select EXCEPTION_EVENTID_SEQ.nextval from dual");
    	
    	BigDecimal id = (BigDecimal)query.uniqueResult(); 
    	return id.longValue();
    }
    
    private Session getSession() {
		Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
 }