package com.auction.event;

import com.auction.Entity.Account;
import com.auction.Entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class RegistrationCompleteEvent extends ApplicationEvent {
    private Account account;
    private String confirmationUrl;
    public RegistrationCompleteEvent(Account account, String confirmationUrl) {
        super(account);
        this.account = account;
        this.confirmationUrl=confirmationUrl;
    }
}
