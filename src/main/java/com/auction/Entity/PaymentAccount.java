package com.auction.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

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
