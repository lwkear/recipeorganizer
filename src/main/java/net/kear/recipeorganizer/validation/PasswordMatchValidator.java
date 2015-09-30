package net.kear.recipeorganizer.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.kear.recipeorganizer.persistence.model.Users;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {   
    
	@Override
    public void initialize(PasswordMatch constraintAnnotation) {       
    }
    
	@Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){   
        Users user = (Users) obj;
        return user.getPassword().equals(user.getConfirmPassword());    
    }     
}