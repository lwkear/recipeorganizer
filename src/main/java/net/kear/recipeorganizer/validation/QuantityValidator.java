package net.kear.recipeorganizer.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class QuantityValidator implements ConstraintValidator<Quantity, String> {

	@Override
	public void initialize(Quantity constraintAnnotation) {
	}

	@Override
	public boolean isValid(String str, ConstraintValidatorContext context) {
		str = str.trim();
		
		if (str == null) {
	        return false;
	    }
	    int length = str.length();
	    if (length == 0) {
	        return false;
	    }
	    if ((str.charAt(0) == '/') || (str.charAt(0) == ',') || (str.charAt(0) == '.')) {
	        if (length == 1) {
	            return false;
	        }
	    }
	    for (int i=0; i < length; i++) {
	        char c = str.charAt(i);
	        if ((c < '0' || c > '9') && (c != '/' && c != '.' && c != ',' && c != ' ')) {
	        	return false;
	        }
	    }
	    for (int i=0; i < length; i++) {
	        char c = str.charAt(i);
	        if ((c == '/') && (i+1 >= length))
	        	return false;
	    }

	    return true;
	}
}
