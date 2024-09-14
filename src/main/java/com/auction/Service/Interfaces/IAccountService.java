package com.auction.Service.Interfaces;

import com.auction.Dtos.UpdateAccountDto;
import com.auction.Entity.Account;
import com.auction.Entity.User;
import java.util.Optional;

public interface IAccountService {



    void updateAccount(UpdateAccountDto updateAccountDto);
}
