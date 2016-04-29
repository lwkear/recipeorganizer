package net.kear.recipeorganizer.persistence.service;
 
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import net.kear.recipeorganizer.persistence.dto.CategoryDto;
import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.repository.CategoryRepository;
import net.kear.recipeorganizer.persistence.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private MessageSource messages;
    @Autowired
    private CategoryRepository categoryRepository;
      
    public void addCategory(Category category) {
    	categoryRepository.addCategory(category);
    }
    
    public void updateCategory(Category category) {
    	categoryRepository.updateCategory(category);
    }
 
    public void deleteCategory(Long id) {
    	categoryRepository.deleteCategory(id);
    }

    public List<Category> listCategory() {
    	return categoryRepository.listCategory();
    }
    
    public List<CategoryDto> listCategoryDto(Locale locale) {
    	
    	List<CategoryDto> list = categoryRepository.listCategoryDto();
    	for (CategoryDto cat : list) {
    		String display = messages.getMessage("category." + cat.getName(), null, cat.getName(), locale);
    		cat.setDisplayName(display);
    	}
    	
    	Collections.sort(list, new Comparator<CategoryDto>() {
			@Override
			public int compare(CategoryDto o1, CategoryDto o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
    	});
    	
    	return list;
    }

    public String getCategoryName(Long id) {
    	return categoryRepository.getCategoryName(id);
    }
    
    public Category getCategory(Long id) {
    	return categoryRepository.getCategory(id);
    }
}