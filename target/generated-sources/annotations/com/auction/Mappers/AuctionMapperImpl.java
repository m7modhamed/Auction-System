package com.auction.Mappers;

import com.auction.Dtos.CategoryDto;
import com.auction.Dtos.ItemDto;
import com.auction.Dtos.RequestAuctionDto;
import com.auction.Dtos.ResponseAuctionDto;
import com.auction.Dtos.ResponseBidDto;
import com.auction.Dtos.ResponseItemDto;
import com.auction.Dtos.UserDto;
import com.auction.Entity.Auction;
import com.auction.Entity.Bid;
import com.auction.Entity.Category;
import com.auction.Entity.Image;
import com.auction.Entity.Item;
import com.auction.Enums.Address;
import com.auction.Enums.ItemStatus;
import com.auction.security.entites.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "٢٠٢٤-٠٨-٠٧T١٠:٤٦:٤٥+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class AuctionMapperImpl implements AuctionMapper {

    @Override
    public Auction toAuction(RequestAuctionDto auction) {
        if ( auction == null ) {
            return null;
        }

        Auction auction1 = new Auction();

        auction1.setExpireDate( auction.getExpireDate() );
        auction1.setItem( itemDtoToItem( auction.getItem() ) );
        if ( auction.getLocation() != null ) {
            auction1.setLocation( Enum.valueOf( Address.class, auction.getLocation() ) );
        }
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

        responseAuctionDto.id( auction.getId() );
        responseAuctionDto.active( auction.isActive() );
        responseAuctionDto.beginDate( auction.getBeginDate() );
        responseAuctionDto.expireDate( auction.getExpireDate() );
        responseAuctionDto.item( toResponseItemDto( auction.getItem() ) );
        responseAuctionDto.location( auction.getLocation() );
        responseAuctionDto.seller( userToUserDto( auction.getSeller() ) );
        responseAuctionDto.minBid( auction.getMinBid() );
        responseAuctionDto.initialPrice( auction.getInitialPrice() );
        responseAuctionDto.currentPrice( (float) auction.getCurrentPrice() );
        responseAuctionDto.bids( bidListToResponseBidDtoList( auction.getBids() ) );

        return responseAuctionDto.build();
    }

    @Override
    public ResponseItemDto toResponseItemDto(Item item) {
        if ( item == null ) {
            return null;
        }

        ResponseItemDto.ResponseItemDtoBuilder responseItemDto = ResponseItemDto.builder();

        try {
            responseItemDto.images( mapImagesToByteArrays( item.getImages() ) );
        }
        catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        responseItemDto.name( item.getName() );
        responseItemDto.description( item.getDescription() );
        if ( item.getItemStatus() != null ) {
            responseItemDto.itemStatus( item.getItemStatus().name() );
        }
        responseItemDto.category( categoryToCategoryDto( item.getCategory() ) );
        Map<String, String> map = item.getCategoryAttributes();
        if ( map != null ) {
            responseItemDto.categoryAttributes( new LinkedHashMap<String, String>( map ) );
        }

        return responseItemDto.build();
    }

    protected Image multipartFileToImage(MultipartFile multipartFile) {
        if ( multipartFile == null ) {
            return null;
        }

        Image.ImageBuilder image = Image.builder();

        image.name( multipartFile.getName() );

        return image.build();
    }

    protected List<Image> multipartFileListToImageList(List<MultipartFile> list) {
        if ( list == null ) {
            return null;
        }

        List<Image> list1 = new ArrayList<Image>( list.size() );
        for ( MultipartFile multipartFile : list ) {
            list1.add( multipartFileToImage( multipartFile ) );
        }

        return list1;
    }

    protected Category categoryDtoToCategory(CategoryDto categoryDto) {
        if ( categoryDto == null ) {
            return null;
        }

        Category category = new Category();

        category.setId( categoryDto.getId() );
        category.setName( categoryDto.getName() );
        category.setDescription( categoryDto.getDescription() );
        List<String> list = categoryDto.getAttributes();
        if ( list != null ) {
            category.setAttributes( new ArrayList<String>( list ) );
        }

        return category;
    }

    protected Item itemDtoToItem(ItemDto itemDto) {
        if ( itemDto == null ) {
            return null;
        }

        Item item = new Item();

        item.setName( itemDto.getName() );
        item.setDescription( itemDto.getDescription() );
        item.setImages( multipartFileListToImageList( itemDto.getImages() ) );
        if ( itemDto.getItemStatus() != null ) {
            item.setItemStatus( Enum.valueOf( ItemStatus.class, itemDto.getItemStatus() ) );
        }
        item.setCategory( categoryDtoToCategory( itemDto.getCategory() ) );
        Map<String, String> map = itemDto.getCategoryAttributes();
        if ( map != null ) {
            item.setCategoryAttributes( new LinkedHashMap<String, String>( map ) );
        }

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

    protected CategoryDto categoryToCategoryDto(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDto.CategoryDtoBuilder categoryDto = CategoryDto.builder();

        categoryDto.id( category.getId() );
        categoryDto.name( category.getName() );
        categoryDto.description( category.getDescription() );
        List<String> list = category.getAttributes();
        if ( list != null ) {
            categoryDto.attributes( new ArrayList<String>( list ) );
        }

        return categoryDto.build();
    }
}
