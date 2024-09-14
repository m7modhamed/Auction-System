package com.auction.Mappers;

import com.auction.Dtos.SignUpRequestDto;
import com.auction.Dtos.UserDto;
import com.auction.Dtos.UserAuthDto;
import com.auction.Entity.Account;
import com.auction.Entity.User;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;

@Mapper(componentModel = "spring")
public interface IUserMapper {

     PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    default User signUpToUser(SignUpRequestDto signUpRequestDto){
        User user = new User();
        user.setFirstName(signUpRequestDto.firstName());
        user.setLastName(signUpRequestDto.lastName());
        user.setEmail(signUpRequestDto.email());
        user.setImage(signUpRequestDto.image());
        user.setMyPassword(passwordEncoder.encode(signUpRequestDto.password()));
        user.setIsActive(false);
        user.setIsBlocked(false);
        return user;
    }


}
