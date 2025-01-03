package com.auction.paymentmanagement.model;

import com.auction.usersmanagement.model.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "Payment_Account")
public class PaymentAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String customerId;

    private String stripeAccountId;

    @JoinColumn(name = "user_id", referencedColumnName = "id",nullable = false,unique = true)
    @OneToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    private User owner;

    @OneToOne(cascade = CascadeType.ALL)
    private PaymentMethod paymentMethod;




}
