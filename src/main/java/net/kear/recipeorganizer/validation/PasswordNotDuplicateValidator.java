package net.kear.recipeorganizer.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.kear.recipeorganizer.persistence.dto.ChangePasswordDto;

public class PasswordNotDuplicateValidator implements ConstraintValidator<PasswordNotDuplicate, Object> {

	@Override
    public void initialize(PasswordNotDuplicate constraintAnnotation) {       
    }
    
	@Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
		String password = "";
		String currentPassword = "";
		
		if (obj instanceof ChangePasswordDto) {
			ChangePasswordDto pswdDto = (ChangePasswordDto) obj;
			password = pswdDto.getPassword();
			currentPassword = pswdDto.getCurrentPassword();
		}
		
        return !password.equals(currentPassword);
	}
}
