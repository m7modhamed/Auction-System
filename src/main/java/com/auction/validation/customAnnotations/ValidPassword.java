package com.auction.validation.customAnnotations;

import com.auction.validation.PasswordConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ValidPassword {

    String message() default "Password must be at least 10" +
            " characters and contain at least one uppercase" +
            " letter, one lowercase letter, one number," +
            " one special character, and no whitespace";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}