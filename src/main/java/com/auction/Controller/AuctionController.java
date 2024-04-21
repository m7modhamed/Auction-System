package com.auction.Controller;

import com.auction.Dtos.RequestAuctionDto;
import com.auction.Dtos.ResponseAuctionDto;
import com.auction.Entity.Auction;
import com.auction.Mappers.AuctionMapper;
import com.auction.Service.Interfaces.IAuctionService;
import com.auction.security.entites.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.BindException;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auction")
public class AuctionController {

    private final AuctionMapper auctionMapper;
    private final IAuctionService auctionService;

    @PostMapping("/create")
    public ResponseEntity<ResponseAuctionDto> createAuction(@RequestBody RequestAuctionDto requestAuction) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();

        Auction auction = auctionService.CreateAuction(requestAuction, userId);

        ResponseAuctionDto responseAuctionDto = auctionMapper.toResponseAuctionDto(auction);

        return ResponseEntity.created(URI.create("/")).body(responseAuctionDto);
    }

    public ResponseEntity<Auction> deleteAuction(@RequestBody Auction auction) {


        return ResponseEntity.ok(auction);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Auction>> getAuctions() {
        List<Auction> auctions = auctionService.getAllAuctions();

        return ResponseEntity.ok(auctions);
    }


  /* @ExceptionHandler(BindException.class)
   public ResponseEntity<BindException> handleBindException(BindException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex);
   }*/
}
