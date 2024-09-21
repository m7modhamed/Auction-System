package com.auction.Repository;

import com.auction.Entity.Auction;
import com.auction.Entity.Category;
import com.auction.Entity.Item;
import com.auction.Enums.Address;
import com.auction.Enums.ItemStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionSpecification {


    public static Specification<Auction> hasActive() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("active"),true);
    }

    public static Specification<Auction> hasName(String keySearch) {
        return (root, query, criteriaBuilder) -> {
            if (keySearch == null || keySearch.isEmpty() || keySearch.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            Join<Auction, Item> itemJoin = root.join("item");
            return criteriaBuilder.like(itemJoin.get("name"), "%" + keySearch + "%");
        };
    }


    public static Specification<Auction> hasDescription(String keySearch) {
        return (root, query, criteriaBuilder) -> {
            if (keySearch == null || keySearch.isEmpty() || keySearch.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            Join<Auction, Item> itemJoin = root.join("item");
            return criteriaBuilder.like(itemJoin.get("description"), "%" + keySearch + "%");
        };
    }

    public static Specification<Auction> hasItemStatus(ItemStatus itemStatus) {
        return (root, query, criteriaBuilder) -> {
            if (itemStatus == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Auction, Item> itemJoin = root.join("item");
            return criteriaBuilder.equal(itemJoin.get("itemStatus"),itemStatus);
        };
    }

    public static Specification<Auction> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || category.isEmpty() || category.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            Join<Auction, Item> itemJoin = root.join("item");
            Join<Item, Category> categoryJoin = itemJoin.join("category");
            return criteriaBuilder.equal(categoryJoin.get("name") , category);
        };
    }




    public static Specification<Auction> hasLocation(Address location) {
        return (root, query, criteriaBuilder) -> {
            if (location == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("location") , location);
        };
    }

    public static Specification<Auction> hasDateRange(LocalDateTime beginDate, LocalDateTime expireDate) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (beginDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("beginDate"), beginDate));
            }


            if (expireDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expireDate"), expireDate));
            }


            return predicates.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    public static Specification<Auction> hasPriceBetween(double minPrice, double maxPrice) {
        return (root, query, criteriaBuilder) -> {

            if (minPrice <= 0 && maxPrice <= 0) {
                return criteriaBuilder.conjunction();
            }

            if (minPrice > 0 && maxPrice <= 0) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("currentPrice"), minPrice);
            }
            if (minPrice <= 0 && maxPrice > 0) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("currentPrice"), maxPrice);
            }


            return criteriaBuilder.between(root.get("currentPrice"), minPrice, maxPrice);
        };
    }



}