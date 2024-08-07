package com.auction.Service.Implementation;

import com.auction.Dtos.RequestAuctionDto;
import com.auction.Entity.Auction;
import com.auction.Entity.Category;
import com.auction.Entity.Image;
import com.auction.Mappers.AuctionMapper;
import com.auction.Repository.AuctionRepository;
import com.auction.Repository.CategoryRepository;
import com.auction.Service.Interfaces.IAuctionService;
import com.auction.security.entites.Account;
import com.auction.exceptions.AppException;
import com.auction.security.entites.User;
import com.auction.security.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuctionService implements IAuctionService {

    private final AuctionMapper auctionMapper;
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Value("${spring.folder.images}")
    private String FOLDER_PATH;

    private static final int DELETION_TIME_LIMIT =20;
    @Override
    public Auction CreateAuction(RequestAuctionDto requestAuctionDto,List<MultipartFile> images,Long userId) {
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


        //handle upload images

        if(images == null || images.isEmpty() || images.size() > 5) {
            throw new AppException("The number of images must be between 1 and 5.", HttpStatus.BAD_REQUEST);
        }
        for (MultipartFile file : images) {
            //get full filePath
            String filePath = FOLDER_PATH + file.getOriginalFilename();

            // Save the image file to the file system
            try {
                file.transferTo(new File(filePath));
            } catch (IOException e) {
                throw new AppException("Failed to save images", HttpStatus.BAD_REQUEST);
            }

            // set image path to the new auction object to save later in database
            Image image= Image.builder()
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .imagePath(filePath)
                    .build();

            newAuction.getItem().getImages().add(image);
        }


        Optional<User> user= userService.getUserById(userId);
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
    public Optional<Auction> getAuctionById(Long auctionId) {
        return auctionRepository.findById(auctionId);
    }

    @Override
    public void deleteAuctionById(Long id, Long userId) {
        Optional<Auction> auction=auctionRepository.findById(id);
        Optional<User> user=userService.getUserById(userId);

        deleteAuctionValidation(user,auction);


        auctionRepository.deleteById(id);
    }

    @Override
    public List<Auction> getMyAuctions(Long userId) {
        Optional<User> user=userService.getUserById(userId);
        if(user.isEmpty()){
            throw new AppException("the user dose not exist",HttpStatus.NOT_FOUND);
        }

        return user.get().getOwnAuctions();
    }

    @Override
    public List<Auction> getMyWonAuctions(Long userId) {
        Optional<User> user=userService.getUserById(userId);
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
    public List<Auction> getActiveAuctions() {

        return auctionRepository.findByActiveTrue();
    }


}
