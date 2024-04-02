package com.auction.security.services;

import com.auction.security.entites.User;
import com.auction.security.entites.Role;
import com.auction.security.repositories.RoleRepository;
import com.auction.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.CharBuffer;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        Role moderatorRole = new Role();
        moderatorRole.setName("ROLE_MODERATOR");
        moderatorRole.setDescription("Moderator role");
        roleRepository.save(moderatorRole);

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole.setDescription("admin role");
        roleRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        userRole.setDescription("user role");
        roleRepository.save(userRole);


        User user = new User();
        user.setEmail("m7modm42@gmail.com");
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap("123")));
        user.setFirstName("mahmoud");
        user.setLastName("hamed");
        user.setIsActive(true);
        user.setIsBlocked(false);
        user.addRole(moderatorRole);
        userRepository.save(user);
    }
}

