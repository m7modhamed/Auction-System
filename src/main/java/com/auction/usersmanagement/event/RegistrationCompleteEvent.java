package com.auction.usersmanagement.event;

import com.auction.usersmanagement.model.SysAccount;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class RegistrationCompleteEvent extends ApplicationEvent {
    private SysAccount sysAccount;
    private String confirmationUrl;
    public RegistrationCompleteEvent(SysAccount sysAccount, String confirmationUrl) {
        super(sysAccount);
        this.sysAccount = sysAccount;
        this.confirmationUrl=confirmationUrl;
    }
}
