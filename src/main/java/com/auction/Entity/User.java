package com.auction.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User extends Account{


    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonBackReference
    private PaymentAccount paymentAccount;


    @OneToMany(mappedBy = "seller", fetch = FetchType.EAGER)
    @JsonBackReference
    private List<Auction> myAuctions;


    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_id", referencedColumnName = "id")
    @JsonBackReference
    private List<Auction> wonAuctions;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Joined-auctions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "auction_id"))
    private List<Auction> JoinedAuctions;

}



