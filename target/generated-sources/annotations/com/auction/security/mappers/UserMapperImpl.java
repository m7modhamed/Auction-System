package com.auction.security.mappers;

import com.auction.security.dtos.SignUpDto;
import com.auction.security.dtos.UserAuthDto;
import com.auction.security.entites.Role;
import com.auction.security.entites.User;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-30T13:18:51+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 20.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserAuthDto toUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserAuthDto.UserAuthDtoBuilder userAuthDto = UserAuthDto.builder();

        userAuthDto.id( user.getId() );
        userAuthDto.firstName( user.getFirstName() );
        userAuthDto.lastName( user.getLastName() );
        userAuthDto.email( user.getEmail() );
        if ( user.getIsActive() != null ) {
            userAuthDto.isActive( user.getIsActive() );
        }
        if ( user.getIsBlocked() != null ) {
            userAuthDto.isBlocked( user.getIsBlocked() );
        }
        Set<Role> set = user.getRoles();
        if ( set != null ) {
            userAuthDto.roles( new LinkedHashSet<Role>( set ) );
        }

        return userAuthDto.build();
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
