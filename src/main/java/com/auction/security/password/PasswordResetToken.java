package com.auction.security.password;

import com.auction.security.entites.User;
import com.auction.security.utility.TokenExpirationTime;
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
    @JoinColumn(name = "user_id")
    private User user;

    public PasswordResetToken(User user, String token) {
        this.user = user;
        this.token = token;
        this.expirationTime = TokenExpirationTime.getExpirationTime();
    }
}
