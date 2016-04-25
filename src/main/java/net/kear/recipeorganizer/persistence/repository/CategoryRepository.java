package net.kear.recipeorganizer.persistence.repository;

import java.util.List;

import net.kear.recipeorganizer.persistence.dto.CategoryDto;
import net.kear.recipeorganizer.persistence.model.Category;
 
public interface CategoryRepository {

    public void addCategory(Category category);
    public void updateCategory(Category category);
    public void deleteCategory(Long id);
    public List<Category> listCategory();
    public List<CategoryDto> listCategoryDto();
    public String getCategoryName(Long id);
    public Category getCategory(Long id);
}
