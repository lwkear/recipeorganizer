package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Category;
import net.kear.recipeorganizer.persistence.repository.CategoryRepository;
import net.kear.recipeorganizer.persistence.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
 
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

    public String getCategoryName(Long id) {
    	return categoryRepository.getCategoryName(id);
    }
    
    public Category getCategory(Long id) {
    	return categoryRepository.getCategory(id);
    }
}