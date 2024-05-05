package com.auction.validation;

import com.auction.Enums.Address;
import com.auction.validation.customAnnotations.ValidAddress;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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