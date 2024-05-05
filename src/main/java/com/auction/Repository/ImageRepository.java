package com.auction.Repository;

import com.auction.Entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image,Long> {


    Optional<Image> findByName(String imagePath);
}
