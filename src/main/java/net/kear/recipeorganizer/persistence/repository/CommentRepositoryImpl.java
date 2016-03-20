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

import net.kear.recipeorganizer.persistence.dto.CommentDto;
import net.kear.recipeorganizer.persistence.dto.FlaggedCommentDto;
import net.kear.recipeorganizer.persistence.model.RecipeComment;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    @Autowired
    private SessionFactory sessionFactory;
	
	@Override
	public void addComment(RecipeComment recipeComment) {
		getSession().save(recipeComment);
	}

	@Override
	public void deleteComment(long id) {
		RecipeComment recipeComment = (RecipeComment) getSession().load(RecipeComment.class, id);
		getSession().delete(recipeComment);
	}

	@SuppressWarnings("unchecked")
	public List<CommentDto> listComments(long recipeId) {
    	SQLQuery query = (SQLQuery) getSession().createSQLQuery(
			"select c.id as id, u.id as userId, u.firstname as firstName, u.lastname as lastName, p.avatar as avatar, c.user_comment as userComment,"
				+ " c.date_added as dateAdded, c.flag as flag from recipe_comments c, users u, user_profile p"
				+ " where c.recipe_id = :id and u.id = c.user_id and p.user_id = u.id order by c.date_added desc")
			.addScalar("id",StandardBasicTypes.LONG)
			.addScalar("userId",StandardBasicTypes.LONG)
			.addScalar("firstName",StandardBasicTypes.STRING)
			.addScalar("lastName",StandardBasicTypes.STRING)
			.addScalar("avatar",StandardBasicTypes.STRING)
			.addScalar("userComment",StandardBasicTypes.STRING)
			.addScalar("dateAdded",StandardBasicTypes.TIMESTAMP)
			.addScalar("flag",StandardBasicTypes.INTEGER)
			.setLong("id", recipeId)
			.setResultTransformer(Transformers.aliasToBean(CommentDto.class));

    	List<CommentDto> comments = (List<CommentDto>) query.list();
       	return comments;
	}

	public long getCommentCount(long recipeId) {
    	Criteria criteria = getSession().createCriteria(RecipeComment.class)
    		.add(Restrictions.eq("recipeId", recipeId))
   			.setProjection(Projections.rowCount());
       	
    	Object result = criteria.uniqueResult();
    	return (result == null ? 0L : (Long)result);
	}
	
	public void setCommentFlag(long id, int flag) {
    	SQLQuery query = (SQLQuery) getSession().createSQLQuery(
			"update recipe_comments set flag = :flag where id = :id")
			.setInteger("flag", flag)
			.setLong("id", id);
   	
    	query.executeUpdate();		
	}
	
    @SuppressWarnings("unchecked")
    public List<FlaggedCommentDto> getFlaggedComments() {
    	SQLQuery query = (SQLQuery) getSession().createSQLQuery(
    			"select c.id as id, u.id as userId, r.id as recipeId, r.name as recipeName, c.user_comment as userComment,"
    				+ " c.date_added as dateAdded, u.firstname as firstName, u.lastname as lastName"
    				+ " from recipe_comments c, users u, recipe r"
    				+ " where r.id = c.recipe_id and u.id = c.user_id and c.flag = 1"
    				+ " order by c.date_added asc")
    			.addScalar("id",StandardBasicTypes.LONG)
    			.addScalar("userId",StandardBasicTypes.LONG)
    			.addScalar("recipeId",StandardBasicTypes.LONG)
    			.addScalar("recipeName",StandardBasicTypes.STRING)
    			.addScalar("userComment",StandardBasicTypes.STRING)
    			.addScalar("dateAdded",StandardBasicTypes.TIMESTAMP)
    			.addScalar("firstName",StandardBasicTypes.STRING)
    			.addScalar("lastName",StandardBasicTypes.STRING)    			
    			.setResultTransformer(Transformers.aliasToBean(FlaggedCommentDto.class));
    	
    	List<FlaggedCommentDto> comments = (List<FlaggedCommentDto>) query.list();
    	return comments;
    }
    
    public Long getFlaggedCount() {
    	Criteria criteria = getSession().createCriteria(RecipeComment.class)
    		.add(Restrictions.eq("flag", 1))
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
