package net.kear.recipeorganizer.webflow;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.kear.recipeorganizer.interceptor.MaintenanceInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.mvc.servlet.FlowHandlerAdapter;

@Component
public class RecipeFlowHandlerAdapter extends FlowHandlerAdapter {

	@Autowired
	private MaintenanceInterceptor maintInterceptor; 
	
	@Override
	protected ServletExternalContext createServletExternalContext(HttpServletRequest request, HttpServletResponse response) {

		ServletExternalContext context = super.createServletExternalContext(request, response);

		Locale locale = request.getLocale();
		if (maintInterceptor.isMaintenanceWindowSet() && !maintInterceptor.isMaintenanceInEffect()) {
			String msg = maintInterceptor.getImminentMaint(locale);
			if (!msg.isEmpty())
				context.getSessionMap().put("warningMaint", msg);
		}
		
		return context;
	}
}
