package net.kear.recipeorganizer.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.kear.recipeorganizer.persistence.dto.ChangeEmailDto;
import net.kear.recipeorganizer.persistence.dto.UserDto;

public class EmailMatchValidator implements ConstraintValidator<EmailMatch, Object> {   
    
	@Override
    public void initialize(EmailMatch constraintAnnotation) {       
    }
    
	@Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
		if (obj instanceof UserDto) {
			UserDto user = (UserDto) obj;
			return user.getEmail().equalsIgnoreCase(user.getConfirmEmail());
		}

		if (obj instanceof ChangeEmailDto) {
			ChangeEmailDto user = (ChangeEmailDto) obj;
			return user.getEmail().equalsIgnoreCase(user.getConfirmEmail());
		}
		
		return false;
    }     
}