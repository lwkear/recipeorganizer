package net.kear.recipeorganizer.persistence.service;
 
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.kear.recipeorganizer.persistence.model.Role;
import net.kear.recipeorganizer.persistence.repository.RoleRepository;
 
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
	
    public String getRoleName(Long id) {
    	return roleRepository.getRoleName(id);    	
    }
    
    public Role getRole(String name) {
    	return roleRepository.getRole(name);    	
    }
    
    public List<Role> getRoles() {
    	return roleRepository.getRoles();
    }
}