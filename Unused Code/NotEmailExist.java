package net.kear.recipeorganizer.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({FIELD,TYPE,ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = NotEmailExistValidator.class)
@Documented
public @interface NotEmailExist { 
    String message() default "email already registered";	//messages.getMessage("message.notEmailExist", null, request.getLocale())
    Class<?>[] groups() default {}; 
    Class<? extends Payload>[] payload() default {};
}

