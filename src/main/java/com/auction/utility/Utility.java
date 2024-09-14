package com.auction.utility;
import com.auction.Entity.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class Utility {


    public static Long getCurrentAccountId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((Account) authentication.getPrincipal()).getId();
    }
}
