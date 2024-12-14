package com.auction.usersmanagement.service.interfaces;


import com.auction.usersmanagement.model.SysAccount;
import com.auction.usersmanagement.model.Token;

/**
 * @author Sampson Alfred
 */

public interface ITokenService {
    void saveVerificationTokenForUser(SysAccount sysAccount, String token);

    void deleteUserToken(Long id);

    void createPasswordResetTokenForUser(SysAccount sysAccount, String passwordResetToken);

    Token validateToken(String theToken);

    void saveToken(Token tokenObj);

    SysAccount getAccountByToken(String token);
}
