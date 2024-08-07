package com.auction.security.password;


import com.auction.security.entites.Account;
import com.auction.security.entites.User;

import java.util.Optional;

public interface IpasswordResetTokenService {

    String validatePasswordResetToken(String theToken);

    Optional<Account> findUserByPasswordResetToken(String theToken);

    void resetPassword(Account theAccount, String password);

    void createPasswordResetTokenForUser(Account account, String passwordResetToken);


}
