package net.kear.recipeorganizer.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MaintStartTimeValidator implements ConstraintValidator<MaintStartTime, String> {

	@Override
	public void initialize(MaintStartTime constraintAnnotation) {
	}

	@Override
	public boolean isValid(String str, ConstraintValidatorContext context) {
		if (str == null) {
	        return false;
	    }
		str = str.trim();
	    int length = str.length();
	    if (length == 0)
	        return false;
	    if (str.length() != 5)
	    	return false;
		if (str.charAt(2) != ':')
			return false;
		if (!str.matches("[0-9:]+"))
			return false;
		int hour = Integer.parseInt(str.substring(0, 2));
		if (hour > 23)
			return false;
		int minute = Integer.parseInt(str.substring(3));
		if (minute > 59)
			return false;

	    return true;
	}
}
