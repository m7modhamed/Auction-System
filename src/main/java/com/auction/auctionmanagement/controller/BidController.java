package com.auction.auctionmanagement.controller;

import com.auction.auctionmanagement.dto.RequestBidDto;
import com.auction.auctionmanagement.dto.ResponseAuctionDto;
import com.auction.auctionmanagement.model.Auction;
import com.auction.auctionmanagement.Mapper.IAuctionMapper;
import com.auction.auctionmanagement.service.interfaces.IBidService;
import com.auction.usersmanagement.model.SysAccount;
import com.auction.common.utility.AccountUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bid")
public class BidController {

    private final IBidService bidService;
    private final IAuctionMapper auctionMapper;

    @PostMapping("/make")
    public ResponseEntity<ResponseAuctionDto> addBid(@RequestBody RequestBidDto requestBidDto){
        Long userId = AccountUtil.getCurrentAccountId();

       Auction updatedAuction=bidService.createNewBid(requestBidDto,userId);

        ResponseAuctionDto responseAuctionDto=auctionMapper.toResponseAuctionDto(updatedAuction);

        return ResponseEntity.ok(responseAuctionDto);
    }


    @GetMapping("/isDeleteFree/{id}")
    public ResponseEntity<Boolean> isOverDeleteTime(@PathVariable Long id){

        return ResponseEntity.ok(bidService.canDeleteBidWithoutCharge(id));
    }


    @DeleteMapping("delete/{bidId}")
    public ResponseEntity<String> deleteBid(@PathVariable Long bidId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((SysAccount) authentication.getPrincipal()).getId();

        bidService.deleteBidById(bidId,userId);

        return ResponseEntity.ok("Bid with ID " + bidId + " deleted successfully.");
    }






}
