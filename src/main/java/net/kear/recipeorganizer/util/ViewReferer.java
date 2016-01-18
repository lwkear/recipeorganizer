package net.kear.recipeorganizer.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ViewReferer {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public ViewReferer() {}
	
	public void setReferer(String referer, HttpServletRequest request) {
	
		String returnLabel = null;
		
		if (referer.contains("searchResults"))
			returnLabel = "title.searchresults";
		else
		if (referer.contains("listRecipes"))
			returnLabel = "menu.submittedrecipes";
		else
		if (referer.contains("favorites"))
			returnLabel = "menu.favorites";
		else
		if (referer.contains("dashboard"))
			returnLabel = "dashboard.head";
		else
			returnLabel = "";

		logger.info("referLabel: " + returnLabel);
		logger.info("referUrl: " + referer);

		request.getSession().setAttribute("returnLabel", returnLabel);
		request.getSession().setAttribute("returnUrl", referer);
	}
}
