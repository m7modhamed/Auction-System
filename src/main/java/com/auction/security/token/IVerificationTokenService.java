package com.auction.security.token;


import com.auction.security.entites.Account;
import com.auction.security.entites.User;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

/**
 * @author Sampson Alfred
 */

public interface IVerificationTokenService {
    ResponseEntity<String> validateToken(String token);
    void saveVerificationTokenForUser(User user, String token);
    Optional<VerificationToken> findByToken(String token);


    void deleteUserToken(Long id);
}
