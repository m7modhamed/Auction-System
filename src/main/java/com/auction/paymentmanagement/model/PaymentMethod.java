package com.auction.paymentmanagement.model;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class PaymentMethod {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String StripePaymentMethodId;

    @OneToOne(mappedBy = "paymentMethod")
    private PaymentAccount paymentAccount;

    private String type;

    private String last4;

    private String expMonth;

    private String expYear;

    @OneToMany(mappedBy = "paymentMethod" , cascade = CascadeType.ALL)
    private List<Transaction> transactions;

}
