package com.auction.usersmanagement.model;

import com.auction.auctionmanagement.model.Auction;
import com.auction.paymentmanagement.model.PaymentAccount;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_user")
public class User extends SysAccount {


    @OneToOne(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private PaymentAccount paymentAccount;


    @OneToMany(mappedBy = "seller", fetch = FetchType.EAGER)
    private List<Auction> myAuctions;


    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_id", referencedColumnName = "id")
    @JsonManagedReference
    private List<Auction> wonAuctions;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Joined-auctions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "auction_id"))
    @JsonManagedReference
    private List<Auction> JoinedAuctions;




}



