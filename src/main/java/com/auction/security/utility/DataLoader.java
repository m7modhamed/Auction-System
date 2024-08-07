package com.auction.security.utility;

import com.auction.security.entites.Account;
import com.auction.security.entites.Moderator;
import com.auction.security.entites.Role;
import com.auction.security.repositories.ModeratorRepository;
import com.auction.security.repositories.RoleRepository;
import com.auction.security.repositories.AccountRepository;
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
    private final AccountRepository userRepository;
    private final ModeratorRepository moderatorRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {

        Role moderatorRole;
        Role adminRole;
        Role userRole;

        Optional<Role> roleModeratorOptional = roleRepository.findByName("ROLE_MODERATOR");
        if (roleModeratorOptional.isEmpty()) {
            moderatorRole = new Role();
            moderatorRole.setName("ROLE_MODERATOR");
            moderatorRole.setDescription("Moderator role");
            roleRepository.save(moderatorRole);
        }else{
            moderatorRole=roleModeratorOptional.get();
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

        Optional<Moderator> moderatorOptional=moderatorRepository.findByEmail("m7modm42@gmail.com");
        if(moderatorOptional.isEmpty()) {
            Moderator moderator = new Moderator();
            moderator.setEmail("m7modm42@gmail.com");
            moderator.setMyPassword(passwordEncoder.encode(CharBuffer.wrap("123")));
            moderator.setFirstName("mahmoud");
            moderator.setLastName("hamed");
            moderator.setIsActive(true);
            moderator.setIsBlocked(false);
            moderator.getRoles().add(moderatorRole);
            moderatorRepository.save(moderator);
        }
    }
}

