package net.kear.recipeorganizer.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AutoPopulatingList;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import net.kear.recipeorganizer.exception.RestException;
import net.kear.recipeorganizer.persistence.dto.QuestionDto;
import net.kear.recipeorganizer.persistence.dto.RecipeDisplayDto;
import net.kear.recipeorganizer.persistence.dto.TopicDto;
import net.kear.recipeorganizer.persistence.dto.WhatsNewDto;
import net.kear.recipeorganizer.persistence.model.User;
import net.kear.recipeorganizer.persistence.service.ExceptionLogService;
import net.kear.recipeorganizer.persistence.service.RecipeService;
import net.kear.recipeorganizer.util.CookieUtil;
import net.kear.recipeorganizer.util.UserInfo;
import net.kear.recipeorganizer.util.view.CommonView;

@Controller
public class HomeController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CookieUtil cookieUtil;
	@Autowired
	private UserInfo userInfo;
	@Autowired
	private MessageSource messages;
	@Autowired
	private CommonView commonView;
	@Autowired
	private ExceptionLogService logService;
	@Autowired
	private RecipeService recipeService;

	private static final int MAX_TOPICS = 25;
	private static final int MAX_QUESTIONS = 100;
	
	@RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
	public String getHome(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response, Locale locale) {
		logger.info("home GET");
		
		User currentUser = (User)userInfo.getUserDetails();

		/*User user = null;
		try {
			user = userService.getUser(currentUser.getId());
		} 
		catch (Exception ex) {
			throw new AccessUserException(ex);
		}

		//get returns null if the object is not found in the db; no exception is thrown until you try to use the object
		if (user == null) {
			throw new AccessUserException();
		}*/		
		
		List<RecipeDisplayDto> recentRecipes = null;
		//List<RecipeDisplayDto> viewedRecipes = null;
		List<RecipeDisplayDto> mostViewedRecipes = null;
		RecipeDisplayDto mostViewedRecipe = null;
		RecipeDisplayDto featuredRecipe = null;
		
		try {
			recentRecipes = recipeService.recentRecipes();
			mostViewedRecipes = recipeService.mostViewedRecipes();
			//viewedRecipes = recipeService.viewedRecipes(user.getId());
			mostViewedRecipe = recipeService.getMostViewedRecipe(true);
		} 
		catch (Exception ex) {
			//do nothing - these are not a fatal errors
			logService.addException(ex);
		}

		String recipeIdStr = messages.getMessage("featuredrecipe", null, "0", locale);
		long recipeId = Long.parseLong(recipeIdStr);
		
		try {
			featuredRecipe = recipeService.getFeaturedRecipe(recipeId);
		} 
		catch (Exception ex) {
			//do nothing - these are not a fatal errors
			logService.addException(ex);
		}
		
		//tell the page to not include the white vertical filler
		//model.addAttribute("vertFiller", "1");
		model.addAttribute("recentRecipes", recentRecipes);
		model.addAttribute("mostViewedRecipes", mostViewedRecipes);
		model.addAttribute("featuredRecipe", featuredRecipe);
		model.addAttribute("mostViewedRecipe", mostViewedRecipe);		
		
		if (!cookieUtil.authCookieExists(request))
			cookieUtil.setAuthCookie(request, response, userInfo.getName());
		
		return "home";
	}

	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String getAbout(Model model, HttpSession session) {
		logger.info("about GET");
		
		return "about";
	}

	@RequestMapping(value = "/thankyou", method = RequestMethod.GET)
	public String getThankyou(Model model, HttpSession session) {
		logger.info("thankyou GET");
	
		//tell the page to not include the white vertical filler
		model.addAttribute("vertFiller", "1");
		
		return "thankyou";
	}
	
	@RequestMapping(value = "/faq", method = RequestMethod.GET)
	public String getFaq(Model model, Locale locale) {
		logger.info("FAQ GET");

		List<TopicDto> topics = new AutoPopulatingList<TopicDto>(TopicDto.class);
		List<QuestionDto> questions = new AutoPopulatingList<QuestionDto>(QuestionDto.class);
		
		String totalTopics = messages.getMessage("faq.topic.total", null, "0", locale);
		int totTopics = Integer.parseInt(totalTopics);
		
		int tNdx = 1;
		for (int ndx=1;;ndx++) {
			if ((tNdx > totTopics) || (tNdx > MAX_TOPICS))
				break;
			
			String currTopic = "faq.topic." + ndx + ".";
			String topicTotal = messages.getMessage(currTopic + "count", null, "", locale);
			if (StringUtils.isBlank(topicTotal))
				continue;
				
			int topicTot = Integer.parseInt(topicTotal);
			if (topicTot > 0) {
				TopicDto topic = new TopicDto();
				topic.setId(ndx);
				topic.setDescription(messages.getMessage(currTopic + "description", null, "", locale));
				topics.add(topic);
				tNdx++;
			}
		}

		if (!topics.isEmpty()) {
			TopicDto topic = topics.get(0); 
			int topicOne = topic.getId();
			String topicStr = "faq.topic." + topicOne + ".";
			String topicTotal = messages.getMessage(topicStr + "count", null, "0", locale);
			int topicTot = Integer.parseInt(topicTotal);
	
			int qNdx = 1;
			for (int ndx=1;;ndx++) {
				if ((qNdx > topicTot) || (qNdx > MAX_QUESTIONS))
					break;
				
				String question = messages.getMessage(topicStr + "question." + ndx, null, "", locale);
				if (StringUtils.isBlank(question))
					continue;
				String answer = messages.getMessage(topicStr + "answer." + ndx, null, "", locale);
				if (StringUtils.isBlank(answer))
					continue;
				
				QuestionDto questDto = new QuestionDto();
				questDto.setId(ndx);
				questDto.setQuestion(question);
				questDto.setAnswer(answer);
				questions.add(questDto);
				qNdx++;
			}
		}

		model.addAttribute("topics", topics);
		model.addAttribute("questions", questions);
				
		return "faq";
	}
	
	//NOTE: do NOT add @ResponseBody to this method since that will return a string instead of HTML
	@RequestMapping(value = "/questions/{topicId}", method = RequestMethod.GET)
	@ResponseStatus(value=HttpStatus.OK)
	public String getQuestions(Model model, @PathVariable Integer topicId, Locale locale, HttpServletResponse response) throws RestException {
		logger.info("questions/topic GET: topicId=" + topicId);

		List<QuestionDto> questions = new AutoPopulatingList<QuestionDto>(QuestionDto.class);
		
		String currTopic = "faq.topic." + topicId + ".";
		String topicTotal = messages.getMessage(currTopic + "count", null, "", locale);
		if (StringUtils.isBlank(topicTotal))
			return "questions";
		
		int topicTot = Integer.parseInt(topicTotal);
		String topicStr = "faq.topic." + topicId + ".";
		
		int qNdx = 1;
		for (int ndx=1;;ndx++) {
			if ((qNdx > topicTot) || (qNdx > MAX_QUESTIONS))
				break;
			
			String question = messages.getMessage(topicStr + "question." + ndx, null, "", locale);
			if (StringUtils.isBlank(question))
				continue;
			String answer = messages.getMessage(topicStr + "answer." + ndx, null, "", locale);
			if (StringUtils.isBlank(answer))
				continue;
			
			QuestionDto questDto = new QuestionDto();
			questDto.setId(ndx);
			questDto.setQuestion(question);
			questDto.setAnswer(answer);
			questions.add(questDto);
			qNdx++;
		}
		
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		model.addAttribute("questions", questions);
		
		return "questions";
	}
	
	@RequestMapping(value = "/technical", method = RequestMethod.GET)
	public String getTechnical(Model model) {
		logger.info("technical GET");
		
		return "technical";
	}

	@RequestMapping(value = "/contact", method = RequestMethod.GET)
	public String getContact(Model model) {
		logger.info("contact GET");
				
		return "contact";
	}
	
	@RequestMapping(value = "/policies", method = RequestMethod.GET)
	public String getPolicies(Model model) {
		logger.info("policies GET");
				
		return "policies";
	}

	@RequestMapping(value = "/whatsnew", method = RequestMethod.GET)
	public String getWhatsNew(Model model, Locale locale) {
		logger.info("whatsnew GET");
		
		List<WhatsNewDto> releases = new AutoPopulatingList<WhatsNewDto>(WhatsNewDto.class);

		String totalReleases = messages.getMessage("whatsnew.release.total", null, "0", locale);
		int totReleases = Integer.parseInt(totalReleases);
		String startRelease = messages.getMessage("whatsnew.release.start", null, "0", locale);
		int startNdx = Integer.parseInt(startRelease);
		String endRelease = messages.getMessage("whatsnew.release.end", null, "0", locale);
		int endNdx = Integer.parseInt(endRelease);
		
		int tNdx = 1;
		for (int ndx=startNdx;ndx>=endNdx;ndx--) {
			if (tNdx > totReleases)
				break;
			
			String currRelease = "whatsnew.release." + ndx + ".";
			String releaseCount = messages.getMessage(currRelease + "count", null, "", locale);
			if (StringUtils.isBlank(releaseCount))
				continue;

			int releaseTot = Integer.parseInt(releaseCount);
			if (releaseTot > 0) {
				WhatsNewDto whatsnew = new WhatsNewDto();				
				
				String releaseDateStr = messages.getMessage(currRelease + "date", null, "", locale);
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				
				Date releaseDate = null;
				try {
					releaseDate = sdf.parse(releaseDateStr);
				} catch (ParseException e) {
				}
				whatsnew.setReleaseDate(releaseDate);
				
				List<String> descList = new AutoPopulatingList<String>(String.class);
				
				for (int descndx=1;descndx<=releaseTot;descndx++) {
			
					String description = messages.getMessage(currRelease + "description." + descndx, null, "", locale);
					if (StringUtils.isBlank(description))
						continue;
					
					descList.add(description);
				}

				whatsnew.setDescriptions(descList);
				releases.add(whatsnew);
			}
			tNdx++;
		}

		model.addAttribute("releases", releases);
				
		return "whatsnew";
	}
	
	@RequestMapping(value = "/betatest", method = RequestMethod.GET)
	public String getBetaTest(Model model, Locale locale) {
		logger.info("betatest GET");

		return "betatest";
	}

	@RequestMapping(value = "/sysmaint", method = RequestMethod.GET)
	public ModelAndView getMaintenance(Model model, Locale locale) {
		logger.info("sysmaint GET");

		return commonView.getMaintenancePage(locale);
	}

	//AJAX/JSON request for getting the timeout interval for the current user
	@RequestMapping(value="/getSessionTimeout")
	@ResponseBody 
	public Integer getSessionTimeout(HttpSession session, HttpServletResponse response) {
		
		Integer maxInactive = session.getMaxInactiveInterval();
		
		logger.debug("getSessionTimeout returned " + maxInactive + " seconds");
		
		return maxInactive;
	}

	//AJAX/JSON request for resetting the session timeout
	@RequestMapping(value="/setSessionTimeout", method = RequestMethod.POST)
	@ResponseBody 
	public String setSessionTimeout(HttpSession session, HttpServletResponse response) {
		
		response.setStatus(HttpServletResponse.SC_OK);
		
		Integer maxInactive = session.getMaxInactiveInterval();
		session.setMaxInactiveInterval(maxInactive);
		
		logger.debug("setSessionTimeout for " + maxInactive + " seconds");
		
		return "{}";
	}
}