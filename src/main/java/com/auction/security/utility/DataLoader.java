package com.auction.security.utility;

import com.auction.security.entites.User;
import com.auction.security.entites.Role;
import com.auction.security.repositories.RoleRepository;
import com.auction.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.CharBuffer;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {

        Role moderatorRole = null;
        Role adminRole;
        Role userRole;

        Optional<Role> roleModeratorOptional = roleRepository.findByName("ROLE_MODERATOR");
        if (roleModeratorOptional.isEmpty()) {
            moderatorRole = new Role();
            moderatorRole.setName("ROLE_MODERATOR");
            moderatorRole.setDescription("Moderator role");
            roleRepository.save(moderatorRole);
        }

        Optional<Role> roleAdminOptional = roleRepository.findByName("ROLE_ADMIN");
        if (roleAdminOptional.isEmpty()) {
            adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setDescription("admin role");
            roleRepository.save(adminRole);
        }

        Optional<Role> roleUserOptional = roleRepository.findByName("ROLE_USER");
        if (roleUserOptional.isEmpty()) {
             userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setDescription("user role");
            roleRepository.save(userRole);
        }

        Optional<User> userOptional=userRepository.findByEmail("m7modm42@gmail.com");
        if(userOptional.isEmpty()) {
            User user = new User();
            user.setEmail("m7modm42@gmail.com");
            user.setPassword(passwordEncoder.encode(CharBuffer.wrap("123")));
            user.setFirstName("mahmoud");
            user.setLastName("hamed");
            user.setIsActive(true);
            user.setIsBlocked(false);
            user.getRoles().add(moderatorRole);
            userRepository.save(user);
        }
    }
}

