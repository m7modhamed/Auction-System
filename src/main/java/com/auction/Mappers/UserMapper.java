package com.auction.Mappers;

import com.auction.Dtos.UserDto;
import com.auction.Dtos.UserAuthDto;
import com.auction.Entity.Account;
import com.auction.Dtos.SignUpDto;
import com.auction.Entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserAuthDto UserAuthDto(Account account);

    UserDto toUserDto(Account account);

    //@Mapping(target = "password", ignore = true)

    User signUpToUser(SignUpDto signUpDto);


}
