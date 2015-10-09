package net.kear.recipeorganizer.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.kear.recipeorganizer.persistence.dto.UserDto;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {   
    
	@Override
    public void initialize(PasswordMatch constraintAnnotation) {       
    }
    
	@Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){   
        UserDto user = (UserDto) obj;
        
        return user.getPassword().equals(user.getConfirmPassword());    
    }     
}