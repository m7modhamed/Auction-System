package com.auction.Service.Implementation;

import com.auction.Dtos.UpdateAccountDto;
import com.auction.Entity.Account;
import com.auction.Entity.Image;
import com.auction.Repository.AccountRepository;
import com.auction.Service.Interfaces.IAccountService;
import com.auction.exceptions.AppException;
import com.auction.utility.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final AccountRepository accountRepository;

    @Override
    public void updateAccount(UpdateAccountDto updateAccountDto) {

        long id = Utility.getCurrentAccountId();

        Optional<Account> account = getAccountById(id);

        if (account.isEmpty()) {
            throw new AppException("Account not found.", HttpStatus.NOT_FOUND);
        } else if (account.get().getImage() == null) {
            throw new AppException("No image associated with this account.", HttpStatus.NOT_FOUND);
        }

        account.get().setFirstName(updateAccountDto.getFirstName());
        account.get().setLastName(updateAccountDto.getLastName());
        // add image id to new image
        Image newImg=updateAccountDto.getImage();
        newImg.setId(account.get().getImage().getId());
        account.get().setImage(newImg);

        accountRepository.save(account.get());

    }


    private Optional<Account> getAccountById(Long accountId) {

        return accountRepository.findById(accountId);
    }

}
