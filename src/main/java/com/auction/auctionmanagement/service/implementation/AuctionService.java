package com.auction.auctionmanagement.service.implementation;

import com.auction.auctionmanagement.dto.AuctionSearchCriteria;
import com.auction.auctionmanagement.dto.GetAuctionDto;
import com.auction.auctionmanagement.dto.RequestAuctionDto;
import com.auction.auctionmanagement.enums.Address;
import com.auction.auctionmanagement.enums.AuctionStatus;
import com.auction.auctionmanagement.enums.ItemStatus;
import com.auction.auctionmanagement.Mapper.IAuctionMapper;
import com.auction.auctionmanagement.repository.AuctionRepository;
import com.auction.auctionmanagement.repository.AuctionSpecification;
import com.auction.auctionmanagement.repository.CategoryRepository;
import com.auction.auctionmanagement.model.Auction;
import com.auction.auctionmanagement.model.Category;
import com.auction.auctionmanagement.service.interfaces.IAuctionService;
import com.auction.common.exceptions.AppException;
import com.auction.paymentmanagement.enums.TransactionType;
import com.auction.paymentmanagement.model.PaymentAccount;
import com.auction.paymentmanagement.model.Transaction;
import com.auction.paymentmanagement.service.interfaces.IPaymentService;
import com.auction.paymentmanagement.utility.PriceCalculator;
import com.auction.usersmanagement.model.User;
import com.auction.usersmanagement.service.interfaces.IAccountService;
import com.auction.usersmanagement.service.interfaces.IUserService;
import com.auction.common.utility.AccountUtil;
import com.stripe.exception.StripeException;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionService implements IAuctionService {

    private final IAuctionMapper auctionMapper;
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final IAccountService accountService;
    private final IUserService userService;
    private final IPaymentService paymentService;


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
        newAuction.setStatus(AuctionStatus.ACTIVE);

        //handle payment charge
        //We have to charge the seller reserved amount to ensure the seriousness of the auction
        //the amount will be percentage of initial price
        PaymentAccount paymentAccount = user.get().getPaymentAccount();
        if(paymentAccount.getPaymentMethod() == null){
            throw new AppException("you have to enter payment method", HttpStatus.BAD_REQUEST);
        }

        Auction createdAuction = auctionRepository.save(newAuction);

        try {
            double reservedAmountInCent= PriceCalculator.calculateReservedAmount(createdAuction.getInitialPrice()) * 100;

            paymentService.createPaymentIntent(createdAuction , user.get() ,(long) reservedAmountInCent , TransactionType.CREATE_AUCTION ,  PaymentIntentCreateParams.CaptureMethod.MANUAL);
        } catch (StripeException e) {
            throw new AppException(e.getMessage() , HttpStatus.BAD_REQUEST);
        }

        return createdAuction;
    }


    @Override
    public Auction getAuctionById(Long auctionId) {

       return auctionRepository.findById(auctionId).orElseThrow(
               () -> new AppException("Auction not found", HttpStatus.NOT_FOUND)
       );

    }

    @Override
    public GetAuctionDto getAuctionDtoById(Long auctionId) {

        Auction auction =getAuctionById(auctionId);


        User user=(User) AccountUtil.getCurrentAccount();

        GetAuctionDto getAuctionDto=auctionMapper.toGetAuctionDto(auction);
        if(user.getJoinedAuctions().contains(auction)){
            getAuctionDto.setJoined(true);
        }
        return getAuctionDto;

    }


    @Override
    public boolean canDeleteWithoutCharge(Long id) {
        Auction auction = getAuctionById(id);

        if(!auction.getStatus().equals(AuctionStatus.ACTIVE)){
            return false;
        }
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime deletionAllowedTime = auction.getBeginDate().plusMinutes(DELETION_TIME_LIMIT);

        return currentTime.isBefore(deletionAllowedTime);
    }

    @Override
    public void receiveAuctionItem(Long auctionId) {
        User user=(User)AccountUtil.getCurrentAccount();
        Auction auction = getAuctionById(auctionId);
        User winner= auction.getWinner();
        if(auction.getStatus() != AuctionStatus.TIME_OUT){
            throw new AppException("Receiving this item is only allowed once the auction is fully complete.", HttpStatus.BAD_REQUEST);
        } else if(winner == null){
            throw new AppException("The auction winner has not been determined yet.", HttpStatus.BAD_REQUEST);
        }else if(!user.getId().equals(winner.getId())){
            throw new AppException("You are not the auction winner.", HttpStatus.BAD_REQUEST);
        }

        auction.setStatus(AuctionStatus.RESERVED);
       auctionRepository.save(auction);

        //here we must send the auction price to the seller - the reserved amount

    }

    @Override
    public void deleteAuctionById(Long id, Long userId) {
        Auction auction=getAuctionById(id);

        User user= userService.getUserById(userId);

        // validation before delete the auction
        if(!user.getMyAuctions().contains(auction)){
            throw new AppException("Deletion failed. The user attempting to delete the auction is not the owner.", HttpStatus.BAD_REQUEST);
        }

        if (auction.getStatus() != AuctionStatus.ACTIVE){
            throw new AppException("Deletion failed. The auction must be in an active state to be removed.", HttpStatus.BAD_REQUEST);
        }


        Transaction requiredTransaction=null;
        //here we must refund the reserved amount again to the user account
        for(Transaction transaction: user.getPaymentAccount().getPaymentMethod().getTransactions()){
            if(transaction.getAuction().equals(auction)){
                requiredTransaction=transaction;
                break;
            }
        }


        //check the time before deletion
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime deletionAllowedTime = auction.getBeginDate().plusMinutes(DELETION_TIME_LIMIT);

        if(currentTime.isBefore(deletionAllowedTime)) {
            //here we need to refund the reserved amount to the user account
            try {
                paymentService.cancelPaymentIntent(requiredTransaction);
            } catch (StripeException e) {
                throw new AppException(e.getMessage() , HttpStatus.BAD_REQUEST);
            }
        }else{
            //here we don't need to refund the reserved amount to the user account
            try {
                paymentService.capturePaymentIntent(requiredTransaction);
            } catch (StripeException e) {
                throw new AppException(e.getMessage() , HttpStatus.BAD_REQUEST);
            }
        }

        //and we must refund the reserved amount for each bidder (charged when join the auction)
        returnReservedAmountToBidders(auction);

        auction.setStatus(AuctionStatus.CANCELLED);
        auctionRepository.save(auction);
    }

  public void returnReservedAmountToBidders(Auction auction){
      List<User> joinedUsers = userService.findAllByAuctionId(auction.getId());
      for (User user : joinedUsers) {

          //and we must refund the reserved amount for each bidder (charged when join the auction)
          for(Transaction transaction : user.getPaymentAccount().getPaymentMethod().getTransactions()){
              if(isEligibleToCancelPaymentIntent(auction , transaction)){
                  try {
                      paymentService.cancelPaymentIntent(transaction);
                  } catch (StripeException e) {
                      throw new AppException(e.getMessage(), HttpStatus.BAD_REQUEST);
                  }

              }
          }
      }
  }

  private boolean isEligibleToCancelPaymentIntent(Auction auction , Transaction transaction){

        return transaction.getAuction().equals(auction)
                && transaction.getType() == TransactionType.JOIN_AUCTION
                && transaction.getStatus().equals("requires_capture");
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
    public List<Auction> getAuctionByStatus(AuctionStatus status) {
        return auctionRepository.findByStatus(status);
    }

    @Override
    public void joinAuction(long auctionId) {

        User user =(User) AccountUtil.getCurrentAccount();
        Auction auction = getAuctionById(auctionId);

        if(auction.getStatus() != AuctionStatus.ACTIVE){
            throw new AppException("You are attempting to join an expired auction.", HttpStatus.BAD_REQUEST);
        }
        else if(user.getMyAuctions().contains(auction)){
            throw new AppException("You are trying to join your own auction.", HttpStatus.CONFLICT);
        }else if (user.getJoinedAuctions().contains(auction)){
            throw new AppException("You are already joined the auction.", HttpStatus.CONFLICT);
        }
        user.getJoinedAuctions().add(auction);

        //here must charge the reserved amount for the user to join the auction
        PaymentAccount paymentAccount = user.getPaymentAccount();
        if(paymentAccount.getPaymentMethod() == null){
            throw new AppException("you have to enter payment method", HttpStatus.BAD_REQUEST);
        }

        try {
            double reservedAmountInCent= PriceCalculator.calculateReservedAmount(auction.getInitialPrice()) * 100;
            
            paymentService.createPaymentIntent(auction , user ,(long) reservedAmountInCent,TransactionType.JOIN_AUCTION , PaymentIntentCreateParams.CaptureMethod.MANUAL);
        } catch (StripeException e) {
            throw new AppException(e.getMessage() , HttpStatus.BAD_REQUEST);
        }

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
             itemStatus=ItemStatus.valueOf(strItemStatus.toUpperCase());
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
