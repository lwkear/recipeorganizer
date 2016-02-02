package net.kear.recipeorganizer.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.ANNOTATION_TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = QuantityValidator.class)
@Documented
public @interface Quantity {
    String message() default "Quantity error";
    Class<?>[] groups() default {}; 
    Class<? extends Payload>[] payload() default {};
}
