package com.auction.usersmanagement.repository;

import com.auction.usersmanagement.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Sampson Alfred
 */

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    void deleteBySysAccountId(Long id);
}
