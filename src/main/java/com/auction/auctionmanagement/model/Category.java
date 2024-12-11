package com.auction.auctionmanagement.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false ,unique = true)
    private String name;

    private String description;


    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> attributes;

    private String imageUrl;
}
