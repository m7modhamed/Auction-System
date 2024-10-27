package com.auction.auctionmanagement.validation.customAnnotations;

import com.auction.auctionmanagement.validation.ItemStatusConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ItemStatusConstraintValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ValidItemStatus {

    String message() default "The Item Status must be one of [NEW , USED]";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
