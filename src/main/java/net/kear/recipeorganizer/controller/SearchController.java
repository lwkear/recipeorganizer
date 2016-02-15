package net.kear.recipeorganizer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.kear.recipeorganizer.persistence.dto.SearchResultsDto;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.util.CookieUtil;
import net.kear.recipeorganizer.util.SolrUtil;
import net.kear.recipeorganizer.util.UserInfo;

import org.apache.solr.client.solrj.SolrServerException;
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
	private CookieUtil cookieUtil;
	@Autowired
	private SolrUtil solrUtil;
	@Autowired
	private UserInfo userInfo;
	
	@RequestMapping(value = "/submitSearch", method = RequestMethod.POST)
	public ModelAndView submitSearch(@RequestParam String searchTerm, RedirectAttributes redir) throws SolrServerException, IOException {
		logger.info("submitSearch POST: searchTerm=" + searchTerm);
		
		ArrayList<SearchResultsDto> resultsList = solrUtil.searchRecipes(searchTerm);

		User user = (User)userInfo.getUserDetails();
		long userId = user.getId();
		Iterator<SearchResultsDto> iter = resultsList.iterator();
		while (iter.hasNext()) {
			SearchResultsDto result = iter.next(); 
			if (result.getUserId() != userId) {
				if (result.getAllowShare() == false || result.getApproved() == false) {
					iter.remove();
				}
			}					
		}
	    
	    ModelAndView mv = new ModelAndView();
	    redir.addFlashAttribute("searchTerm", searchTerm);
	    redir.addFlashAttribute("resultList", resultsList);
	    redir.addFlashAttribute("numFound", resultsList.size());	    
        mv.setViewName("redirect:/searchResults");
	    return mv;
	}	

	@RequestMapping(value = "/searchResults", method = RequestMethod.GET)
	public String getSearchResults(Model model) throws SolrServerException, IOException {
		logger.info("searchResults GET");
		
		HashMap<String, Object> modelMap = (HashMap<String, Object>) model.asMap();
		int num = modelMap.size();
		logger.debug("modelMap size: " + num);
		boolean term = modelMap.containsKey("searchTerm");
		boolean list = modelMap.containsKey("resultList");
		boolean found = modelMap.containsKey("numFound");
		logger.debug("modelMap contains serchTerm: " + term);
		logger.debug("modelMap contains resultList: " + list);
		logger.debug("modelMap contains found: " + found);

		Object val = modelMap.get("searchTerm");
		String searchTerm = (String)val;
		logger.debug("modelMap searchTerm: " + searchTerm);
		
		List<SearchResultsDto> resultsList = null;
		
		if (!found && !list) {
			resultsList = solrUtil.searchRecipes(searchTerm);
			model.addAttribute("searchTerm", searchTerm);
			model.addAttribute("resultList", resultsList);
			model.addAttribute("numFound", resultsList.size());
		}
		
		return "searchResults";
	}
}