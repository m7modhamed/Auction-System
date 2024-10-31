package com.auction.auctionmanagement.model;

import com.auction.auctionmanagement.enums.Address;
import com.auction.auctionmanagement.enums.AuctionStatus;
import com.auction.usersmanagement.model.User;
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
    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id", referencedColumnName = "id")
    @JsonBackReference
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


    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "JoinedAuctions")
    private List<User> participants;

}
