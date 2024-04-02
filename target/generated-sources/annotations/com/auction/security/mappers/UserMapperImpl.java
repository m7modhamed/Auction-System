package com.auction.security.mappers;

import com.auction.security.entites.User;
import com.auction.security.dtos.SignUpDto;
import com.auction.security.dtos.UserDto;
import com.auction.security.entites.Role;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-02T07:10:01+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 20.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id( user.getId() );
        userDto.firstName( user.getFirstName() );
        userDto.lastName( user.getLastName() );
        userDto.email( user.getEmail() );
        if ( user.getIsActive() != null ) {
            userDto.isActive( user.getIsActive() );
        }
        if ( user.getIsBlocked() != null ) {
            userDto.isBlocked( user.getIsBlocked() );
        }
        Set<Role> set = user.getRoles();
        if ( set != null ) {
            userDto.roles( new LinkedHashSet<Role>( set ) );
        }

        return userDto.build();
    }

    @Override
    public User signUpToUser(SignUpDto signUpDto) {
        if ( signUpDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.firstName( signUpDto.firstName() );
        user.lastName( signUpDto.lastName() );
        user.email( signUpDto.email() );

        return user.build();
    }
}
