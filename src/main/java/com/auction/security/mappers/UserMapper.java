package com.auction.security.mappers;

import com.auction.Dtos.UserDto;
import com.auction.security.dtos.UserAuthDto;
import com.auction.security.entites.User;
import com.auction.security.dtos.SignUpDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserAuthDto UserAuthDto(User user);

    UserDto toUserDto(User user);
    @Mapping(target = "password", ignore = true)
    User signUpToUser(SignUpDto signUpDto);
}
