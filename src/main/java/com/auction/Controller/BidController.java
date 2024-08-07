package com.auction.Controller;

import com.auction.Dtos.RequestBidDto;
import com.auction.Dtos.ResponseAuctionDto;
import com.auction.Entity.Auction;
import com.auction.Entity.Bid;
import com.auction.Mappers.AuctionMapper;
import com.auction.Mappers.BidMapper;
import com.auction.Service.Interfaces.IBidService;
import com.auction.security.entites.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bid")
public class BidController {

    private final IBidService bidService;
    private final BidMapper bidMapper;
    private final AuctionMapper auctionMapper;

    @PostMapping("/make")
    public ResponseEntity<ResponseAuctionDto> addBid(@RequestBody RequestBidDto requestBidDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((Account) authentication.getPrincipal()).getId();

       Auction updatedAuction=bidService.makeBid(requestBidDto,userId);

        ResponseAuctionDto responseAuctionDto=auctionMapper.toResponseAuctionDto(updatedAuction);

        return ResponseEntity.ok(responseAuctionDto);
    }

    @DeleteMapping("delete/{bidId}")
    public ResponseEntity<String> deleteBid(@PathVariable Long bidId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((Account) authentication.getPrincipal()).getId();

        bidService.deleteBidById(bidId,userId);

        return ResponseEntity.ok("Bid with ID " + bidId + " deleted successfully.");
    }


    public ResponseEntity<List<Bid>> getAllBids(@RequestParam int auctionId){


        return ResponseEntity.ok(Arrays.asList(new Bid()));
    }


}
