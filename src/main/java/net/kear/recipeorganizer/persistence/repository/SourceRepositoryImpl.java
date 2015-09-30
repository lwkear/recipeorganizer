package net.kear.recipeorganizer.persistence.repository;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import net.kear.recipeorganizer.persistence.model.Source;
import net.kear.recipeorganizer.persistence.repository.SourceRepository;

@Repository
public class SourceRepositoryImpl implements SourceRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public void addSource(Source source) {
    	getSession().save(source);
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getSources(String searchStr, String type) {
    	String field = "";
    	if (type.equals("Cookbook"))
    		field = "cookbook";
    	else
    	if (type.equals("Magazine"))
    		field = "magazine";
    	else
    	if (type.equals("Newspaper"))
    		field = "newspaper";
    	else
    	if (type.equals("Person"))
    		field = "person";
    	else
    	if (type.equals("Website"))
    		field = "websiteUrl";
    	else
    		return null;
    		
    	Criteria criteria = getSession().createCriteria(Source.class)
    		.add(Restrictions.ilike(field, searchStr, MatchMode.ANYWHERE))
    		.setProjection(Projections.distinct(Projections.property(field).as("source")))
    		.addOrder(Order.asc("source"));
    	
    	List<String> result = criteria.list();
    	return result;
	}

	private Session getSession() {
		Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
}
