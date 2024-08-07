package com.auction.security.entites;

import com.auction.Entity.Auction;
import com.auction.Entity.PaymentAccount;
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
    private List<Auction> ownAuctions;


    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_id", referencedColumnName = "id")
    @JsonBackReference
    private List<Auction> WonAuctions;


}



