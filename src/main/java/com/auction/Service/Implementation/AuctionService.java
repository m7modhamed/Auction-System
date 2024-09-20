package com.auction.Service.Implementation;

import com.auction.Dtos.RequestAuctionDto;
import com.auction.Entity.Auction;
import com.auction.Entity.Category;
import com.auction.Mappers.IAuctionMapper;
import com.auction.Repository.AuctionRepository;
import com.auction.Repository.CategoryRepository;
import com.auction.Service.Interfaces.IAuctionService;
import com.auction.exceptions.AppException;
import com.auction.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuctionService implements IAuctionService {

    private final IAuctionMapper auctionMapper;
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final AccountService accountService;

    private static final int DELETION_TIME_LIMIT =20;

    @Override
    public Auction CreateAuction(RequestAuctionDto requestAuctionDto,Long userId) {
        Auction newAuction=auctionMapper.toAuction(requestAuctionDto);

        newAuction.setBeginDate(LocalDateTime.now());

        if(requestAuctionDto.getExpireDate().isBefore(LocalDateTime.now().plusHours(1))){
            throw new AppException("The expiration time must be at least after 1 hour from now",HttpStatus.BAD_REQUEST);
        }

        Optional<Category> category=categoryRepository.findById(requestAuctionDto.getItem().getCategory().getId());

        if(category.isEmpty()){
            throw new AppException("Category not found", HttpStatus.NOT_FOUND);
        }
        newAuction.getItem().setCategory(category.get());

        Optional<User> user= accountService.getUserById(userId);
        if(user.isEmpty()){
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        newAuction.setSeller(user.get());
        newAuction.setCurrentPrice(newAuction.getInitialPrice());


        //handle payment charge
        /*
        * We have to charge the seller a temporary amount to ensure the seriousness of the auction
        *
        * the amount will be percentage of initial price 
        * */
       return auctionRepository.save(newAuction);

    }

    @Override
    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }

    @Override
    public Auction getAuctionById(Long auctionId) {

        Optional<Auction> auction = auctionRepository.findById(auctionId);
        if(auction.isEmpty()){
            throw new AppException("Auction not found", HttpStatus.NOT_FOUND);
        }

        return auction.get();
    }

    @Override
    public void deleteAuctionById(Long id, Long userId) {
        Optional<Auction> auction=auctionRepository.findById(id);
        Optional<User> user= accountService.getUserById(userId);

        deleteAuctionValidation(user,auction);


        auctionRepository.deleteById(id);
    }

    @Override
    public List<Auction> getMyAuctions(Long userId) {
        Optional<User> user= accountService.getUserById(userId);
        if(user.isEmpty()){
            throw new AppException("the user dose not exist",HttpStatus.NOT_FOUND);
        }

        return user.get().getOwnAuctions();
    }

    @Override
    public List<Auction> getMyWonAuctions(Long userId) {
        Optional<User> user= accountService.getUserById(userId);
        if(user.isEmpty()){
            throw new AppException("the user dose not exist",HttpStatus.NOT_FOUND);
        }

        return user.get().getWonAuctions();
    }


    private void deleteAuctionValidation(Optional<User> user, Optional<Auction> auction){

        if(user.isEmpty()){
            throw new AppException("the user dose not exist",HttpStatus.NOT_FOUND);
        }

        if(auction.isEmpty()){
            throw new AppException("the auction dose not exist",HttpStatus.NOT_FOUND);
        }

        //check the time before deletion
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime deletionAllowedTime = auction.get().getBeginDate().plusMinutes(DELETION_TIME_LIMIT);
        if (currentTime.isAfter(deletionAllowedTime)) {
            throw new AppException("Deletion is not allowed after " + DELETION_TIME_LIMIT + " minutes from auction start time.", HttpStatus.BAD_REQUEST);
        }

        //check the user is already owner if auction wants to delete
        if(!user.get().getOwnAuctions().contains(auction.get())){
            throw new AppException("The user attempting to delete the auction is not the owner.", HttpStatus.BAD_REQUEST);

        }

    }

    @Override
    public Page<Auction> getActiveAuctions(PageRequest pageRequest) {

        return auctionRepository.findByActiveTrue(pageRequest);
    }

    @Override
    public List<Auction> getActiveAuctions() {
        return auctionRepository.findByActiveTrue();
    }


}
