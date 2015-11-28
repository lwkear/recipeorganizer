package net.kear.recipeorganizer.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.kear.recipeorganizer.persistence.model.Recipe;
import net.kear.recipeorganizer.persistence.service.UserService;
import net.kear.recipeorganizer.security.AuthCookie;
import net.kear.recipeorganizer.util.UserInfo;

@Controller
public class HomeController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
    private static String url = "http://localhost:8983/solr/recipe/";
    private static HttpSolrClient solrCore = new HttpSolrClient(url);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthCookie authCookie;
	
	@Autowired
	private UserInfo userInfo;
	
	@RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
	public String getHome(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		logger.info("getHome");

		//tell the page to not include the white vertical filler
		model.addAttribute("vertFiller", "1");
		
		/*Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate);

		Date createTime = new Date(session.getCreationTime());
		Date lastAccess = new Date(session.getLastAccessedTime());
		int maxInactive = session.getMaxInactiveInterval();
		String sess = session.toString();
		String sessID = session.getId();
		
		String sCreate = "Session created on: " + createTime;
		String sLast = "Session last accessed on: " + lastAccess;
		String sInactive = "Session expires after: " + maxInactive + " seconds";
		String sID = "Session ID: " + sessID;
		
		String country = locale.getCountry();
		String language = locale.getLanguage();
		String sCountry = "Request country: " + country;
		String sLanguage = "Request language: " + language;
		
		model.addAttribute("create", sCreate);
		model.addAttribute("last", sLast);
		model.addAttribute("inactive", sInactive);
		model.addAttribute("sess", sess);
		model.addAttribute("sessID", sID);
		model.addAttribute("country", sCountry);
		model.addAttribute("language", sLanguage);*/
		
		if (!authCookie.cookieExists(request))
			authCookie.setCookie(request, response, userInfo.getName());
		
		return "home";
	}

	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String getAbout(Model model, HttpSession session) {
		logger.info("getAbout");
		
		return "about";
	}
	
	@RequestMapping(value = "/about", method = RequestMethod.POST)
	public ModelAndView postAbout(@RequestParam String searchTerm, RedirectAttributes redir) {
		logger.info("postAbout");
		
		QueryResponse rsp = null;
		
		SolrQuery query = new SolrQuery();
	    query.setQuery("name:" + searchTerm);
	    query.add("name:" + searchTerm);
	    //query.add("catname:" + searchTerm);
	    query.addSort("name", SolrQuery.ORDER.asc);
	    
	    try {
			rsp = solrCore.query( query );
		} catch (SolrServerException | IOException e) {
			//TODO: SOLR Auto-generated catch block
			e.printStackTrace();
		}
	    
	    ArrayList<String> recipeNames = new ArrayList<String>();
	    
	    SolrDocumentList docs = rsp.getResults();
	    for (SolrDocument doc : docs) {
	    	String rslt = doc.toString();
	    	logger.info("doc: " + rslt);
	    	
	    	String name = (String)doc.getFieldValue("name");
	    	
	    	recipeNames.add(name);
	    }
	    
	    long found = docs.getNumFound();
	    
	    ModelAndView mv = new ModelAndView();
	    
	    redir.addFlashAttribute("numFound", found);
	    redir.addFlashAttribute("nameList", recipeNames);
        mv.setViewName("redirect:/searchResults");
		
		return mv;
	}	

	@RequestMapping(value = "/thankyou", method = RequestMethod.GET)
	public String getThankyou(Model model) {
		logger.info("getThankyou");

		//tell the page to not include the white vertical filler
		model.addAttribute("vertFiller", "1");
		
		/*Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate);*/

		return "thankyou";
	}

	@RequestMapping(value = "/testpage", method = RequestMethod.GET)
	public String getTestpage(Model model) {
		logger.info("getTestpage");

		Recipe recipe = new Recipe();
		recipe.setAllowShare(true);
		model.addAttribute("recipe", recipe);
		
		return "testpage";
	}

	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public String getStartpage(Model model) {
		logger.info("getStartpage");

		return "start";
	}

	@RequestMapping(value = "/searchResults", method = RequestMethod.GET)
	public String getResultspage(Model model) {
		logger.info("getResultspage");

		return "searchResults";
	}
	
	/*@RequestMapping(value = "/recipe/end", method = RequestMethod.GET)
	public String getEndpage(Model model) {
		logger.info("getEndpage");

		return "recipe/end";
	}*/
}