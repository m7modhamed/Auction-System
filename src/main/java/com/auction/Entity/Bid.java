package com.auction.Entity;

import com.auction.security.entites.Account;
import com.auction.security.entites.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "Bid")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bid_time",nullable = false )
    @CreationTimestamp
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime bidTime;

    @Column(nullable = false )
    private double amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "auction_id", referencedColumnName = "id" ,nullable = false)
    @JsonBackReference
    private Auction auction;

    @ManyToOne
    @JoinColumn(name = "bidder_id" , referencedColumnName = "id" ,nullable = false)
    private User bidder;
}
