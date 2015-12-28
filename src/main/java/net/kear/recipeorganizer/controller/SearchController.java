package net.kear.recipeorganizer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.kear.recipeorganizer.persistence.dto.SearchResultsDto;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SearchController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
    private static String url = "http://localhost:8983/solr/recipe/";
    private static HttpSolrClient solrCore = new HttpSolrClient(url);
	
	@RequestMapping(value = "/submitSearch", method = RequestMethod.POST)
	public ModelAndView submitSearch(@RequestParam String searchTerm, RedirectAttributes redir) {
		logger.info("postTestSearch");
		
		QueryResponse rsp = null;
		
		SolrQuery query = new SolrQuery();
		query.setQuery(searchTerm);
		query.setParam("defType","edismax");
		query.setParam("qf", "name or catname or ingredname or description or source");
		query.setParam("fl", "id, name, description, photo");
		query.setHighlight(true);
		query.addHighlightField("name or description");
		query.setHighlightSimplePre("<strong>");
		query.setHighlightSimplePost("</strong>");
		query.addSort("name", SolrQuery.ORDER.asc);
	    
	    String qstr = ClientUtils.toQueryString(query, false);
	    logger.info("qstr: " + qstr);
	    
	    try {
			rsp = solrCore.query(query);
		} catch (SolrServerException | IOException e) {
			//TODO: SOLR Auto-generated catch block
			e.printStackTrace();
		}
	    
	    List<SearchResultsDto> resultsList = new ArrayList<SearchResultsDto>();
	    
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
	    
	    long found = docs.getNumFound();
	    
	    ModelAndView mv = new ModelAndView();
	    
	    redir.addFlashAttribute("numFound", found);
	    redir.addFlashAttribute("resultList", resultsList);
        mv.setViewName("redirect:/searchResults");
		
		return mv;
	}	

	@RequestMapping(value = "/searchResults", method = RequestMethod.GET)
	public String getResultspage(Model model) {
		logger.info("getResultspage");

		return "searchResults";
	}
	
}