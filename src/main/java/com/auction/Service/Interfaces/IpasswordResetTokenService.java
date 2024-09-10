package com.auction.Service.Interfaces;


import com.auction.Entity.Account;

import java.util.Optional;

public interface IpasswordResetTokenService {

    String validatePasswordResetToken(String theToken);

    Optional<Account> findUserByPasswordResetToken(String theToken);

    void resetPassword(Account theAccount, String password);

    void createPasswordResetTokenForUser(Account account, String passwordResetToken);


}
