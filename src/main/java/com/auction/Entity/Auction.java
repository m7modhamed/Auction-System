package com.auction.Entity;

import com.auction.Enums.Address;
import com.auction.security.entites.Account;
import com.auction.security.entites.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "Auction")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "begin_Date", nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime beginDate;

    @Column(name = "expire_Date", nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime expireDate;

    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Item item;

    @Column(nullable = false )
    @Enumerated(EnumType.STRING)
    private Address location;

    @Column
    private double commission;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", referencedColumnName = "id", nullable = false)
    @JsonManagedReference
    private User seller;


    @Column(nullable = false)
    private float minBid;

    @Column(nullable = false)
    private float initialPrice;

    @Column(nullable = false)
    private double currentPrice;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "auction")
    @JsonManagedReference
    private List<Bid> bids;


}
