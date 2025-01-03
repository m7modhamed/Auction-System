package com.auction.paymentmanagement.model;

import com.auction.auctionmanagement.model.Auction;
import com.auction.paymentmanagement.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
@EqualsAndHashCode
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String PaymentIntentId;

    @Column(nullable = false)
    private float amountInCent;

    @Column
    private String currency;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String status;

    @ManyToOne
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "auction_id" ,referencedColumnName = "id" ,nullable = false)
    private Auction auction;


}
