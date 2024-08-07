package com.auction.Mappers;

import com.auction.Dtos.RequestAuctionDto;
import com.auction.Dtos.ResponseAuctionDto;
import com.auction.Dtos.ResponseItemDto;
import com.auction.Entity.Auction;
import com.auction.Entity.Image;
import com.auction.Entity.Item;
import com.auction.Repository.ImageRepository;
import com.auction.Service.Implementation.imageService;
import com.auction.Service.Interfaces.IimageService;
import com.auction.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuctionMapper {



    Auction toAuction(RequestAuctionDto auction);


    ResponseAuctionDto toResponseAuctionDto(Auction auction);

    @Mapping(source = "images", target = "images", qualifiedByName = "mapImagesToByteArrays")
    ResponseItemDto toResponseItemDto(Item item);

    @Named("mapImagesToByteArrays")
    default byte[][] mapImagesToByteArrays(List<Image> images) throws IOException {
        if (images == null) {
            return null;
        }
        byte[][] byteArrayImages = new byte[images.size()][];
        for (int i = 0; i < images.size(); i++) {
            byteArrayImages[i] = downloadImageFromFileSystem(images.get(i));
        }
        return byteArrayImages;
    }



    default byte[] downloadImageFromFileSystem(Image image) throws IOException {


        return Files.readAllBytes(new File(image.getImagePath()).toPath());
    }




    /*
    @Mapping(source = "sourceNestedField", target = "targetNestedField")
    ResponseAuctionDto toResponseAuctionDto(Auction auction);

    default byte[] downloadImageFromFileSystem(Image image) throws IOException {
        Optional<Image> fileData = imageService.getByName(image.getImagePath());
        if(fileData.isEmpty()){
            throw new IOException();
        }
        String filePath = fileData.get().getImagePath();

        return Files.readAllBytes(new File(filePath).toPath());
    }
*/

}
