package com.auction.paymentmanagement.utility;

public class PriceCalculator {


    public static double calculateReservedAmount(double initialPrice) {
        double reservedAmount;

        if (initialPrice >= 1 && initialPrice <= 100) {
            reservedAmount = initialPrice * 0.10; // 10%
        } else if (initialPrice >= 101 && initialPrice <= 1000) {
            reservedAmount = initialPrice * 0.07; // 7%
        } else if (initialPrice >= 1001 && initialPrice <= 5000) {
            reservedAmount = initialPrice * 0.05; // 5%
        } else if (initialPrice >= 5001 && initialPrice <= 10000) {
            reservedAmount = initialPrice * 0.03; // 3%
        } else if (initialPrice > 10000) {
            reservedAmount = initialPrice * 0.02; // 2%
        } else {
            reservedAmount = 0; // Default case, if needed
        }

        return reservedAmount;
    }

}
