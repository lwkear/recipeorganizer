package net.kear.recipeorganizer.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.kear.recipeorganizer.persistence.dto.ChangePasswordDto;
import net.kear.recipeorganizer.persistence.dto.NewPasswordDto;
import net.kear.recipeorganizer.persistence.dto.UserDto;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {   
    
	@Override
    public void initialize(PasswordMatch constraintAnnotation) {       
    }
    
	@Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
		String password = "";
		String confirmPassword = "";
		
		if (obj instanceof UserDto) {
			UserDto user = (UserDto) obj;
			password = user.getPassword();
			confirmPassword = user.getConfirmPassword();
		}
		if (obj instanceof NewPasswordDto) {
			NewPasswordDto pswdDto = (NewPasswordDto) obj;
			password = pswdDto.getPassword();
			confirmPassword = pswdDto.getConfirmPassword();
		}
		if (obj instanceof ChangePasswordDto) {
			ChangePasswordDto pswdDto = (ChangePasswordDto) obj;
			password = pswdDto.getPassword();
			confirmPassword = pswdDto.getConfirmPassword();
		}
		
        return password.equals(confirmPassword);
    }     
}