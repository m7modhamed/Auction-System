package com.auction.common.utility;

import com.auction.usersmanagement.model.SysAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AccountUtil {


    public static Long getCurrentAccountId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SysAccount) authentication.getPrincipal()).getId();
    }

    public static SysAccount getCurrentAccount(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (SysAccount) authentication.getPrincipal();
    }
}
