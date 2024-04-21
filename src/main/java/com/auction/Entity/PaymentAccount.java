package com.auction.Entity;

import com.auction.Enums.PaymentMethod;
import com.auction.security.entites.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "Payment_Account")
public class PaymentAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", referencedColumnName = "id",nullable = false,unique = true)
    @OneToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    private User owner;

    @Column(name = "card_Number" ,unique = true,nullable = false)
    private String cardNumber;

    @OneToMany(mappedBy = "sender")
    private List<Transaction> transactions;

    @Column(name = "payment_Method" ,nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;


}
