package com.auction.security.mappers;

import com.auction.Dtos.UserDto;
import com.auction.security.dtos.UserAuthDto;
import com.auction.security.entites.Account;
import com.auction.security.dtos.SignUpDto;
import com.auction.security.entites.User;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserAuthDto UserAuthDto(Account account);

    UserDto toUserDto(Account account);

    //@Mapping(target = "password", ignore = true)

    User signUpToUser(SignUpDto signUpDto);


}
