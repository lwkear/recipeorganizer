package net.kear.recipeorganizer.util;

import org.springframework.web.servlet.ModelAndView;

public interface CommonView {

	public ModelAndView getStandardErrorPage(Exception ex);

}
