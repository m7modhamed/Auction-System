package com.auction.security.controllers;

import com.auction.security.dtos.UserDto;
import com.auction.security.entites.Role;
import com.auction.security.entites.User;
import com.auction.security.exceptions.AppException;
import com.auction.security.mappers.UserMapper;
import com.auction.security.repositories.RoleRepository;
import com.auction.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moderator")
public class ModeratorController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    @PutMapping("/addRole")
    public ResponseEntity<UserDto> addAdminRole(@RequestParam long id){
        Optional<User> user= userRepository.findById(id);
        Optional<Role> role=roleRepository.findByName("ROLE_ADMIN");
        if(role.isPresent()){
            user.get().addRole(role.get());
            userRepository.save(user.get());
        }else{
            throw new AppException("the role not found", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok().body(userMapper.toUserDto(user.get()));
    }

    @DeleteMapping("/deleteRole")
    public ResponseEntity<UserDto> deleteRole(@RequestParam long id){
        Optional<User> user= userRepository.findById(id);
        Optional<Role> role=roleRepository.findByName("ROLE_ADMIN");
        if(role.isPresent()){
            user.get().deleteRole(role.get());
            userRepository.save(user.get());
        }else{
            throw new AppException("the role not found", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userMapper.toUserDto(user.get()));
    }
}
