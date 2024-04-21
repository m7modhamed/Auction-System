package com.auction.Service.Implementation;

import com.auction.Dtos.RequestAuctionDto;
import com.auction.Entity.Auction;
import com.auction.Entity.Category;
import com.auction.Mappers.AuctionMapper;
import com.auction.Mappers.CategoryMapper;
import com.auction.Repository.AuctionRepository;
import com.auction.Repository.CategoryRepository;
import com.auction.Service.Interfaces.IAuctionService;
import com.auction.security.entites.User;
import com.auction.security.exceptions.AppException;
import com.auction.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuctionService implements IAuctionService {

    private final AuctionMapper auctionMapper;
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserRepository userRepository;

    @Override
    public Auction CreateAuction(RequestAuctionDto requestAuctionDto,Long userId){
        Auction newAuction=auctionMapper.toAuction(requestAuctionDto);

        newAuction.setBeginDate(LocalDateTime.now());
        Optional<Category> category=categoryRepository.findById(requestAuctionDto.getItem().getCategory().getId());

        if(category.isEmpty()){
            throw new AppException("Category not found", HttpStatus.NOT_FOUND);
        }
        newAuction.getItem().setCategory(category.get());

        Optional<User> user= userRepository.findById(userId);
        if(user.isEmpty()){
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        newAuction.setSeller(user.get());
        newAuction.setCurrentPrice(newAuction.getInitialPrice());

       return auctionRepository.save(newAuction);

    }

    @Override
    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }

    @Override
    public Optional<Auction> getAuctionById(Long auctionId) {
        return auctionRepository.findById(auctionId);
    }

}
