package net.kear.recipeorganizer.webflow;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.interceptor.MaintenanceInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.webflow.mvc.servlet.FlowHandlerAdapter;

@Component
public class RecipeFlowHandlerAdapter extends FlowHandlerAdapter {

	@Autowired
	private MaintenanceInterceptor maintInterceptor; 
	@Autowired
	private ServletContext servletContext;

	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		Locale locale = request.getLocale();

		/*ServletContext context = request.getSession().getServletContext();
		if (maintInterceptor.isMaintenanceWindowSet() && !maintInterceptor.isMaintenanceInEffect()) {
			String msg = maintInterceptor.getImminentMaint(locale);
			if (!msg.isEmpty())
				context.setAttribute("warningMaint", msg);
		}*/

		if (maintInterceptor.isMaintenanceWindowSet() && !maintInterceptor.isMaintenanceInEffect()) {
			String msg = maintInterceptor.getImminentMaint(locale);
			if (!msg.isEmpty())
				servletContext.setAttribute("warningMaint", msg);
		}
		
		return super.handle(request, response, handler);
	}
}
