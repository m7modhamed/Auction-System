package com.auction.Entity;

import com.auction.Enums.PaymentMethod;
import com.auction.security.entites.Account;
import com.auction.security.entites.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "Payment_Account")
public class PaymentAccount {

    @Id
    private String customerId;

    @JoinColumn(name = "user_id", referencedColumnName = "id",nullable = false,unique = true)
    @OneToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    private User owner;






}
