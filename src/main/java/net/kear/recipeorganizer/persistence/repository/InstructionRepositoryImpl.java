package net.kear.recipeorganizer.persistence.repository;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.repository.InstructionRepository;

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
        sessionFactory.getCurrentSession().save(instruction);
    }

    public void updateInstruction(Instruction instruction) {
        if (null != instruction) {
            sessionFactory.getCurrentSession().merge(instruction);
        }
    }
    
    public void deleteInstruction(Long recipeID, int seqNo) {
    	/*Instructions instruction = (Instructions) sessionFactory.getCurrentSession().load(Instructions.class, recipeID, seqNo);
        if (null != instruction) {
            sessionFactory.getCurrentSession().delete(instruction);
        }*/
    }
    
    @SuppressWarnings("unchecked")
    public List<Instruction> listInstruction() {
    	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Instruction.class)
    		.addOrder(Order.asc("description"));
    	return criteria.list();
    }
 }