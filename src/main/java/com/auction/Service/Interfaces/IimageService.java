package com.auction.Service.Interfaces;

import com.auction.Entity.Image;

import java.util.Optional;

public interface IimageService {
    Optional<Image> getByName(String imagePath);

    Image save(Image image);
}
