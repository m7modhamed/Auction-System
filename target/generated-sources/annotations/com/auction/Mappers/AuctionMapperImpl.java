package com.auction.Mappers;

import com.auction.Dtos.CategoryDto;
import com.auction.Dtos.ItemDto;
import com.auction.Dtos.RequestAuctionDto;
import com.auction.Dtos.ResponseAuctionDto;
import com.auction.Dtos.ResponseBidDto;
import com.auction.Dtos.UserDto;
import com.auction.Entity.Auction;
import com.auction.Entity.Bid;
import com.auction.Entity.Category;
import com.auction.Entity.Item;
import com.auction.security.entites.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-21T13:32:10+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 20.0.2 (Oracle Corporation)"
)
@Component
public class AuctionMapperImpl implements AuctionMapper {

    @Override
    public RequestAuctionDto toRequestAuctionDto(Auction auction) {
        if ( auction == null ) {
            return null;
        }

        RequestAuctionDto.RequestAuctionDtoBuilder requestAuctionDto = RequestAuctionDto.builder();

        requestAuctionDto.title( auction.getTitle() );
        requestAuctionDto.expireDate( auction.getExpireDate() );
        requestAuctionDto.item( itemToItemDto( auction.getItem() ) );
        requestAuctionDto.location( auction.getLocation() );
        requestAuctionDto.minBid( auction.getMinBid() );
        requestAuctionDto.initialPrice( auction.getInitialPrice() );

        return requestAuctionDto.build();
    }

    @Override
    public Auction toAuction(RequestAuctionDto auction) {
        if ( auction == null ) {
            return null;
        }

        Auction auction1 = new Auction();

        auction1.setTitle( auction.getTitle() );
        auction1.setExpireDate( auction.getExpireDate() );
        auction1.setItem( itemDtoToItem( auction.getItem() ) );
        auction1.setLocation( auction.getLocation() );
        auction1.setMinBid( auction.getMinBid() );
        auction1.setInitialPrice( auction.getInitialPrice() );

        return auction1;
    }

    @Override
    public ResponseAuctionDto toResponseAuctionDto(Auction auction) {
        if ( auction == null ) {
            return null;
        }

        ResponseAuctionDto.ResponseAuctionDtoBuilder responseAuctionDto = ResponseAuctionDto.builder();

        responseAuctionDto.title( auction.getTitle() );
        responseAuctionDto.status( auction.isStatus() );
        responseAuctionDto.beginDate( auction.getBeginDate() );
        responseAuctionDto.expireDate( auction.getExpireDate() );
        responseAuctionDto.item( itemToItemDto( auction.getItem() ) );
        responseAuctionDto.location( auction.getLocation() );
        responseAuctionDto.seller( userToUserDto( auction.getSeller() ) );
        responseAuctionDto.minBid( auction.getMinBid() );
        responseAuctionDto.initialPrice( auction.getInitialPrice() );
        responseAuctionDto.currentPrice( auction.getCurrentPrice() );
        responseAuctionDto.bids( bidListToResponseBidDtoList( auction.getBids() ) );

        return responseAuctionDto.build();
    }

    protected CategoryDto categoryToCategoryDto(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDto.CategoryDtoBuilder categoryDto = CategoryDto.builder();

        categoryDto.id( category.getId() );
        categoryDto.name( category.getName() );
        categoryDto.description( category.getDescription() );

        return categoryDto.build();
    }

    protected ItemDto itemToItemDto(Item item) {
        if ( item == null ) {
            return null;
        }

        ItemDto.ItemDtoBuilder itemDto = ItemDto.builder();

        itemDto.name( item.getName() );
        itemDto.description( item.getDescription() );
        List<String> list = item.getImages();
        if ( list != null ) {
            itemDto.images( new ArrayList<String>( list ) );
        }
        itemDto.itemStatus( item.getItemStatus() );
        itemDto.category( categoryToCategoryDto( item.getCategory() ) );

        return itemDto.build();
    }

    protected Category categoryDtoToCategory(CategoryDto categoryDto) {
        if ( categoryDto == null ) {
            return null;
        }

        Category category = new Category();

        category.setId( categoryDto.getId() );
        category.setName( categoryDto.getName() );
        category.setDescription( categoryDto.getDescription() );

        return category;
    }

    protected Item itemDtoToItem(ItemDto itemDto) {
        if ( itemDto == null ) {
            return null;
        }

        Item item = new Item();

        item.setName( itemDto.getName() );
        item.setDescription( itemDto.getDescription() );
        List<String> list = itemDto.getImages();
        if ( list != null ) {
            item.setImages( new ArrayList<String>( list ) );
        }
        item.setItemStatus( itemDto.getItemStatus() );
        item.setCategory( categoryDtoToCategory( itemDto.getCategory() ) );

        return item;
    }

    protected UserDto userToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id( user.getId() );
        userDto.firstName( user.getFirstName() );
        userDto.lastName( user.getLastName() );
        userDto.email( user.getEmail() );

        return userDto.build();
    }

    protected ResponseBidDto bidToResponseBidDto(Bid bid) {
        if ( bid == null ) {
            return null;
        }

        ResponseBidDto.ResponseBidDtoBuilder responseBidDto = ResponseBidDto.builder();

        responseBidDto.bidTime( bid.getBidTime() );
        responseBidDto.amount( bid.getAmount() );
        responseBidDto.bidder( userToUserDto( bid.getBidder() ) );

        return responseBidDto.build();
    }

    protected List<ResponseBidDto> bidListToResponseBidDtoList(List<Bid> list) {
        if ( list == null ) {
            return null;
        }

        List<ResponseBidDto> list1 = new ArrayList<ResponseBidDto>( list.size() );
        for ( Bid bid : list ) {
            list1.add( bidToResponseBidDto( bid ) );
        }

        return list1;
    }
}
