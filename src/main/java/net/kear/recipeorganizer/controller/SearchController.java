package net.kear.recipeorganizer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.kear.recipeorganizer.persistence.dto.SearchResultsDto;
import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.model.Source;
import net.kear.recipeorganizer.persistence.service.CategoryService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.SourceService;
import net.kear.recipeorganizer.util.CategoryFacet;
import net.kear.recipeorganizer.util.CookieUtil;
import net.kear.recipeorganizer.util.SolrUtil;
import net.kear.recipeorganizer.util.SourceFacet;
import net.kear.recipeorganizer.util.UserInfo;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionAttributes("searchTerm")
public class SearchController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private SourceService sourceService;
	@Autowired
	private CookieUtil cookieUtil;
	@Autowired
	private SolrUtil solrUtil;
	@Autowired
	private UserInfo userInfo;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/submitSearch", method = RequestMethod.POST)
	public ModelAndView submitSearch(@RequestParam String searchTerm, RedirectAttributes redir, Locale locale) throws SolrServerException, IOException {
		logger.info("submitSearch POST: searchTerm=" + searchTerm);
		
		List<List<Object>> results = solrUtil.searchRecipes(searchTerm);
		//the first list in the 2D results set is the actual search results
		List<Object> objList = results.get(0);
		ArrayList<SearchResultsDto> resultsList = (ArrayList<SearchResultsDto>)(List<?>)objList;

		//long userId = 0;
		//User user = (User)userInfo.getUserDetails();
		//if (user != null)
			//userId = user.getId();
		
		//ArrayList<SearchResultsDto> filteredList = filterResults(resultsList, userId);
		//int numFound = filteredList.size();
		int numFound = resultsList.size();

		List<CategoryFacet> catFacets = null;
		List<SourceFacet> srcFacets = null;
		
		if (numFound > 0) {
			//the second list is the facets results (category & source)
			objList = results.get(1);
			ArrayList<FacetField> facets = (ArrayList<FacetField>)(List<?>)objList;
			catFacets = getCategories(facets);
			srcFacets = getSources(facets, numFound, locale);
		}
		
	    ModelAndView mv = new ModelAndView();
	    redir.addFlashAttribute("searchTerm", searchTerm);
	    //redir.addFlashAttribute("resultList", filteredList);
	    redir.addFlashAttribute("resultList", resultsList);
	    redir.addFlashAttribute("numFound", numFound);
	    redir.addFlashAttribute("categories", catFacets);
	    redir.addFlashAttribute("sources", srcFacets);
        mv.setViewName("redirect:/searchResults");
	    return mv;
	}	

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/searchResults", method = RequestMethod.GET)
	public String getSearchResults(Model model, Locale locale) throws SolrServerException, IOException {
		logger.info("searchResults GET");
		
		HashMap<String, Object> modelMap = (HashMap<String, Object>) model.asMap();
		int num = modelMap.size();
		logger.debug("modelMap size: " + num);
		boolean term = modelMap.containsKey("searchTerm");
		boolean list = modelMap.containsKey("resultList");
		boolean found = modelMap.containsKey("numFound");
		logger.debug("modelMap contains serchTerm: " + term);
		logger.debug("modelMap contains resultList: " + list);
		logger.debug("modelMap contains numFound: " + found);

		Object val = modelMap.get("searchTerm");
		String searchTerm = (String)val;
		logger.debug("modelMap searchTerm: " + searchTerm);

		//long userId = 0;
		//User user = (User)userInfo.getUserDetails();
		//if (user != null)
			//userId = user.getId();
		
		//the user returned to the results page from viewing a recipe - need to re-run the search
		if (!found && !list) {
			List<List<Object>> results = solrUtil.searchRecipes(searchTerm);
			List<Object> objList = results.get(0);
			ArrayList<SearchResultsDto> resultsList = (ArrayList<SearchResultsDto>)(List<?>)objList;			
			//ArrayList<SearchResultsDto> filteredList = filterResults(resultsList, userId);
			//int numFound = filteredList.size();
			int numFound = resultsList.size();

			List<CategoryFacet> catFacets = null;
			List<SourceFacet> srcFacets = null;
			
			if (numFound > 0) {
				//the second list is the category facets results
				objList = results.get(1);
				ArrayList<FacetField> facets = (ArrayList<FacetField>)(List<?>)objList;
				catFacets = getCategories(facets);
				srcFacets = getSources(facets, numFound, locale);
			}
			
			model.addAttribute("searchTerm", searchTerm);
			//model.addAttribute("resultList", filteredList);
			//model.addAttribute("numFound", filteredList.size());
			model.addAttribute("resultList", resultsList);
			model.addAttribute("numFound", resultsList.size());
			model.addAttribute("categories", catFacets);
			model.addAttribute("sources", srcFacets);
			model.addAttribute("newSearch", false);
		}
		else
			model.addAttribute("newSearch", true);
		
		return "searchResults";
	}
	
	/*private ArrayList<SearchResultsDto> filterResults(ArrayList<SearchResultsDto> results, long userId) {

		Iterator<SearchResultsDto> iter = results.iterator();
		while (iter.hasNext()) {
			SearchResultsDto result = iter.next(); 
			if (result.getUserId() != userId) {
				if (result.getAllowShare() == false || result.getApproved() == false) {
					iter.remove();
				}
			}					
		}
		
		return results;
	}*/
	
	private List<CategoryFacet> getCategories(ArrayList<FacetField> facets) {
		
		ArrayList<CategoryFacet> cats = new ArrayList<CategoryFacet>();
		
		for (FacetField facet : facets) {
			String fName = facet.getName();
			if (fName.equalsIgnoreCase("catid")) {
				List<Count> counts = facet.getValues();
				for (Count count : counts) {
					String cName = count.getName();
					long cCount = count.getCount();
					if (cCount > 0) {
						long id = Long.parseLong(cName);
						Category cat = categoryService.getCategory(id);
						CategoryFacet catFacet = new CategoryFacet(cat.getId(), cat.getName(), cCount);
						cats.add(catFacet);
					}
				}
			}
		}
		
		return cats;
	}

	private List<SourceFacet> getSources(ArrayList<FacetField> facets, int numFound, Locale locale) {
		
		ArrayList<SourceFacet> srcs = new ArrayList<SourceFacet>();
		
		int facetCount = 0;
		
		for (FacetField facet : facets) {
			String fName = facet.getName();
			if (fName.equalsIgnoreCase("srctype")) {
				List<Count> counts = facet.getValues();
				for (Count count : counts) {
					String cName = count.getName();
					long cCount = count.getCount();
					facetCount += cCount;
					if (cCount > 0) {
						String sourceName = sourceService.getSourceDisplayName(cName, locale);
						if (sourceName != null) {
							SourceFacet srcFacet = new SourceFacet(cName, sourceName, cCount);
							srcs.add(srcFacet);
						}
					}
				}
			}
		}
		
		if (numFound > facetCount) {
			String sourceName = sourceService.getSourceDisplayName(Source.TYPE_NONE, locale);
			if (sourceName != null) {
				SourceFacet srcFacet = new SourceFacet(Source.TYPE_NONE, sourceName, numFound - facetCount);
				srcs.add(srcFacet);
			}
		}
		
		return srcs;
	}
}