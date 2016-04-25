package net.kear.recipeorganizer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.kear.recipeorganizer.enums.SourceType;
import net.kear.recipeorganizer.persistence.dto.RecipeListDto;
import net.kear.recipeorganizer.persistence.dto.SearchResultsDto;
import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.model.Ingredient;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.CategoryService;
import net.kear.recipeorganizer.persistence.service.IngredientService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.persistence.service.SourceService;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.security.AuthCookie;
import net.kear.recipeorganizer.solr.CategoryFacet;
import net.kear.recipeorganizer.solr.SolrUtil;
import net.kear.recipeorganizer.solr.SourceFacet;
import net.kear.recipeorganizer.util.CookieUtil;
import net.kear.recipeorganizer.util.UserInfo;
import net.kear.recipeorganizer.util.maint.MaintAware;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
	private static final AuthCookie authCookie = new AuthCookie();
	
	@Autowired
	private UserService userService;
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private IngredientService ingredientService;
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
	@Autowired
	private MessageSource messages;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/submitSearch", method = RequestMethod.POST)
	public ModelAndView submitSearch(@RequestParam String searchTerm, RedirectAttributes redir, HttpServletRequest request, Locale locale) throws SolrServerException, IOException {
		logger.info("submitSearch POST: searchTerm=" + searchTerm);

		long userId = 0L;
		if (!authCookie.isCurrentCookieAnonymous(request)) {
			User user = (User)userInfo.getUserDetails();
			userId = user.getId();
		}
				
		List<List<Object>> results = solrUtil.searchRecipes(searchTerm, userId);
		//the first list in the 2D results set is the actual search results
		List<Object> objList = results.get(0);
		ArrayList<SearchResultsDto> resultsList = (ArrayList<SearchResultsDto>)(List<?>)objList;

		int numFound = resultsList.size();

		List<CategoryFacet> catFacets = null;
		List<SourceFacet> srcFacets = null;
		
		if (numFound > 0) {
			//the second list is the facets results (category & source)
			objList = results.get(1);
			ArrayList<FacetField> facets = (ArrayList<FacetField>)(List<?>)objList;
			catFacets = getCategories(facets, locale);
			srcFacets = getSources(facets, numFound, locale);
		}
		
	    ModelAndView mv = new ModelAndView();
	    redir.addFlashAttribute("searchTerm", searchTerm);
	    redir.addFlashAttribute("resultList", resultsList);
	    redir.addFlashAttribute("numFound", numFound);
	    redir.addFlashAttribute("categories", catFacets);
	    redir.addFlashAttribute("sources", srcFacets);
        mv.setViewName("redirect:/searchResults");
	    return mv;
	}	

	@MaintAware
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/searchResults", method = RequestMethod.GET)
	public String getSearchResults(Model model, HttpServletRequest request, Locale locale) throws SolrServerException, IOException {
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

		long userId = 0L;
		if (!authCookie.isCurrentCookieAnonymous(request)) {
			User user = (User)userInfo.getUserDetails();
			userId = user.getId();
		}
		
		//the user returned to the results page from viewing a recipe - need to re-run the search
		if (!found && !list) {
			List<List<Object>> results = solrUtil.searchRecipes(searchTerm, userId);
			List<Object> objList = results.get(0);
			ArrayList<SearchResultsDto> resultsList = (ArrayList<SearchResultsDto>)(List<?>)objList;			
			int numFound = resultsList.size();

			List<CategoryFacet> catFacets = null;
			List<SourceFacet> srcFacets = null;
			
			if (numFound > 0) {
				//the second list is the category facets results
				objList = results.get(1);
				ArrayList<FacetField> facets = (ArrayList<FacetField>)(List<?>)objList;
				catFacets = getCategories(facets, locale);
				srcFacets = getSources(facets, numFound, locale);
			}
			
			model.addAttribute("searchTerm", searchTerm);
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

	@RequestMapping(value = "/submitIngredientSearch", method = RequestMethod.POST)
	public ModelAndView submitIngredientSearch(@RequestParam String searchTerm, RedirectAttributes redir, HttpServletRequest request, Locale locale) throws SolrServerException, IOException, Exception {
		logger.info("submitIngredientSearch POST: searchTerm=" + searchTerm);

		List<RecipeListDto> resultsList = solrUtil.searchIngredients(searchTerm);

		int numFound = resultsList.size();
		if (numFound > 0) {
			for (RecipeListDto dto : resultsList) {
				String userName = userService.getUserFullName(dto.getUserId());
				dto.setFirstName(userName);
			}			
		}		
		
		long ingredId = Long.parseLong(searchTerm);
		Ingredient ingredient = ingredientService.getIngredient(ingredId);
		
	    ModelAndView mv = new ModelAndView();
	    redir.addFlashAttribute("searchTerm", searchTerm);
	    redir.addFlashAttribute("ingredientName", ingredient.getName());
	    redir.addFlashAttribute("recipes", resultsList);
	    redir.addFlashAttribute("numFound", numFound);
        mv.setViewName("redirect:/admin/ingredientRecipes");
	    return mv;
	}	
	
	@MaintAware
	@RequestMapping(value = "/admin/ingredientRecipes", method = RequestMethod.GET)
	public String getIngredientSearchResults(Model model, HttpServletRequest request, Locale locale) throws SolrServerException, IOException {
		logger.info("ingredientSearchResults GET");
		
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

		//the user returned to the results page from viewing a recipe - need to re-run the search
		if (!found && !list) {
			List<RecipeListDto> resultsList = solrUtil.searchIngredients(searchTerm);

			int numFound = resultsList.size();
			if (numFound > 0) {
				for (RecipeListDto dto : resultsList) {
					String userName = userService.getUserFullName(dto.getUserId());
					dto.setFirstName(userName);
				}			
			}		

			long ingredId = Long.parseLong(searchTerm);
			Ingredient ingredient = ingredientService.getIngredient(ingredId);
			
		    model.addAttribute("searchTerm", searchTerm);
		    model.addAttribute("ingredientName", ingredient.getName());
		    model.addAttribute("recipes", resultsList);
		    model.addAttribute("numFound", numFound);
			model.addAttribute("newSearch", false);
		}
		else
			model.addAttribute("newSearch", true);
		
		return "admin/ingredientRecipes";
	}
	
	private List<CategoryFacet> getCategories(ArrayList<FacetField> facets, Locale locale) {
		
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
						String catName = messages.getMessage("category."+cat.getName(), null, null, locale);
						CategoryFacet catFacet = new CategoryFacet(id, catName, cCount);
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
						String sourceName = messages.getMessage("sourcetype."+cName, null, null, locale);
						if (sourceName != null) {
							SourceFacet srcFacet = new SourceFacet(cName, sourceName, cCount);
							srcs.add(srcFacet);
						}						
					}
				}
			}
		}
		
		if (numFound > facetCount) {
			String sourceName = messages.getMessage("sourcetype."+SourceType.NONE.name(), null, null, locale);
			if (sourceName != null) {
				SourceFacet srcFacet = new SourceFacet(SourceType.NONE.name(), sourceName, numFound - facetCount);
				srcs.add(srcFacet);
			}
		}
		
		return srcs;
	}
}