package com.auction.auctionmanagement.service.implementation;

import com.auction.auctionmanagement.model.AuctionImage;
import com.auction.auctionmanagement.repository.ImageRepository;
import com.auction.auctionmanagement.service.interfaces.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class imageService implements IImageService {
    private final ImageRepository imageRepository;


    @Override
    public AuctionImage save(AuctionImage auctionImage) {
        return imageRepository.save(auctionImage);
    }
}
