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
        if (null != recipeComment) {
            getSession().delete(recipeComment);
        }
	}

	@SuppressWarnings("unchecked")
	public List<CommentDto> listComments(long recipeId) {
    	SQLQuery query = (SQLQuery) getSession().createSQLQuery(
			"select c.id as id, u.firstname as firstName, u.lastname as lastName, p.avatar as avatar, c.user_comment as userComment,"
				+ " c.date_added as dateAdded from recipe_comments c, users u, user_profile p"
				+ " where c.recipe_id = :id and u.id = c.user_id and p.user_id = u.id order by c.date_added desc")
			.addScalar("id",StandardBasicTypes.LONG)
			.addScalar("firstName",StandardBasicTypes.STRING)
			.addScalar("lastName",StandardBasicTypes.STRING)
			.addScalar("avatar",StandardBasicTypes.STRING)
			.addScalar("userComment",StandardBasicTypes.STRING)
			.addScalar("dateAdded",StandardBasicTypes.TIMESTAMP)
			.setLong("id", recipeId)
			.setResultTransformer(Transformers.aliasToBean(CommentDto.class));
    	
    	List<CommentDto> comments = (List<CommentDto>) query.list();
       	return comments;
	}

	public long getCommentCount(long recipeId) {
    	Criteria criteria = getSession().createCriteria(RecipeComment.class)
    		.add(Restrictions.eq("recipeId", recipeId))
   			.setProjection(Projections.rowCount());
       	return (Long)criteria.uniqueResult();
	}

    private Session getSession() {
		Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
}
