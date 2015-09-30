package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import net.kear.recipeorganizer.persistence.model.Category;
 
public interface CategoryService {
     
    public void addCategory(Category category);
    public void updateCategory(Category category);
    public void deleteCategory(Long id);
    public List<Category> listCategory();
    public String getCategoryName(Long id);
    public Category getCategory(Long id);
}