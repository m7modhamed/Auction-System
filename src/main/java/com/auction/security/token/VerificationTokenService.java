package com.auction.security.token;

import com.auction.security.entites.Account;
import com.auction.security.entites.User;
import com.auction.security.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

/**
 * @author Sampson Alfred
 */
@Service
@RequiredArgsConstructor
public class VerificationTokenService implements IVerificationTokenService {
    private final VerificationTokenRepository tokenRepository;
    private final AccountRepository userRepository;

    @Override
    public ResponseEntity<String> validateToken(String token) {
        Optional<VerificationToken> theToken = tokenRepository.findByToken(token);
        if (theToken.isEmpty()) {
            return new ResponseEntity<>("token not found",HttpStatus.NOT_FOUND);
        }

        User user = theToken.get().getUser();
        Calendar calendar = Calendar.getInstance();

         if ((theToken.get().getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
             return new ResponseEntity<>("token expired",HttpStatus.BAD_REQUEST);
         }
        user.setIsActive(true);
        userRepository.save(user);
        return ResponseEntity.ok("token valid");
    }

    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        var verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public void deleteUserToken(Long id) {
        tokenRepository.deleteByUserId(id);
    }
}
