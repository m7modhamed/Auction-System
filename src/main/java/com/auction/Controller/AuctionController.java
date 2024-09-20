package com.auction.Controller;

import com.auction.Dtos.RequestAuctionDto;
import com.auction.Dtos.ResponseAuctionDto;
import com.auction.Entity.Auction;
import com.auction.Mappers.IAuctionMapper;
import com.auction.Service.Interfaces.IAuctionService;
import com.auction.utility.Utility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auctions")
public class AuctionController {

    private final IAuctionMapper auctionMapper;
    private final IAuctionService auctionService;


    @PostMapping
    public ResponseEntity<ResponseAuctionDto> createAuction(@RequestBody @Valid RequestAuctionDto requestAuction) {
        // get user id
        Long accountId =Utility.getCurrentAccountId();
        //creat auction
        Auction auction = auctionService.CreateAuction(requestAuction, accountId);

        ResponseAuctionDto responseAuctionDto = auctionMapper.toResponseAuctionDto(auction);

        return ResponseEntity.created(URI.create("/")).body(responseAuctionDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseAuctionDto> getAuctionById(@PathVariable Long id) {
        Auction auction = auctionService.getAuctionById(id);

        ResponseAuctionDto responseAuctionDto = auctionMapper.toResponseAuctionDto(auction);
        return ResponseEntity.ok(responseAuctionDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuction(@PathVariable Long id) {
        // get user id
        Long accountId =Utility.getCurrentAccountId();

        auctionService.deleteAuctionById(id,accountId);

        return ResponseEntity.ok("Auction with ID " + id + " deleted successfully.");
    }



    @GetMapping
    public ResponseEntity<Page<ResponseAuctionDto>> getAuctions(
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy) {

        PageRequest pageRequest = PageRequest.of(offset, pageSize, Sort.by(sortBy));
        Page<Auction> auctionPage = auctionService.getActiveAuctions(pageRequest);

        Page<ResponseAuctionDto> responseAuctionPage = auctionPage.map(auctionMapper::toResponseAuctionDto);

        return ResponseEntity.ok(responseAuctionPage);
    }



    @GetMapping("/myAuctions")
    public ResponseEntity<List<ResponseAuctionDto>> getMyAuctions() {
        Long accountId =Utility.getCurrentAccountId();

        List<Auction> auctions = auctionService.getMyAuctions(accountId);

        List<ResponseAuctionDto> responseAuction=new ArrayList<>();
        for(Auction auction: auctions){
            responseAuction.add(auctionMapper.toResponseAuctionDto(auction));
        }
        return ResponseEntity.ok(responseAuction);
    }

    @GetMapping("/myWonAuctions")
    public ResponseEntity<List<ResponseAuctionDto>> getWonAuctions() {
        Long accountId =Utility.getCurrentAccountId();

        List<Auction> auctions = auctionService.getMyWonAuctions(accountId);

        List<ResponseAuctionDto> responseAuction=new ArrayList<>();
        for(Auction auction: auctions){
            responseAuction.add(auctionMapper.toResponseAuctionDto(auction));
        }
        return ResponseEntity.ok(responseAuction);
    }




}
