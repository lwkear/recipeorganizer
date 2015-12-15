package net.kear.recipeorganizer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.service.CategoryService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class AdminController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CategoryService categoryService;

	/**
	 * Simply selects the login view to render by returning its name.
	 */

	@RequestMapping(value = "/admin/category", method = RequestMethod.GET)
	public String loadCategory(Model model) {
		logger.info("admin/category GET");
		
		Category category = new Category();
		model.addAttribute("category", category);
		model.addAttribute("categoryList", categoryService.listCategory());
		
		return "admin/category";
	}
	
	@RequestMapping(value = "/admin/category", method = RequestMethod.POST, params = {"save"})	
	public String saveCategory(@ModelAttribute Category category) {
		logger.info("admin/category POST save");
		
		if (category.getId() == 0) {
			logger.info("ID = 0");
			logger.info("Name = " + category.getName());
			
			categoryService.addCategory(category);
		}
		else {
			logger.info("ID = " + category.getId());
			logger.info("Name = " + category.getName());
			
			categoryService.updateCategory(category);
		}
		
		return "redirect:category";
	}

	@RequestMapping(value = "/admin/category", method = RequestMethod.POST, params = {"delete"})	
	public String deleteCategory(@ModelAttribute Category category) {
		logger.info("admin/category POST delete");
		logger.info("ID = " + category.getId());
		logger.info("Name = " + category.getName());
		
		/* Must check both fields - the ID is hidden and doesn't get cleared when the Reset button is clicked
		 * This is the default behavior of Reset and not worth the effort to override */
		if ((category.getId() != 0) && (category.getName() != "")) {
			categoryService.deleteCategory(category.getId());
		}

		return "redirect:category";
	}
}
