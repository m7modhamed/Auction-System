package com.auction.security.mappers;

import com.auction.security.entites.User;
import com.auction.security.dtos.SignUpDto;
import com.auction.security.dtos.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface UserMapper {

    UserDto toUserDto(User user);


    @Mapping(target = "password", ignore = true)
    User signUpToUser(SignUpDto signUpDto);
}
