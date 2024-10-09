package com.auction.Service.Implementation;

import com.auction.Dtos.AuctionSearchCriteria;
import com.auction.Dtos.GetAuctionDto;
import com.auction.Dtos.RequestAuctionDto;
import com.auction.Entity.Auction;
import com.auction.Entity.Category;
import com.auction.Enums.Address;
import com.auction.Enums.ItemStatus;
import com.auction.Mappers.IAuctionMapper;
import com.auction.Repository.AuctionRepository;
import com.auction.Repository.AuctionSpecification;
import com.auction.Repository.CategoryRepository;
import com.auction.Service.Interfaces.IAccountService;
import com.auction.Service.Interfaces.IAuctionService;
import com.auction.Service.Interfaces.IUserService;
import com.auction.exceptions.AppException;
import com.auction.Entity.User;
import com.auction.utility.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuctionService implements IAuctionService {

    private final IAuctionMapper auctionMapper;
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final IAccountService accountService;

    private static final int DELETION_TIME_LIMIT =20;
    private final IUserService userService;

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
        * We have to charge the seller reserved amount to ensure the seriousness of the auction
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
    public GetAuctionDto getAuctionDtoById(Long auctionId) {

        Optional<Auction> auction = auctionRepository.findById(auctionId);
        if(auction.isEmpty()){
            throw new AppException("Auction not found", HttpStatus.NOT_FOUND);
        }

        User user=(User)Utility.getCurrentAccount();

        GetAuctionDto getAuctionDto=auctionMapper.toGetAuctionDto(auction.get());
        if(user.getJoinedAuctions().contains(auction.get())){
            getAuctionDto.setJoined(true);
        }
        return getAuctionDto;

    }


    @Override
    public boolean canDeleteWithoutCharge(Long id) {
        Optional<Auction> auction=auctionRepository.findById(id);

        if(auction.isEmpty()){
            throw new AppException("Auction not found", HttpStatus.NOT_FOUND);
        }
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime deletionAllowedTime = auction.get().getBeginDate().plusMinutes(DELETION_TIME_LIMIT);

        return currentTime.isBefore(deletionAllowedTime);

    }

    @Override
    public void deleteAuctionById(Long id, Long userId) {
        Optional<Auction> auction=auctionRepository.findById(id);
        Optional<User> user= accountService.getUserById(userId);

        // validation before delete the auction
        deleteAuctionValidation(user,auction);

        //check the time before deletion
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime deletionAllowedTime = auction.get().getBeginDate().plusMinutes(DELETION_TIME_LIMIT);
        if(currentTime.isBefore(deletionAllowedTime)) {
            //here we must refund the reserved amount again to the user account
            //******
            //********
            auctionRepository.deleteById(id);
        }else{
            // throw new AppException("Deletion is not allowed after " + DELETION_TIME_LIMIT + " minutes from auction start time.", HttpStatus.BAD_REQUEST);
            //here we don't need to refund the reserved amount to the user account
            auctionRepository.deleteById(id);

        }


    }

    private void deleteAuctionValidation(Optional<User> user, Optional<Auction> auction){

        if(user.isEmpty()){
            throw new AppException("the user dose not exist",HttpStatus.NOT_FOUND);
        }

        if(auction.isEmpty()){
            throw new AppException("the auction dose not exist",HttpStatus.NOT_FOUND);
        }


        //check the user is already owner if auction wants to delete
        if(!user.get().getMyAuctions().contains(auction.get())){
            throw new AppException("The user attempting to delete the auction is not the owner.", HttpStatus.BAD_REQUEST);

        }

    }

    @Override
    public List<Auction> getMyAuctions(Long userId) {
        Optional<User> user= accountService.getUserById(userId);
        if(user.isEmpty()){
            throw new AppException("the user dose not exist",HttpStatus.NOT_FOUND);
        }

        return user.get().getMyAuctions();
    }

    @Override
    public List<Auction> getMyWonAuctions(Long userId) {
        Optional<User> user= accountService.getUserById(userId);
        if(user.isEmpty()){
            throw new AppException("the user dose not exist",HttpStatus.NOT_FOUND);
        }

        return user.get().getWonAuctions();
    }

    @Override
    public List<Auction> getActiveAuctions() {
        return auctionRepository.findByActiveTrue();
    }

    @Override
    public void joinAuction(long auctionId) {
        User user =(User) Utility.getCurrentAccount();
        Auction auction = getAuctionById(auctionId);

        if(!auction.getActive()){
            throw new AppException("You are attempting to join an expired auction.", HttpStatus.BAD_REQUEST);
        }
        if(user.getMyAuctions().contains(auction)){
            throw new AppException("You are trying to join your own auction.", HttpStatus.CONFLICT);
        }
        user.getJoinedAuctions().add(auction);

        //here must charge the reserved amount for the user to join the auction
        //******
        //********

        userService.save(user);
    }


    @Override
    public Page<Auction> getActiveAuctions(AuctionSearchCriteria criteria, PageRequest pageRequest) {

        String strItemStatus= criteria.getItemStatus();
        List<String> strAddress = criteria.getAddress();
        ItemStatus itemStatus;
        List<Address> address;
        if(strItemStatus.isEmpty() || strItemStatus.isBlank()){
             itemStatus=null;
        }else{
             itemStatus=ItemStatus.valueOf(strItemStatus);
        }

        if(strAddress.isEmpty()){
            address=null;
        }else {

            address=strAddress.stream().map((s)->{
                        if(!s.isEmpty() || !s.isBlank()) {
                            return Address.valueOf(s.toUpperCase());
                        }else{
                            return null;
                        }
                    }).filter(Objects::nonNull)
                    .collect(Collectors.toList());


        }
        Specification<Auction> spec= Specification.where(AuctionSpecification.hasName(criteria.getSearchKey()))
                .or(AuctionSpecification.hasDescription(criteria.getSearchKey()))
                .and(AuctionSpecification.hasItemStatus(itemStatus))
                .and(AuctionSpecification.hasCategory(criteria.getCategory()))
                .and(AuctionSpecification.hasDateRange(criteria.getBeginDate() , criteria.getExpireDate()))
                .and(AuctionSpecification.hasAddress(address))
                .and(AuctionSpecification.hasPriceBetween(criteria.getMinCurrentPrice(), criteria.getMaxCurrentPrice()))
                .and(AuctionSpecification.hasActive());

        return auctionRepository.findAll(spec, pageRequest);
    }

}
