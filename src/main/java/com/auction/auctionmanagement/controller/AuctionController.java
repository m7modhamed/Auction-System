package com.auction.auctionmanagement.controller;

import com.auction.auctionmanagement.dto.AuctionSearchCriteria;
import com.auction.auctionmanagement.dto.GetAuctionDto;
import com.auction.auctionmanagement.dto.RequestAuctionDto;
import com.auction.auctionmanagement.dto.ResponseAuctionDto;
import com.auction.auctionmanagement.model.Auction;
import com.auction.auctionmanagement.Mapper.IAuctionMapper;
import com.auction.auctionmanagement.service.interfaces.IAuctionService;
import com.auction.common.utility.AccountUtil;
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
        Long accountId = AccountUtil.getCurrentAccountId();
        //creat auction
        Auction auction = auctionService.CreateAuction(requestAuction, accountId);

        ResponseAuctionDto responseAuctionDto = auctionMapper.toResponseAuctionDto(auction);

        return ResponseEntity.created(URI.create("/")).body(responseAuctionDto);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<GetAuctionDto> getAuctionByIdFormUser(@PathVariable Long id) {
        GetAuctionDto getAuctionDto = auctionService.getAuctionDtoById(id);

        return ResponseEntity.ok(getAuctionDto);
    }

    @GetMapping("/guest/{id}")
    public ResponseEntity<ResponseAuctionDto> getAuctionByIdFormGuest(@PathVariable Long id) {
        Auction auction = auctionService.getAuctionById(id);

        return ResponseEntity.ok(auctionMapper.toResponseAuctionDto(auction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuction(@PathVariable Long id) {
        // get user id
        Long accountId = AccountUtil.getCurrentAccountId();

        auctionService.deleteAuctionById(id,accountId);

        return ResponseEntity.ok("Auction with ID " + id + " deleted successfully.");
    }

    @GetMapping("/isDeleteFree/{id}")
    public ResponseEntity<Boolean> isOverDeleteTime(@PathVariable Long id){

        return ResponseEntity.ok(auctionService.canDeleteWithoutCharge(id));
    }

    @PostMapping("/all")
    public ResponseEntity<Page<ResponseAuctionDto>> getAuctions(
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String[] sortBy,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
            @RequestBody AuctionSearchCriteria searchCriteria
            ) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());

        if (sortBy == null || sortBy.length == 0) {
            sortBy = new String[] { "expireDate" };
        }
        Sort sort = Sort.by(direction, sortBy);

        PageRequest pageRequest = PageRequest.of(offset, pageSize, sort);

        Page<Auction> auctionPage = auctionService.getActiveAuctions(searchCriteria, pageRequest);

        Page<ResponseAuctionDto> responseAuctionPage = auctionPage.map(auctionMapper::toResponseAuctionDto);

        return ResponseEntity.ok(responseAuctionPage);
    }



    @GetMapping("/myAuctions")
    public ResponseEntity<List<ResponseAuctionDto>> getMyAuctions() {
        Long accountId = AccountUtil.getCurrentAccountId();

        List<Auction> auctions = auctionService.getMyAuctions(accountId);

        List<ResponseAuctionDto> responseAuction=new ArrayList<>();
        for(Auction auction: auctions){
            responseAuction.add(auctionMapper.toResponseAuctionDto(auction));
        }
        return ResponseEntity.ok(responseAuction);
    }

    @GetMapping("/myWonAuctions")
    public ResponseEntity<List<ResponseAuctionDto>> getWonAuctions() {
        Long accountId = AccountUtil.getCurrentAccountId();

        List<Auction> auctions = auctionService.getMyWonAuctions(accountId);

        List<ResponseAuctionDto> responseAuction=new ArrayList<>();
        for(Auction auction: auctions){
            responseAuction.add(auctionMapper.toResponseAuctionDto(auction));
        }
        return ResponseEntity.ok(responseAuction);
    }


    @GetMapping("/join/{auctionId}")
    public ResponseEntity<String> joinAuction(@PathVariable long auctionId){

        auctionService.joinAuction(auctionId);

        return ResponseEntity.ok("joined auction successfully.");
    }

    @GetMapping("/{auctionId}/receive")
    public ResponseEntity<String> receiveAuctionItem(@PathVariable Long auctionId){

        auctionService.receiveAuctionItem(auctionId);

        return ResponseEntity.ok("The item for auction ID " + auctionId + " has been successfully received.");
    }

}
