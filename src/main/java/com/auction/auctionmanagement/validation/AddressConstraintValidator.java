package com.auction.auctionmanagement.validation;

import com.auction.auctionmanagement.enums.Address;
import com.auction.auctionmanagement.validation.customAnnotations.ValidAddress;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddressConstraintValidator implements ConstraintValidator<ValidAddress, String> {

        @Override
        public void initialize(ValidAddress constraintAnnotation) {
            ConstraintValidator.super.initialize(constraintAnnotation);

        }

        @Override
        public boolean isValid(String anEnum, ConstraintValidatorContext constraintValidatorContext) {

            Address[] addresses= Address.values();
            for(Address address: addresses){
                if(address.name().equals(anEnum)){
                    return true;
                }
            }
            return false;
        }
    }