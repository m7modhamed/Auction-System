package com.auction.validation;

import com.auction.Enums.Address;
import com.auction.Enums.ItemStatus;
import com.auction.validation.customAnnotations.ValidItemStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemStatusConstraintValidator implements ConstraintValidator<ValidItemStatus,String> {
    @Override
    public void initialize(ValidItemStatus constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        ItemStatus[] itemStatuses= ItemStatus.values();
        for(ItemStatus status: itemStatuses){
            if(status.name().equals(s)){
                return true;
            }
        }
        return false;
    }
}
