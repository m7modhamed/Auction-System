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

    @Column(name = "transaction_Time" , nullable = false)
    private Date transactionTime;

    @Column(nullable = false)
    private float amount;

    @Column
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "sender_id",referencedColumnName = "id",nullable = false)
    private PaymentAccount sender;

    @OneToOne
    @JoinColumn(name = "receiver_id",referencedColumnName = "id",nullable = false)
    private PaymentAccount receiver;

}
