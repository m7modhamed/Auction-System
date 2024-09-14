package com.auction.security.config;

import com.auction.Entity.Account;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auction.Entity.Role;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UserAuthenticationProvider {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

   // private final UserService userService;
    private final UserDetailsService userDetailsService;
    @PostConstruct
    protected void init() {
        // this is to avoid having the raw secret key available in the JVM
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(Account account) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 36000000); // 10 hours

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return JWT.create()
                .withSubject(account.getEmail())
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("firstName", account.getFirstName())
                .withClaim("lastName", account.getLastName())
                .withClaim("roles" , new ArrayList<>(account.getRoles().stream().map(Role::getName).collect(Collectors.toList())))
                .withClaim("isActive" , account.getIsActive())
                .withClaim("isBlocked" , account.getIsBlocked())
                .sign(algorithm);
    }




    public Authentication validateTokenStrongly(String token, HttpServletRequest request) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decoded = verifier.verify(token);

            // Populate WebAuthenticationDetails with request details
            WebAuthenticationDetails authDetails = new WebAuthenticationDetails(request);

            // Retrieve user details
            UserDetails user = userDetailsService.loadUserByUsername(decoded.getSubject());

            // Create authentication token with user details and authorities
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            // Set WebAuthenticationDetails in the authentication token
            authentication.setDetails(authDetails);

            return authentication;
        } catch (JWTVerificationException | UsernameNotFoundException e) {
            // Handle token verification or user details retrieval errors
            throw new BadCredentialsException("Invalid token", e);
        }
    }

}
