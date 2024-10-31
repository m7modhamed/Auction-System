package com.auction.auctionmanagement.model;

import com.auction.auctionmanagement.enums.ItemStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Entity
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false )
    private String name;

    private String description;


    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id" , referencedColumnName = "id")
    private List<AuctionImage> auctionImages;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    @JoinColumn(name = "category_id" , referencedColumnName = "id" ,nullable = false )
    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String,String> categoryAttributes;


}
