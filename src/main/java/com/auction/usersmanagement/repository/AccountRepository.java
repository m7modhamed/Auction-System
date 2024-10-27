package com.auction.usersmanagement.repository;

import com.auction.usersmanagement.model.SysAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Transactional
@Repository
public interface AccountRepository extends JpaRepository<SysAccount, Long> {

    Optional<SysAccount> findByEmail(String login);
}
