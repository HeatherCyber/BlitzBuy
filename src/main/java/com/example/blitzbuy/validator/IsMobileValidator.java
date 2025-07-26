package com.example.blitzbuy.validator;

import com.example.blitzbuy.util.ValidatorUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

/**
 * @author Heather
 * @version 1.0
 * <p>
 * Validator implementation for the {@link IsMobile} annotation.
 *
 * <p>This class contains the actual validation logic to verify if a string
 * conforms to a valid mobile phone number format. The validation behavior
 * can be configured through the {@link IsMobile} annotation parameters.
 */
//我们自拟定注解IsMobile 的校验规则
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    /**
     * Initializes the validator with the constraint annotation configuration.
     *
     * @param constraintAnnotation The {@link IsMobile} annotation instance
     *                             containing the defined parameters
     */
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    /**
     * Performs the actual validation logic.
     *
     * @param value   The input string to be validated
     * @param context Context in which the constraint is evaluated
     * @return {@code true} if the input is valid according to the rules,
     * {@code false} otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (required) {
            // Mandatory validation: value must be non-empty and valid
            return ValidatorUtil.isMobile(value);
        } else {
            // Optional validation: empty value is acceptable, non-empty must be valid
            if (!StringUtils.hasText(value)) {
                return true;
            } else {
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
