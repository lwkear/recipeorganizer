package net.kear.recipeorganizer.persistence.repository;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Measure;
import net.kear.recipeorganizer.persistence.repository.MeasureRepository;

import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MeasureRepositoryImpl implements MeasureRepository {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    public void addMeasure(Measure measure) {
        sessionFactory.getCurrentSession().save(measure);
    }

    public void updateMeasure(Measure measure) {
        if (null != measure) {
            sessionFactory.getCurrentSession().merge(measure);
        }
    }
    
    public void deleteMeasure(Long id) {
    	Measure measure = (Measure) sessionFactory.getCurrentSession().load(Measure.class, id);
        if (null != measure) {
            sessionFactory.getCurrentSession().delete(measure);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Measure> listMeasure() {
    	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Measure.class)
    		.addOrder(Order.asc("description"));
    	return criteria.list();
    }
 }