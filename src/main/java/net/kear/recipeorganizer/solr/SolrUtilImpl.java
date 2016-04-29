package net.kear.recipeorganizer.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.kear.recipeorganizer.enums.ApprovalStatus;
import net.kear.recipeorganizer.enums.SourceType;
import net.kear.recipeorganizer.event.UpdateSolrRecipeEvent;
import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.dto.SearchResultsDto;
import net.kear.recipeorganizer.persistence.model.IngredientSection;
import net.kear.recipeorganizer.persistence.model.Instruction;
import net.kear.recipeorganizer.persistence.model.InstructionSection;
import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.model.RecipeIngredient;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class SolrUtilImpl implements SolrUtil {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ExceptionLogService logService;
	
	private String url;
    private HttpSolrClient solrCore;
    
    public SolrUtilImpl() {}
    
    @Override
	public void setUrl(String url) {
    	this.url = url;
    }

    @Override
	public void setCore() {
    	this.solrCore = new HttpSolrClient(this.url);
    }
    
	@Override
	public List<List<Object>> searchRecipes(String searchTerm, long userId) throws SolrServerException, IOException {
		logger.info("searchRecipes: qstr=" + searchTerm);
		
		QueryResponse rsp = null;
		String filterStr = "";
		
		int approved = ApprovalStatus.APPROVED.getValue();
		
		if (userId > 0)
			filterStr = String.format("userid:%d || (*:* && !userid:%d && allowshare:true && status:%d)", userId, userId, approved);
		else
			filterStr = String.format("allowshare:true && approved:%d", approved);
		logger.debug("filterStr: " + filterStr);
		
		//Note: the default sort is by score, so no need to explicitly identify score as the sort
		SolrQuery query = new SolrQuery();
		query.setQuery(searchTerm);
		query.setParam("defType","edismax");
		query.setParam("qf", "name^2 or ingredname or description^1 or background or source or notes or tags");
		query.setParam("fl", "id, userid, name, description, photo, allowshare, status, score, catid, source, sourcetype");
		query.addFilterQuery(filterStr);
		query.setParam("start", "0");
		query.setParam("rows", "100");
		query.setHighlight(true);
		query.addHighlightField("name or description or ingredname or background or source or notes or tags");
		query.setHighlightSimplePre("<strong>");
		query.setHighlightSimplePost("</strong>");
		query.setFacet(true);
		query.addFacetField("catid");
		query.addFacetField("sourcetype");
	    
	    rsp = solrCore.query(query);

	    ArrayList<SearchResultsDto> resultsList = new ArrayList<SearchResultsDto>();
	    
	    //rank will be used in the datatable to order the results; can't use score since it's a decimal number
	    int rank = 1;
	    SolrDocumentList docs = rsp.getResults();
	    for (SolrDocument doc : docs) {
	    	String result = doc.toString();
	    	logger.debug("doc: " + result);
	    	
	    	String idStr = (String)doc.getFieldValue("id");
	    	Long id = Long.valueOf(idStr);
	    	Long uId = (Long)doc.getFieldValue("userid");
	    	String name = (String)doc.getFieldValue("name");
	    	String desc = (String)doc.getFieldValue("description");
	    	String photo = (String)doc.getFieldValue("photo");
	    	boolean allowShare = (boolean)doc.getFieldValue("allowshare");
	    	int stat = (Integer)doc.getFieldValue("status");
	    	Long catId = (Long)doc.getFieldValue("catid");
	    	String source = (String)doc.getFieldValue("source");
	    	String srcType = (String)doc.getFieldValue("sourcetype");
	    	SourceType sourceType = SourceType.NONE;
	    	if (!StringUtils.isBlank(srcType))
	    		sourceType = SourceType.valueOf(srcType);
	    	
	    	boolean addResult = true;

	    	if (rsp.getHighlighting().containsKey(idStr)) {
	    		Map<String, List<String>> highMap = rsp.getHighlighting().get(idStr);
	        	if (highMap.containsKey("name")) {
	        		List<String> highList = highMap.get("name");
		        	for (String highStr : highList) {
		        		logger.debug("highStr: " + highStr);
		        		name = highStr;
		        	}
	        	}
	        	if (highMap.containsKey("description")) {
	        		List<String> highList = highMap.get("description");
		        	for (String highStr : highList) {
		        		logger.debug("highStr: " + highStr);
		        		desc = highStr;
		        	}
	        	}
	        	if (highMap.containsKey("tags")) {
	        		//if the only match was a tag but the recipe doesn't belong to the user, don't add it to the results
	        		if (highMap.size() == 1 && uId != userId)
	        			addResult = false;
	        	}
	        }
	    	
	    	if (addResult) {
	    		ApprovalStatus status = ApprovalStatus.values()[stat];
	    		SearchResultsDto rslts = new SearchResultsDto(rank++, id, uId, name, desc, photo, allowShare, status, catId, source, sourceType);
	    		resultsList.add(rslts);
	    	}
	    }
	    
	    ArrayList<FacetField> facets = new ArrayList<FacetField>();
	    
	    if (!resultsList.isEmpty()) {
	    	List<FacetField> facetFields = rsp.getFacetFields();
	    	for (FacetField field : facetFields) {
	    		facets.add(field);	    		
	    	}
	    }
	    
	    //Note: need to convert the two different lists into Object lists for the return to the caller
	    //this is not necessarily best practice to have a 2D array of different kinds of list, but it works...
	    List<List<Object>> results = new ArrayList<List<Object>>();
	    List<Object> list = new ArrayList<Object>(resultsList);	    
	    results.add(list);
	    list = new ArrayList<Object>(facets);
	    results.add(list);
	    return results;
	}
	
	@Override
	public List<RecipeListDto> searchIngredients(String searchTerm) throws SolrServerException, IOException {
		logger.info("searchRecipes: qstr=" + searchTerm);
		
		//Note: the default sort is by score, so no need to explicitly identify score as the sort
		SolrQuery query = new SolrQuery();
		query.setQuery(searchTerm);
		query.setParam("defType","edismax");
		query.setParam("qf", "ingredid");
		query.setParam("fl", "id, userid, name, description, photo, allowshare, status, score, catid, source, sourcetype");
		query.setParam("start", "0");
		query.setParam("rows", "100000");
	    
		QueryResponse rsp = solrCore.query(query);

		ArrayList<RecipeListDto> resultsList = new ArrayList<RecipeListDto>();
	    
		SolrDocumentList docs = rsp.getResults();
		for (SolrDocument doc : docs) {
	    	String result = doc.toString();
	    	logger.debug("doc: " + result);

	    	String idStr = (String)doc.getFieldValue("id");
	    	Long id = Long.valueOf(idStr);
	    	Long uId = (Long)doc.getFieldValue("userid");
	    	String name = (String)doc.getFieldValue("name");
	    	String desc = (String)doc.getFieldValue("description");
	    	boolean allowShare = (boolean)doc.getFieldValue("allowshare");
	    	int stat = (Integer)doc.getFieldValue("status");
	    	ApprovalStatus status = ApprovalStatus.values()[stat];
	    	String catName = (String)doc.getFieldValue("catname");
	    	String srcType = (String)doc.getFieldValue("sourcetype");
	    	SourceType sourceType = SourceType.NONE;
	    	if (!StringUtils.isBlank(srcType))
	    		sourceType = SourceType.valueOf(srcType);
	    	
	    	RecipeListDto rslts = new RecipeListDto(id, uId, name, desc, null, null, null, catName, sourceType, allowShare, status);
    		resultsList.add(rslts);
	    }
	    
	    return resultsList;
	}	

	@Async
	@TransactionalEventListener(fallbackExecution = true)
	@Override
	public void handleUpdateSolrRecipeEvent(UpdateSolrRecipeEvent event) {
		logger.debug("handleUpdateSolrRecipeEvent");
		
		if (event.isDeleteOnly()) {
			deleteRecipe(event.getRecipe().getId());
			return;
		}
		
		if (event.isDeleteFirst())
			deleteRecipe(event.getRecipe().getId());
		
		addRecipe(event.getRecipe());
	}
	
	private void addRecipe(Recipe recipe) {

		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", recipe.getId());
		document.addField("userid", recipe.getUser().getId());
		document.addField("name", recipe.getName());
		document.addField("catname", recipe.getCategory().getName());
		document.addField("category_id", recipe.getCategory().getId());
		document.addField("description", recipe.getDescription());
		document.addField("allowshare", recipe.getAllowShare());
		document.addField("status", recipe.getStatus().getValue());
		if (!StringUtils.isBlank(recipe.getNotes()))
			document.addField("notes", recipe.getNotes());
		if (!StringUtils.isBlank(recipe.getBackground()))
			document.addField("background", recipe.getBackground());
		if (!StringUtils.isBlank(recipe.getPhotoName()))
			document.addField("photo", recipe.getPhotoName());
		if (!recipe.getTags().isEmpty())
			document.addField("tags", recipe.getTags());
		if (recipe.getSource() != null) {
			document.addField("sourcetype", recipe.getSource().getType());
			if (!StringUtils.isBlank(recipe.getSource().getCookbook()))
				document.addField("cookbook", recipe.getSource().getCookbook());
			if (!StringUtils.isBlank(recipe.getSource().getCookbook()))
				document.addField("magazine", recipe.getSource().getMagazine());
			if (!StringUtils.isBlank(recipe.getSource().getCookbook()))
				document.addField("newspaper", recipe.getSource().getNewspaper());
			if (!StringUtils.isBlank(recipe.getSource().getCookbook()))
				document.addField("person", recipe.getSource().getPerson());
			if (!StringUtils.isBlank(recipe.getSource().getCookbook()))
				document.addField("other", recipe.getSource().getOther());
			if (!StringUtils.isBlank(recipe.getSource().getCookbook()))
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
				document.addField("ingredid", recipeIngred.getIngredient().getId());
			}			
		}

		try {
			solrCore.add(document);
			solrCore.commit();
		} catch (SolrServerException ex) {
	    	logService.addException(ex);
	    } catch (IOException ex) {
	    	logService.addException(ex);
	    }		
	}
	
	private void deleteRecipe(Long recipeId) {
		
		String idStr = recipeId.toString();
		
		try {
			solrCore.deleteById(idStr);
			solrCore.commit();
		} catch (SolrServerException ex) {
	    	logService.addException(ex);
	    } catch (IOException ex) {
	    	logService.addException(ex);
	    }		
	}
}
