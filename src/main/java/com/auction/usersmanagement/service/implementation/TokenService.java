package com.auction.usersmanagement.service.implementation;

import com.auction.common.exceptions.AppException;
import com.auction.usersmanagement.model.SysAccount;
import com.auction.usersmanagement.model.Token;
import com.auction.usersmanagement.repository.TokenRepository;
import com.auction.usersmanagement.service.interfaces.ITokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Optional;

/**
 * @author Sampson Alfred
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TokenService implements ITokenService {
    private final TokenRepository tokenRepository;


    public Token validateToken(String theToken) {
        Optional<Token> token = tokenRepository.findByToken(theToken);
        if (token.isEmpty()) {
            throw new AppException( "The token could not be found." ,HttpStatus.NOT_FOUND);
        }

        if (token.get().isUsed()) {
            throw new AppException( "This token has already been used." ,HttpStatus.BAD_REQUEST);
        }

        Calendar calendar = Calendar.getInstance();
        if ((token.get().getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
            throw new AppException( "This token expired." ,HttpStatus.BAD_REQUEST);
        }
        return token.get();
    }

    @Override
    public void saveToken(Token tokenObj) {
        tokenRepository.save(tokenObj);
    }

    @Override
    public void saveVerificationTokenForUser(SysAccount sysAccount, String token) {
        var verificationToken = new Token(token, sysAccount);
        tokenRepository.save(verificationToken);
    }



    @Override
    public void createPasswordResetTokenForUser(SysAccount sysAccount, String passwordResetToken) {
        Token resetToken = new Token(passwordResetToken , sysAccount);
        tokenRepository.save(resetToken);
    }


    @Override
    public void deleteUserToken(Long id) {
        tokenRepository.deleteBySysAccountId(id);
    }
}
