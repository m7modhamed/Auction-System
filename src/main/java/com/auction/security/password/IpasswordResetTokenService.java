package com.auction.security.password;


import com.auction.security.entites.User;

import java.util.Optional;

public interface IpasswordResetTokenService {

    String validatePasswordResetToken(String theToken);

    Optional<User> findUserByPasswordResetToken(String theToken);

    void resetPassword(User theUser, String password);

    void createPasswordResetTokenForUser(User user, String passwordResetToken);

    void deleteUserToken(Long id);
}
