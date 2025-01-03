package com.auction.usersmanagement.model;

import com.auction.usersmanagement.utility.TokenExpirationTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

/**
 * @author Sampson Alfred
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String token;
    @Value("false")
    private boolean isUsed;
    private Date expirationTime;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private SysAccount sysAccount;

    public Token(String token, SysAccount sysAccount) {
        this.token = token;
        this.sysAccount = sysAccount;
        this.expirationTime = TokenExpirationTime.getExpirationTime();
    }
}
