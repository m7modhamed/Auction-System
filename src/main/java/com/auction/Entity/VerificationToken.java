package com.auction.Entity;

import com.auction.utility.TokenExpirationTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author Sampson Alfred
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date expirationTime;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account account;

    public VerificationToken(String token, Account account) {
        this.token = token;
        this.account = account;
        this.expirationTime = TokenExpirationTime.getExpirationTime();
    }
}
