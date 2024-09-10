package com.auction.Entity;

import com.auction.utility.TokenExpirationTime;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@Data
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String token;

    private Date expirationTime;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public PasswordResetToken(Account account, String token) {
        this.account = account;
        this.token = token;
        this.expirationTime = TokenExpirationTime.getExpirationTime();
    }
}
