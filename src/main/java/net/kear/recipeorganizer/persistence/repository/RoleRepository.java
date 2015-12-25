package net.kear.recipeorganizer.persistence.repository;

import java.util.List;

import net.kear.recipeorganizer.persistence.model.Role;
 
public interface RoleRepository {

    public String getRoleName(Long id);
    public Role getRole(String name);
    public Role getDefaultRole();
    public List<Role> getRoles();
}
