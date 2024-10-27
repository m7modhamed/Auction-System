package com.auction.usersmanagement.repository;

import com.auction.usersmanagement.model.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModeratorRepository extends JpaRepository<Moderator,Long> {
    Optional<Moderator> findByEmail(String email);
}
