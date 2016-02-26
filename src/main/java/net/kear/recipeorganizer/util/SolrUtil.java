package net.kear.recipeorganizer.util;

import java.io.IOException;
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Recipe;

import org.apache.solr.client.solrj.SolrServerException;

public interface SolrUtil {

	public abstract void setUrl(String url);
	public abstract void setCore();
	public abstract List<List<Object>> searchRecipes(String searchTerm) throws SolrServerException, IOException;
	public abstract void addRecipe(Recipe recipe);
	public abstract void deleteRecipe(Long recipeId);

}