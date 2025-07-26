package com.example.blitzbuy.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Heather
 * @version 1.0
 *
 * Custom validation annotation to verify mobile phone number format.
 *
 * <p>Validates whether the annotated field/method parameter conforms to a
 * standard mobile number pattern. The validation logic is implemented in
 * {@link IsMobileValidator}.
 *
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {IsMobileValidator.class})
public @interface IsMobile {

    /**
     * Defines the default error message template when validation fails
     * @return The error message template
     */
    String message() default "Invalid phone number format";

    /**
     * Determines if the field is mandatory
     * @return true if the field is required (default), false otherwise
     */
    boolean required() default true;

    /**
     * Specifies validation groups for conditional validation
     * @return Array of group classes
     */
    Class<?>[] groups() default {};

    /**
     * Used to associate metadata with the constraint
     * @return Array of payload classes
     */
    Class<? extends Payload>[] payload() default {}; // default parameter

}
