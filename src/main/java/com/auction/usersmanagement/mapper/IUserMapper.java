package com.auction.usersmanagement.mapper;

import com.auction.usersmanagement.dto.SignUpRequestDto;
import com.auction.usersmanagement.model.User;
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
        user.setProfileImage(signUpRequestDto.profileImage());
        user.setMyPassword(passwordEncoder.encode(signUpRequestDto.password()));
        user.setPhoneNumber(signUpRequestDto.phoneNumber());
        user.setIsActive(false);
        user.setIsBlocked(false);
        return user;
    }


}
