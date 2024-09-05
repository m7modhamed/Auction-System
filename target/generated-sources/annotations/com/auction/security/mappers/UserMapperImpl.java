package com.auction.security.mappers;

import com.auction.Dtos.UserDto;
import com.auction.security.dtos.SignUpDto;
import com.auction.security.dtos.UserAuthDto;
import com.auction.security.entites.Account;
import com.auction.security.entites.Role;
import com.auction.security.entites.User;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "٢٠٢٤-٠٩-٠٢T٠٧:٤٦:١٥+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserAuthDto UserAuthDto(Account account) {
        if ( account == null ) {
            return null;
        }

        UserAuthDto.UserAuthDtoBuilder userAuthDto = UserAuthDto.builder();

        userAuthDto.id( account.getId() );
        userAuthDto.firstName( account.getFirstName() );
        userAuthDto.lastName( account.getLastName() );
        userAuthDto.email( account.getEmail() );
        if ( account.getIsActive() != null ) {
            userAuthDto.isActive( account.getIsActive() );
        }
        if ( account.getIsBlocked() != null ) {
            userAuthDto.isBlocked( account.getIsBlocked() );
        }
        Set<Role> set = account.getRoles();
        if ( set != null ) {
            userAuthDto.roles( new LinkedHashSet<Role>( set ) );
        }

        return userAuthDto.build();
    }

    @Override
    public UserDto toUserDto(Account account) {
        if ( account == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id( account.getId() );
        userDto.firstName( account.getFirstName() );
        userDto.lastName( account.getLastName() );
        userDto.email( account.getEmail() );

        return userDto.build();
    }

    @Override
    public User signUpToUser(SignUpDto signUpDto) {
        if ( signUpDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        return user.build();
    }
}
