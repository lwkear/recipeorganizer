package net.kear.recipeorganizer.persistence.repository;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import net.kear.recipeorganizer.persistence.dto.UserMessageDto;
import net.kear.recipeorganizer.persistence.model.UserMessage;

@Repository
public class UserMessageRepositoryImpl implements UserMessageRepository {

    @Autowired
    private SessionFactory sessionFactory;
	
	@Override
	public void addMessage(UserMessage message) {
		getSession().save(message);
	}

	@Override
	public void updateMessage(UserMessage message) {
		getSession().merge(message);
	}

	@Override
	public void deleteMessage(long id) {
		UserMessage message = (UserMessage) getSession().load(UserMessage.class, id);
		getSession().delete(message);
	}

	@Override
	public void setViewed(long id) {
    	SQLQuery query = (SQLQuery) getSession().createSQLQuery(
			"update message set viewed = true where id = :id")
			.setLong("id", id);
   	
    	query.executeUpdate();
	}
	
	@Override
	public void setUserViewed(long userId) {
    	SQLQuery query = (SQLQuery) getSession().createSQLQuery(
			"update message set viewed = true where to_user_id = :id")
			.setLong("id", userId);
   	
    	query.executeUpdate();		
	}

	@Override
    @SuppressWarnings("unchecked")
	public List<UserMessageDto> listMessages(long toUserId) {
    	SQLQuery query = (SQLQuery) getSession().createSQLQuery(
			"select m.id as id, m.to_user_id as toUserId, m.from_user_id as fromUserId, u.firstname as fromFirstName, u.lastname as fromLastName, u.email as fromEmail,"
					+ " m.subject as subject, m.message as message, m.html_message as htmlMessage, m.viewed as viewed, m.recipe_id as recipeId, r.name as recipeName, "
					+ " m.date_sent as dateSent from message m left outer join users u on m.from_user_id = u.id left outer join recipe r on m.recipe_id = r.id"
					+ " where m.to_user_id = :toId"
					+ " order by dateSent desc")
			.addScalar("id",StandardBasicTypes.LONG)
			.addScalar("toUserId",StandardBasicTypes.LONG)
			.addScalar("fromUserId",StandardBasicTypes.LONG)
			.addScalar("fromFirstName",StandardBasicTypes.STRING)
			.addScalar("fromLastName",StandardBasicTypes.STRING)
			.addScalar("fromEmail",StandardBasicTypes.STRING)
			.addScalar("subject",StandardBasicTypes.STRING)
			.addScalar("message",StandardBasicTypes.STRING)
			.addScalar("htmlMessage",StandardBasicTypes.STRING)
			.addScalar("viewed",StandardBasicTypes.BOOLEAN)			
			.addScalar("recipeId",StandardBasicTypes.LONG)
			.addScalar("recipeName",StandardBasicTypes.STRING)
			.addScalar("dateSent",StandardBasicTypes.TIMESTAMP)			
			.setLong("toId", toUserId)
			.setResultTransformer(Transformers.aliasToBean(UserMessageDto.class));
    	    	
    	List<UserMessageDto> messages = (List<UserMessageDto>) query.list();
    	return messages;
	}

	@Override
	public long getMessageCount(long toUserId) {
    	Criteria criteria = getSession().createCriteria(UserMessage.class)
    		.add(Restrictions.eq("toUserId", toUserId))
   			.setProjection(Projections.rowCount());
           	
    	Object result = criteria.uniqueResult();
    	return (result == null ? 0L : (Long)result);
	}

	@Override
	public long getNotViewedCount(long toUserId) {
    	Criteria criteria = getSession().createCriteria(UserMessage.class)
    		.add(Restrictions.eq("toUserId", toUserId))
    		.add(Restrictions.eq("viewed", false))
   			.setProjection(Projections.rowCount());
           	
    	Object result = criteria.uniqueResult();
    	return (result == null ? 0L : (Long)result);
	}

    private Session getSession() {
		Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
}
