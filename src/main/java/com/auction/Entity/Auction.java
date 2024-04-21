package com.auction.Entity;

import com.auction.Enums.Address;
import com.auction.security.entites.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Value;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "Auction")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean status = true;

    @Column(name = "begin_Date", nullable = false)
    @CreationTimestamp
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
    private float commission;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonManagedReference
    private User seller;

    @OneToOne
    @JoinColumn(name = "winner_id" , referencedColumnName = "id")
    private User winner;

    @Column(nullable = false)
    private float minBid;

    @Column(nullable = false)
    private float initialPrice;

    @Column(nullable = false)
    private float currentPrice;

    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.EAGER, mappedBy = "auction")
    @JsonManagedReference
    private List<Bid> bids;


}
