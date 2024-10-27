package com.auction.auctionmanagement.validation;

import com.auction.auctionmanagement.enums.ItemStatus;
import com.auction.auctionmanagement.validation.customAnnotations.ValidItemStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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
