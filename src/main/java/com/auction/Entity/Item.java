package com.auction.Entity;

import com.auction.Enums.ItemStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Entity
@Data
@Table(name = "Item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false )
    private String name;

    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> images;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemStatus ItemStatus;

    @JoinColumn(name = "category_id" , referencedColumnName = "id" ,nullable = false )
    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;


    @ElementCollection
    private Map<String,String> categoryAttributes;


}
