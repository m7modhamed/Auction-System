package com.auction.Repository;

import com.auction.Entity.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModeratorRepository extends JpaRepository<Moderator,Long> {
    Optional<Moderator> findByEmail(String email);
}
