package net.kear.recipeorganizer.persistence.service;
 
import net.kear.recipeorganizer.persistence.model.Role;
 
public interface RoleService {
     
    public String getRoleName(Long id);
    public Role getRole(String name);
    public Role getDefaultRole();
}