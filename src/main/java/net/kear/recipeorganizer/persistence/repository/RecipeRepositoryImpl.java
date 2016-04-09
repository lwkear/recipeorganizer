package net.kear.recipeorganizer.persistence.repository;
 
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.AutoPopulatingList;

import net.kear.recipeorganizer.enums.ApprovalAction;
import net.kear.recipeorganizer.enums.ApprovalStatus;
import net.kear.recipeorganizer.persistence.dto.RecipeDisplayDto;
import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.model.Favorites;
import net.kear.recipeorganizer.persistence.model.IngredientSection;
import net.kear.recipeorganizer.persistence.model.InstructionSection;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeMade;
import net.kear.recipeorganizer.persistence.model.RecipeNote;
import net.kear.recipeorganizer.persistence.repository.RecipeRepository;

@Repository
public class RecipeRepositoryImpl implements RecipeRepository {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    public void addRecipe(Recipe recipe) {
    	if (recipe.getViews() == null)
    		recipe.setViews(0);
    	if (recipe.getPhotoName() == null)
    		recipe.setPhotoName("");
    	getSession().save(recipe);
    }

    public void updateRecipe(Recipe recipe) {
    	if (recipe.getViews() == null)
    		recipe.setViews(0);
    	if (recipe.getPhotoName() == null)
    		recipe.setPhotoName("");
    	Session sess = getSession();
    	if (sess.contains(recipe))
    		sess.clear();
    	sess.merge(recipe);
    }
    
    public void deleteRecipe(Long id) {
    	Recipe recipe = (Recipe) getSession().load(Recipe.class, id);
    	getSession().delete(recipe);
    }

	public void approveRecipe(Long id, ApprovalAction action) {
    	SQLQuery query = (SQLQuery) getSession().createSQLQuery(
			"update recipe set status = :status where id = :id")
			.setInteger("status", action.ordinal())
			.setLong("id", id);
    	query.executeUpdate();		
	}

	public Recipe getRecipe(Long id) {
    	Recipe recipe = (Recipe) getSession().load(Recipe.class, id);
    	
    	//all of the collections are LAZY-loaded, so it's necessary to initialize each one
    	Hibernate.initialize(recipe.getIngredSections());
    	for (IngredientSection section : recipe.getIngredSections())
    		Hibernate.initialize(section.getRecipeIngredients());
    	
    	Hibernate.initialize(recipe.getInstructSections());
    	for (InstructionSection section : recipe.getInstructSections())
    		Hibernate.initialize(section.getInstructions());

    	recipe.setNumIngredSections(recipe.getIngredSections().size());
    	recipe.setNumInstructSections(recipe.getInstructSections().size());
    	
    	//removing these setters - they were causing the record to be updated in the database
    	/*if (recipe.getViews() == null)
    		recipe.setViews(0);
    	if (recipe.getPhotoName() == null)
    		recipe.setPhotoName("");*/
    	Hibernate.initialize(recipe.getSource());
    	Hibernate.initialize(recipe.getUser());    	
    	
        return recipe;
    }
	
	public Recipe loadRecipe(Long id) {
		Recipe recipe = (Recipe) getSession().load(Recipe.class, id);
		Hibernate.initialize(recipe.getSource());
		return recipe;
	}

    public void addFavorite(Favorites favorite) {
    	getSession().save(favorite);
    }
    
    public void removeFavorite(Favorites favorite) {
    	getSession().delete(favorite);
    }
    
    public boolean isFavorite(Long userId, Long recipeId) {
    	Criteria criteria = getSession().createCriteria(Favorites.class)
    		.add(Restrictions.eq("id.userId", userId))
    		.add(Restrictions.eq("id.recipeId", recipeId))
    		.setProjection(Projections.rowCount());
    	
    	Object result = criteria.uniqueResult();
    	Long count = (result == null ? 0L : (Long)result); 
    	return (count > 0 ? true : false);
    }

    public void updateRecipeMade(RecipeMade recipeMade) {
    	getSession().merge(recipeMade);
    }
    
    public RecipeMade getRecipeMade(Long userId, Long recipeId) {
    	Criteria criteria = getSession().createCriteria(RecipeMade.class)
       		.add(Restrictions.eq("id.userId", userId))
       		.add(Restrictions.eq("id.recipeId", recipeId));
       	RecipeMade recipeMade = (RecipeMade)criteria.uniqueResult();
       	if (recipeMade == null) {
       		recipeMade = new RecipeMade();
       		recipeMade.getId().setUserId(userId);
       		recipeMade.getId().setRecipeId(recipeId);
       		recipeMade.setMadeCount(0);
       		recipeMade.setLastMade(null);
       	}
       	
       	return recipeMade;
    }

    public void updateRecipeNote(RecipeNote recipeNote) {
    	getSession().merge(recipeNote);
    }
    
