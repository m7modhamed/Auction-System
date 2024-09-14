package com.auction.Mappers;

import com.auction.Dtos.SignUpRequestDto;
import com.auction.Dtos.UserDto;
import com.auction.Dtos.UserAuthDto;
import com.auction.Entity.Account;
import com.auction.Entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IUserMapper {

    UserAuthDto UserAuthDto(Account account);

    UserDto toUserDto(Account account);

    User signUpToUser(SignUpRequestDto signUpRequestDto);


}
