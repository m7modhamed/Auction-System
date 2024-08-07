package com.auction.security.repositories;

import com.auction.security.entites.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModeratorRepository extends JpaRepository<Moderator,Long> {
    Optional<Moderator> findByEmail(String email);
}
