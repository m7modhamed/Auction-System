package com.auction.Service.Implementation;

import com.auction.Entity.Account;
import com.auction.Entity.PasswordResetToken;
import com.auction.Service.Interfaces.IpasswordResetTokenService;
import com.auction.Repository.PasswordResetTokenRepository;
import com.auction.Repository.AccountRepository;
import com.auction.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class passwordResetTokenService implements IpasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public String validatePasswordResetToken(String theToken) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(theToken);
        if (passwordResetToken.isEmpty()){
            return "invalid";
        }
        Calendar calendar = Calendar.getInstance();
        if (passwordResetToken.get().getExpirationTime().getTime() - calendar.getTime().getTime() <= 0){
            return "expired";
        }
        return "valid";
    }

    @Override
    public Optional<Account> findUserByPasswordResetToken(String theToken) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(theToken).get().getAccount());
    }



    @Override
    public void resetPassword(Account theAccount, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        if (passwordEncoder.matches(newPassword, theAccount.getMyPassword())) {
            throw new AppException("The new password must be different from the old password.", HttpStatus.BAD_REQUEST);
        }

        theAccount.setMyPassword(encodedPassword);
        accountRepository.save(theAccount);
    }
    @Override
    public void createPasswordResetTokenForUser(Account account, String passwordResetToken) {
        PasswordResetToken resetToken = new PasswordResetToken(account, passwordResetToken);
        passwordResetTokenRepository.save(resetToken);
    }


}
