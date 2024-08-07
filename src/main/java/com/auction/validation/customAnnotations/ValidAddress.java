package com.auction.validation.customAnnotations;

import com.auction.validation.AddressConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = AddressConstraintValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ValidAddress {

    String message() default "The address must be one of [MADABA, MAAN, IRBID, AJLOUN, KARAK, TAFELAH, ZARQA, BALQA, AMMAN, JERASH, AQABA, MAFRAQ]";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
