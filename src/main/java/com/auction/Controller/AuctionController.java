package com.auction.Controller;

import com.auction.Dtos.RequestAuctionDto;
import com.auction.Dtos.ResponseAuctionDto;
import com.auction.Entity.Auction;
import com.auction.Mappers.AuctionMapper;
import com.auction.Service.Interfaces.IAuctionService;
import com.auction.Entity.Account;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auction")
public class AuctionController {

    private final AuctionMapper auctionMapper;
    private final IAuctionService auctionService;


    @PostMapping("/create")
    public ResponseEntity<ResponseAuctionDto> createAuction(@RequestBody @Valid RequestAuctionDto requestAuction) {
        // get user id
        Long userId =getCurrentUserId();
        //creat auction
        Auction auction = auctionService.CreateAuction(requestAuction, userId);

        ResponseAuctionDto responseAuctionDto = auctionMapper.toResponseAuctionDto(auction);

        return ResponseEntity.created(URI.create("/")).body(responseAuctionDto);
    }


    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteAuction(@PathVariable Long id) {
        // get user id
        Long userId =getCurrentUserId();

        auctionService.deleteAuctionById(id,userId);

        return ResponseEntity.ok("Auction with ID " + id + " deleted successfully.");
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResponseAuctionDto>> getActiveAuctions() {
        List<Auction> auctions = auctionService.getActiveAuctions();

        List<ResponseAuctionDto> responseAuction=new ArrayList<>();
        for(Auction auction: auctions){
            responseAuction.add(auctionMapper.toResponseAuctionDto(auction));
        }
        return ResponseEntity.ok(responseAuction);
    }

    @GetMapping("/myAuctions")
    public ResponseEntity<List<ResponseAuctionDto>> getMyAuctions() {
        Long userId =getCurrentUserId();


        List<Auction> auctions = auctionService.getMyAuctions(userId);

        List<ResponseAuctionDto> responseAuction=new ArrayList<>();
        for(Auction auction: auctions){
            responseAuction.add(auctionMapper.toResponseAuctionDto(auction));
        }
        return ResponseEntity.ok(responseAuction);
    }

    @GetMapping("/myWonAuctions")
    public ResponseEntity<List<ResponseAuctionDto>> getWonAuctions() {
        Long userId =getCurrentUserId();


        List<Auction> auctions = auctionService.getMyWonAuctions(userId);

        List<ResponseAuctionDto> responseAuction=new ArrayList<>();
        for(Auction auction: auctions){
            responseAuction.add(auctionMapper.toResponseAuctionDto(auction));
        }
        return ResponseEntity.ok(responseAuction);
    }


    private Long getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       return ((Account) authentication.getPrincipal()).getId();
    }

}
