package com.auction.Controller;

import com.auction.Dtos.RequestAuctionDto;
import com.auction.Dtos.ResponseAuctionDto;
import com.auction.Entity.Auction;
import com.auction.Mappers.AuctionMapper;
import com.auction.Service.Interfaces.IAuctionService;
import com.auction.Service.Interfaces.IBidService;
import com.auction.security.entites.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController

@RequestMapping("/auction")
public class AuctionController {

    private final AuctionMapper auctionMapper;
    private final IAuctionService auctionService;


    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseAuctionDto> createAuction(@RequestPart("requestAuction") @Valid RequestAuctionDto requestAuction,
                                                            @RequestPart("images") List<MultipartFile> images) throws IOException {
        // get user id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();

        //creat auction
        Auction auction = auctionService.CreateAuction(requestAuction,images, userId);

        ResponseAuctionDto responseAuctionDto = auctionMapper.toResponseAuctionDto(auction);

        return ResponseEntity.created(URI.create("/")).body(responseAuctionDto);
    }


    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteAuction(@PathVariable Long id) {
        // get user id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();


        auctionService.deleteAuctionById(id,userId);

        return ResponseEntity.ok("Auction with ID " + id + " deleted successfully.");
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResponseAuctionDto>> getAuctions() {
        List<Auction> auctions = auctionService.getAllAuctions();

        List<ResponseAuctionDto> responseAuction=new ArrayList<>();
        for(Auction auction: auctions){
            responseAuction.add(auctionMapper.toResponseAuctionDto(auction));
        }
        return ResponseEntity.ok(responseAuction);
    }



}
