package com.auction.security.dtos;

import com.auction.security.entites.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String token;
    private boolean isActive;
    private boolean isBlocked;
    private Set<Role> roles;

}
