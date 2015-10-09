package net.kear.recipeorganizer.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.kear.recipeorganizer.persistence.service.UserService;

public class NotEmailExistValidator implements ConstraintValidator<NotEmailExist, String> {   
 
    @Autowired
    private UsersService usersService;
	
	@Override
    public void initialize(NotEmailExist constraintAnnotation) {       
    }
    
	@Override
    public boolean isValid(String email, ConstraintValidatorContext context) {   
        boolean exist = usersService.doesUserEmailExist(email);
        return (!exist);
    }     
}