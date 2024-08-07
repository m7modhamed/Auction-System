package com.auction.security.entites;

import com.auction.security.repositories.AccountRepository;
import com.auction.security.repositories.AdminRepository;
import com.auction.security.repositories.ModeratorRepository;
import com.auction.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ModeratorRepository moderatorRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

/*
        Optional<Moderator> moderator = moderatorRepository.findByEmail(username);
        if (moderator.isPresent()) {
            return new CustomUserDetails(moderator.get());
        }

        Optional<Admin> admin = adminRepository.findByEmail(username);
        if (admin.isPresent()) {
            return new CustomUserDetails(admin.get());
        }
*/

        Optional<Account> account = accountRepository.findByEmail(username);
        if (account.isPresent()) {
            return account.get();
        }

        throw new UsernameNotFoundException("User not found");
    }
}
