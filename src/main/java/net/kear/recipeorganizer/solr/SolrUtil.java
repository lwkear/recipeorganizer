package net.kear.recipeorganizer.solr;

import java.io.IOException;
import java.util.List;

import net.kear.recipeorganizer.event.UpdateSolrRecipeEvent;
import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
//import net.kear.recipeorganizer.persistence.model.Recipe;


import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

public interface SolrUtil {

	public abstract void setUrl(String url);
	public abstract void setCore();
	public abstract List<List<Object>> searchRecipes(String searchTerm, long userId) throws SolrServerException, IOException;
	public abstract List<RecipeListDto> searchIngredients(String searchTerm) throws SolrServerException, IOException;
	@Async
	@TransactionalEventListener(fallbackExecution = true)
	public abstract void handleUpdateSolrRecipeEvent(UpdateSolrRecipeEvent updateSolrRecipeEvent);
}