package com.auction.auctionmanagement.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class AuctionImage {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String type;

    private String imageUrl;
}
