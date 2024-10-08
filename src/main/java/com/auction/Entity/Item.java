package com.auction.Entity;

import com.auction.Enums.ItemStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Setter
@Getter
@EqualsAndHashCode
@Table(name = "Item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false )
    private String name;

    private String description;


    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id" , referencedColumnName = "id")
    private List<Image> images=new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    @JoinColumn(name = "category_id" , referencedColumnName = "id" ,nullable = false )
    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String,String> categoryAttributes;


}
