package net.kear.recipeorganizer.util.view;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public interface CommonView {

	public ModelAndView getStandardErrorPage(Exception ex);
	public ModelAndView getSystemErrorPage(HttpServletRequest request, HttpServletResponse response, Locale locale);
	public ModelAndView getMaintenancePage(Locale locale);
}
