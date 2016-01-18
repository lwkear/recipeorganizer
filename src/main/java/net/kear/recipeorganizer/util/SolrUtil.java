package net.kear.recipeorganizer.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.kear.recipeorganizer.persistence.dto.SearchResultsDto;
import net.kear.recipeorganizer.persistence.model.IngredientSection;
import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.model.InstructionSection;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SolrUtil {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
    private static final String url = "http://localhost:8983/solr/recipe/";
    private static final HttpSolrClient solrCore = new HttpSolrClient(url);
	
	public ArrayList<SearchResultsDto> searchRecipes(String searchTerm) throws SolrServerException, IOException {
		
		QueryResponse rsp = null;
		
		SolrQuery query = new SolrQuery();
		query.setQuery(searchTerm);
		query.setParam("defType","edismax");
		query.setParam("qf", "name or catname or ingredname or description or source or notes");
		query.setParam("fl", "id, name, description, photo");
		query.setParam("start", "0");
		query.setParam("rows", "50");
		query.setHighlight(true);
		query.addHighlightField("name or description");
		query.setHighlightSimplePre("<strong>");
		query.setHighlightSimplePost("</strong>");
		query.addSort("name", SolrQuery.ORDER.asc);
	    
	    String qstr = ClientUtils.toQueryString(query, false);
	    logger.info("qstr: " + qstr);
	    
	    rsp = solrCore.query(query);
	    
	    ArrayList<SearchResultsDto> resultsList = new ArrayList<SearchResultsDto>();
	    
	    SolrDocumentList docs = rsp.getResults();
	    for (SolrDocument doc : docs) {
	    	String result = doc.toString();
	    	logger.info("doc: " + result);
	    	
	    	String idStr = (String)doc.getFieldValue("id");
	    	Long id = Long.valueOf(idStr);
	    	String name = (String)doc.getFieldValue("name");
	    	String desc = (String)doc.getFieldValue("description");
	    	String photo = (String)doc.getFieldValue("photo");

	    	if (rsp.getHighlighting().get(id) != null) {
	        	List<String> highList = rsp.getHighlighting().get(id).get("name");
	        	if (highList != null) {
		        	for (String highStr : highList) {
		        		logger.info("highStr: " + highStr);
		        		name = highStr;
		        	}
	        	}
	        	highList = rsp.getHighlighting().get(id).get("description");
	        	if (highList != null) {
		        	for (String highStr : highList) {
		        		logger.info("highStr: " + highStr);
		        		desc = highStr;
		        	}
	        	}
	        }
	    	
	    	SearchResultsDto rslts = new SearchResultsDto(id, name, desc, photo);
	    	resultsList.add(rslts);
	    }
	    
	    return resultsList;
	}
	
	public void addRecipe(Recipe recipe) throws SolrServerException, IOException {

		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", recipe.getId());
		document.addField("name", recipe.getName());
		document.addField("catname", recipe.getCategory().getName());
		document.addField("description", recipe.getDescription());
		if (!recipe.getServings().isEmpty())
			document.addField("servings", recipe.getServings());
		if (!recipe.getNotes().isEmpty())
			document.addField("notes", recipe.getNotes());
		if (!recipe.getBackground().isEmpty())
			document.addField("background", recipe.getBackground());
		if (!recipe.getPhotoName().isEmpty())
			document.addField("photo", recipe.getPhotoName());
		if (recipe.getSource() != null) {
			document.addField("sourcetype", recipe.getSource().getType());
			if (!recipe.getSource().getCookbook().isEmpty()) {
				document.addField("cookbook", recipe.getSource().getCookbook());
				document.addField("source", recipe.getSource().getCookbook());
			}
			if (!recipe.getSource().getCookbook().isEmpty()) {
				document.addField("magazine", recipe.getSource().getMagazine());
				document.addField("source", recipe.getSource().getMagazine());
			}
			if (!recipe.getSource().getCookbook().isEmpty()) {
				document.addField("newspaper", recipe.getSource().getNewspaper());
				document.addField("source", recipe.getSource().getNewspaper());
			}
			if (!recipe.getSource().getCookbook().isEmpty()) {
				document.addField("person", recipe.getSource().getPerson());
				document.addField("source", recipe.getSource().getPerson());
			}
			if (!recipe.getSource().getCookbook().isEmpty()) {
				document.addField("other", recipe.getSource().getOther());
				document.addField("source", recipe.getSource().getOther());
			}
			if (!recipe.getSource().getCookbook().isEmpty())
				document.addField("website", recipe.getSource().getWebsiteUrl());
		}
		for (InstructionSection section : recipe.getInstructSections()) {
			List<Instruction> instructs = section.getInstructions();
			for (Instruction instruct : instructs)
				document.addField("instructdesc", instruct.getDescription());
		}
		for (IngredientSection section : recipe.getIngredSections()) {
			List<RecipeIngredient> ingreds = section.getRecipeIngredients();
			for (RecipeIngredient recipeIngred : ingreds) {
				document.addField("ingredname", recipeIngred.getIngredient().getName());
			}			
		}

		solrCore.add(document);
		solrCore.commit();
}
	
	public void deleteRecipe(Long recipeId) throws SolrServerException, IOException {
		
		String idStr = recipeId.toString();
		
		solrCore.deleteById(idStr);
		solrCore.commit();
	}
}
