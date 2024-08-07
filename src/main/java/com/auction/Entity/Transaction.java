package com.auction.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PaymentAccount_id" ,referencedColumnName = "customerId" ,nullable = false)
    private PaymentAccount account;

    @Column(nullable = false)
    private float amount;

    @Column
    private String description;

    @Column
    private String invoiceNumber;

    @ManyToOne
    @JoinColumn(name = "auction_id" ,referencedColumnName = "id" ,nullable = false)
    private Auction auction;


}
