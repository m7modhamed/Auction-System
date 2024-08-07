package com.auction.Service.Implementation;

import com.auction.Entity.Image;
import com.auction.Repository.ImageRepository;
import com.auction.Service.Interfaces.IimageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class imageService implements IimageService {
    private final ImageRepository imageRepository;
    @Override
    public Optional<Image> getByName(String imagePath) {
        return imageRepository.findByName(imagePath);
    }
}
