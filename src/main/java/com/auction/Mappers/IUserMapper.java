package com.auction.Mappers;

import com.auction.Dtos.SignUpRequestDto;
import com.auction.Entity.User;
import org.mapstruct.Mapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