    public RecipeNote getRecipeNote(Long userId, Long recipeId) {
    	Criteria criteria = getSession().createCriteria(RecipeNote.class)
       		.add(Restrictions.eq("id.userId", userId))
       		.add(Restrictions.eq("id.recipeId", recipeId));
        RecipeNote recipeNote = (RecipeNote)criteria.uniqueResult();
       	if (recipeNote == null) {
       		recipeNote = new RecipeNote();
       		recipeNote.getId().setUserId(userId);
       		recipeNote.getId().setRecipeId(recipeId);
       	}
        return recipeNote;
    }
    
    public void addView(Recipe recipe) {
    	SQLQuery query = (SQLQuery) getSession().createSQLQuery(
			"update recipe set views = :num where id = :id")
			.setInteger("num", recipe.getViews())
			.setLong("id", recipe.getId());

    	query.executeUpdate();
    }
    
    public Long getRecipeViewCount(Long recipeId) {
    	Criteria criteria = getSession().createCriteria(Recipe.class)
    		.add(Restrictions.eq("id", recipeId))
    		.setProjection(Projections.projectionList()
    			.add(Projections.property("views")));
        
    	Object result = criteria.uniqueResult();
    	return (result == null ? 0L : (Long)result);
    }
    
    public Long getUserViewCount(Long userId) {
    	Criteria criteria = getSession().createCriteria(Recipe.class)
       		.add(Restrictions.eq("user.id", userId))
        	.setProjection(Projections.sum("views"));
        	
    	Object result = criteria.uniqueResult();
    	return (result == null ? 0L : (Long)result);
    }
    
    public Long getRequireApprovalCount() {
    	Criteria criteria = getSession().createCriteria(Recipe.class)
    		.add(Restrictions.in("status", ApprovalStatus.NOTREVIEWED, ApprovalStatus.PENDING, ApprovalStatus.BLOCKED))
    		.setProjection(Projections.rowCount());
        
    	Object result = criteria.uniqueResult();
    	return (result == null ? 0L : (Long)result);
    }
  
    @SuppressWarnings("unchecked")
    public List<Favorites> getFavorites(Long userId) {
    	Criteria criteria = getSession().createCriteria(Favorites.class)
    		.add(Restrictions.eq("id.userId", userId));

    	List<Favorites> favorites = (List<Favorites>) criteria.list();
    	return favorites;
    }
    
    @SuppressWarnings("unchecked")
    public List<RecipeListDto> approveRecipesList() {
    	Criteria criteria = getSession().createCriteria(Recipe.class, "r")
    		.createAlias("category", "c")
    		.createAlias("user", "u")    		
    		.createAlias("source", "s", JoinType.LEFT_OUTER_JOIN)
    		.add(Restrictions.in("status", ApprovalStatus.NOTREVIEWED, ApprovalStatus.PENDING, ApprovalStatus.BLOCKED))
    		.setProjection(Projections.projectionList()
    			.add(Projections.property("r.id").as("id"))
    			.add(Projections.property("r.name").as("name"))
    			.add(Projections.property("r.description").as("description"))
    			.add(Projections.property("r.dateAdded").as("submitted"))
    			.add(Projections.property("r.allowShare").as("allowShare"))
    			.add(Projections.property("r.status").as("status"))
    			.add(Projections.property("u.id").as("userId"))
    			.add(Projections.property("u.firstName").as("firstName"))
    			.add(Projections.property("u.lastName").as("lastName"))
    			.add(Projections.property("c.name").as("category"))
    			.add(Projections.property("s.type").as("sourcetype")))
    		.addOrder(Order.asc("name"))
    		.setResultTransformer(Transformers.aliasToBean(RecipeListDto.class));

    	List<RecipeListDto> recipes = (List<RecipeListDto>) criteria.list();
    	return recipes;
    }

    @SuppressWarnings("unchecked")
    public List<RecipeListDto> listRecipes(Long userId) {
    	Criteria criteria = getSession().createCriteria(Recipe.class, "r")
    		.createAlias("category", "c")
    		.createAlias("user", "u")    		
    		.createAlias("source", "s", JoinType.LEFT_OUTER_JOIN)
    		.add(Restrictions.eq("user.id", userId))
    		.setProjection(Projections.projectionList()
    			.add(Projections.property("r.id").as("id"))
    			.add(Projections.property("r.name").as("name"))
    			.add(Projections.property("r.description").as("description"))
    			.add(Projections.property("r.dateAdded").as("submitted"))
    			.add(Projections.property("r.allowShare").as("allowShare"))
    			.add(Projections.property("r.status").as("status"))
    			.add(Projections.property("u.id").as("userId"))
    			.add(Projections.property("u.firstName").as("firstName"))
    			.add(Projections.property("u.lastName").as("lastName"))
    			.add(Projections.property("c.name").as("category"))
    			.add(Projections.property("s.type").as("sourcetype")))
    		.addOrder(Order.asc("name"))
    		.setResultTransformer(Transformers.aliasToBean(RecipeListDto.class));

    	List<RecipeListDto> recipes = (List<RecipeListDto>) criteria.list();
    	return recipes;
    }

