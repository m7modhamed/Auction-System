package com.auction.Service.Implementation;

import com.auction.Entity.Account;
import com.auction.Repository.AccountRepository;
import com.auction.Service.Interfaces.IVerificationTokenService;
import com.auction.Entity.VerificationToken;
import com.auction.Repository.VerificationTokenRepository;
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
    private final AccountRepository accountRepository;

    @Override
    public ResponseEntity<String> validateToken(String token) {
        Optional<VerificationToken> theToken = tokenRepository.findByToken(token);
        if (theToken.isEmpty()) {
            return new ResponseEntity<>("token not found",HttpStatus.NOT_FOUND);
        }

        Account account = theToken.get().getAccount();
        Calendar calendar = Calendar.getInstance();

         if ((theToken.get().getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
             return new ResponseEntity<>("token expired",HttpStatus.BAD_REQUEST);
         }
        account.setIsActive(true);
        accountRepository.save(account);
        return ResponseEntity.ok("token valid");
    }

    @Override
    public void saveVerificationTokenForUser(Account account, String token) {
        var verificationToken = new VerificationToken(token, account);
        tokenRepository.save(verificationToken);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public void deleteUserToken(Long id) {
        tokenRepository.deleteByAccountId(id);
    }
}
