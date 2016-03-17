package net.kear.recipeorganizer.controller;

import java.util.Locale;

import net.kear.recipeorganizer.interceptor.MaintenanceInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

	@ControllerAdvice
	public class GlobalModelController {
	
		private final Logger logger = LoggerFactory.getLogger(getClass());
		
		@Autowired
		private MaintenanceInterceptor maintInterceptor; 
	
		@ModelAttribute()
		public void globalAttributes(Model model, Locale locale) {
/*			if (maintInterceptor.isMaintenanceWindowSet() && !maintInterceptor.isMaintenanceInEffect()) {
				String msg = maintInterceptor.getImminentMaint(locale);
				model.addAttribute("warningMaint", msg);
				logger.debug("maint msg= " + msg);			
			}
*/		}
	}