    @SuppressWarnings("unchecked")
    public List<RecipeDisplayDto> listRecipes(List<Long> ids) {
    	Criteria criteria = getSession().createCriteria(Recipe.class, "r")
    		.createAlias("user", "u")
    		.add(Restrictions.in("id", ids))
    		.setProjection(Projections.projectionList()
    			.add(Projections.property("r.id").as("id"))
    			.add(Projections.property("u.id").as("userId"))
    			.add(Projections.property("r.name").as("name"))
    			.add(Projections.property("r.description").as("description"))
    			.add(Projections.property("r.allowShare").as("allowShare"))
    			.add(Projections.property("r.status").as("status"))
    			.add(Projections.property("r.photoName").as("photo")))
    		.setResultTransformer(Transformers.aliasToBean(RecipeDisplayDto.class));

    	List<RecipeDisplayDto> recipes = (List<RecipeDisplayDto>) criteria.list();
    	List<RecipeDisplayDto> sorted = new AutoPopulatingList<RecipeDisplayDto>(RecipeDisplayDto.class);
    	for (Long id : ids) {
    		for (RecipeDisplayDto dto : recipes) {
    			if (id == dto.getId()) {
    				sorted.add(dto);
    				break;
    			}
    		}
    	}
    	
    	return sorted;
    }
    
    @SuppressWarnings("unchecked")
    public List<RecipeDisplayDto> recentRecipes(Long userId) {
    	Criteria criteria = getSession().createCriteria(Recipe.class, "r")
    		.createAlias("user", "u")
    		.add(Restrictions.eq("u.id", userId))
    		.setProjection(Projections.projectionList()
    			.add(Projections.property("r.id").as("id"))
    			.add(Projections.property("u.id").as("userId"))
    			.add(Projections.property("r.name").as("name"))
    			.add(Projections.property("r.description").as("description"))
    			.add(Projections.property("r.allowShare").as("allowShare"))
    			.add(Projections.property("r.status").as("status"))
    			.add(Projections.property("r.photoName").as("photo")))
    		.addOrder(Order.desc("r.dateAdded"))
    		.setMaxResults(5)
    		.setResultTransformer(Transformers.aliasToBean(RecipeDisplayDto.class));

    	List<RecipeDisplayDto> recipes = (List<RecipeDisplayDto>) criteria.list();
    	return recipes;
    }

    @SuppressWarnings("unchecked")
    public List<RecipeListDto> favoriteRecipes(List<Long> ids) {
    	Criteria criteria = getSession().createCriteria(Recipe.class, "r")
    		.createAlias("category", "c")
    		.createAlias("user", "u")
    		.createAlias("source", "s", JoinType.LEFT_OUTER_JOIN)
    		.add(Restrictions.in("r.id", ids))
    		.setProjection(Projections.projectionList()
    			.add(Projections.property("r.id").as("id"))
    			.add(Projections.property("r.name").as("name"))
    			.add(Projections.property("r.description").as("description"))
    			.add(Projections.property("r.dateAdded").as("submitted"))
    			.add(Projections.property("r.allowShare").as("allowShare"))
    			.add(Projections.property("r.status").as("status"))
    			.add(Projections.property("u.id").as("userId"))
    			.add(Projections.property("u.firstName").as("firstName"))
    			.add(Projections.property("u.lastName").as("lastName"))
    			.add(Projections.property("c.name").as("category"))
    			.add(Projections.property("s.type").as("sourcetype")))
    		.setResultTransformer(Transformers.aliasToBean(RecipeListDto.class));

    	List<RecipeListDto> recipes = (List<RecipeListDto>) criteria.list();
    	return recipes;
    }
    
    public Long getRecipeCount(Long userId) {
    	Criteria criteria = getSession().createCriteria(Recipe.class)
   			.add(Restrictions.eq("user.id", userId))
    		.setProjection(Projections.rowCount());
    	
    	Object result = criteria.uniqueResult();
    	return (result == null ? 0L : (Long)result);
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getTags(Long userId) {
    	SQLQuery query = (SQLQuery) getSession().createSQLQuery(
			"select distinct t.column_value as tag from recipe r, table(r.tags) t where r.user_id = :id")
			.addScalar("tag", StringType.INSTANCE)
			.setLong("id", userId);
    	
    	List<String> result = query.list();
    	return result;
    }

    @SuppressWarnings("unchecked")
    public boolean lookupName(String lookupName, Long userId) {
    	//check if the recipe name has already been entered by the user
    	Criteria criteria = getSession().createCriteria(Recipe.class)
    		.add(Restrictions.eq("name", lookupName).ignoreCase())
    		.add(Restrictions.eq("user.id", userId))
    		.setProjection(Projections.property("name"));
    	List<Recipe> result = criteria.list();
    	if (!result.isEmpty()) {
    		return true;
    	}
    	
    	return false;
    }
        
    private Session getSession() {
		Session sess = sessionFactory.getCurrentSession();
		if (sess == null) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}
}
