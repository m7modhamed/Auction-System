package com.auction.Controller;

import com.auction.Dtos.AuctionSearchCriteria;
import com.auction.Dtos.GetAuctionDto;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    public ResponseEntity<GetAuctionDto> getAuctionById(@PathVariable Long id) {
        GetAuctionDto getAuctionDto = auctionService.getAuctionDtoById(id);

        return ResponseEntity.ok(getAuctionDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuction(@PathVariable Long id) {
        // get user id
        Long accountId =Utility.getCurrentAccountId();

        auctionService.deleteAuctionById(id,accountId);

        return ResponseEntity.ok("Auction with ID " + id + " deleted successfully.");
    }

    @GetMapping("/isDeleteFree/{id}")
    public ResponseEntity<Boolean> isOverDeleteTime(@PathVariable Long id){

        return ResponseEntity.ok(auctionService.canDeleteWithoutCharge(id));
    }

    @GetMapping
    public ResponseEntity<Page<ResponseAuctionDto>> getAuctions(
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String[] sortBy,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
            @RequestParam(value = "searchKey", required = false) String searchKey,
            @RequestParam(value = "itemStatus", required = false) String itemStatus,
            @RequestParam(value = "category", required = false) String[] category,
            @RequestParam(value = "beginDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") LocalDateTime beginDate,
            @RequestParam(value = "expireDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") LocalDateTime expireDate,
            @RequestParam(value = "address", required = false) String[] address,
            @RequestParam(value = "minCurrentPrice", required = false, defaultValue = "0") Double minCurrentPrice,
            @RequestParam(value = "maxCurrentPrice", required = false , defaultValue = "0") Double maxCurrentPrice

    ) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());

        if (sortBy == null || sortBy.length == 0) {
            sortBy = new String[] { "expireDate" };
        }
        Sort sort = Sort.by(direction, sortBy);

        PageRequest pageRequest = PageRequest.of(offset, pageSize, sort);

        AuctionSearchCriteria criteria = new AuctionSearchCriteria();
        criteria.setSearchKey(searchKey);
        criteria.setItemStatus(itemStatus);
        criteria.setCategory(Arrays.stream(category).toList());
        criteria.setBeginDate(beginDate);
        criteria.setExpireDate(expireDate);
        criteria.setAddress(Arrays.stream(address).toList());
        criteria.setMinCurrentPrice(minCurrentPrice);
        criteria.setMaxCurrentPrice(maxCurrentPrice);

        Page<Auction> auctionPage = auctionService.getActiveAuctions(criteria, pageRequest);

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


    @GetMapping("/join/{auctionId}")
    public ResponseEntity<String> joinAuction(@PathVariable long auctionId){

        auctionService.joinAuction(auctionId);

        return ResponseEntity.ok("joined auction successfully.");
    }


}
