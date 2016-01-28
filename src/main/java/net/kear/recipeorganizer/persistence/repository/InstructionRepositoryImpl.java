package net.kear.recipeorganizer.persistence.repository;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.repository.InstructionRepository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InstructionRepositoryImpl implements InstructionRepository {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    public void addInstruction(Instruction instruction) {
        getSession().save(instruction);
    }

    public void updateInstruction(Instruction instruction) {
    	getSession().merge(instruction);
    }
    
    @SuppressWarnings("unchecked")
    public List<Instruction> listInstruction() {
    	Criteria criteria = getSession().createCriteria(Instruction.class)
    		.addOrder(Order.asc("description"));
    	return criteria.list();
    }
 
    private Session getSession() {
		Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
}