package com.auction.Service.Interfaces;


import com.auction.Entity.Account;
import com.auction.Entity.User;
import com.auction.Entity.VerificationToken;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

/**
 * @author Sampson Alfred
 */

public interface IVerificationTokenService {
    ResponseEntity<String> validateToken(String token);
    void saveVerificationTokenForUser(Account account, String token);
    Optional<VerificationToken> findByToken(String token);


    void deleteUserToken(Long id);
}
