package com.auction.Entity;

import com.auction.Enums.Address;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;


    @Column(nullable = false)
    private Boolean active = true;

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
    private Address address;

    @Column
    private double commission;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", referencedColumnName = "id", nullable = false)
    @JsonManagedReference
    private User seller;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_id", referencedColumnName = "id")
    @JsonManagedReference
    private User winner;


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
