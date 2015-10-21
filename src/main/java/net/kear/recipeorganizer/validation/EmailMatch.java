package net.kear.recipeorganizer.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailMatchValidator.class)
@Documented
public @interface EmailMatch { 
    String message() default "email mismatch";
    Class<?>[] groups() default {}; 
    Class<? extends Payload>[] payload() default {};
}

