package com.auction.security.services;

import com.auction.security.dtos.UserAuthDto;
import com.auction.security.entites.User;
import com.auction.security.password.IpasswordResetTokenService;
import com.auction.security.dtos.CredentialsDto;
import com.auction.security.dtos.SignUpDto;
import com.auction.security.entites.Role;
import com.auction.security.event.RegistrationCompleteEvent;
import com.auction.security.event.listener.RegistrationCompleteEventListener;
import com.auction.exceptions.AppException;
import com.auction.security.mappers.UserMapper;
import com.auction.security.repositories.UserRepository;
import com.auction.security.repositories.RoleRepository;
import com.auction.security.utility.UrlUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    private final IpasswordResetTokenService passwordResetTokenService;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher publisher;
    private final RegistrationCompleteEventListener eventListener;

    private final AuthenticationManager authenticationManager;

    /*public UserAuthDto login(CredentialsDto credentialsDto) {
        // Find user by email
        User user = userRepository.findByEmail(credentialsDto.login())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        // Check password
        if (!passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()), user.getPassword())) {
            throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
        }

        // Check user status
        if (!user.getIsActive()) {
            throw new AppException("User is not active", HttpStatus.BAD_REQUEST);
        }

        // Check if user is blocked
        if (user.getIsBlocked()) {
            throw new AppException("User is blocked", HttpStatus.BAD_REQUEST);
        }

        // Return user DTO
        return userMapper.toUserDto(user);
    }*/


    public UserAuthDto login(CredentialsDto credentialsDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentialsDto.login(),
                        credentialsDto.password()
                )
        );
        var user = userRepository.findByEmail(credentialsDto.login())
                .orElseThrow();
        return userMapper.toUserDto(user);

    }

    public UserAuthDto register(SignUpDto userDto, HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.email());

        if (optionalUser.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }


        User user = userMapper.signUpToUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setIsBlocked(false);
        user.setIsActive(false);
        user.setRoles(new HashSet<>());
        Optional<Role> role=roleRepository.findByName("ROLE_USER");
        if(role.isPresent()) {
            user.getRoles().add(role.get());
        }else{
            throw new AppException("the role not found",HttpStatus.NOT_FOUND);
        }
        User savedUser = userRepository.save(user);
        publisher.publishEvent(new RegistrationCompleteEvent(savedUser, UrlUtil.getApplicationUrl(request)));
        return userMapper.toUserDto(savedUser);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
    }


    public void resetPasswordRequest(User user, HttpServletRequest request) {
        String passwordResetToken = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetTokenForUser(user, passwordResetToken);

        // Send password reset verification email to the user
        String url = UrlUtil.getApplicationUrl(request) + "/reset-password?token=" + passwordResetToken;
        try {
            eventListener.sendPasswordResetVerificationEmail(url, user);
        } catch (UnsupportedEncodingException | MessagingException e) {
            throw new AppException("Error sending password reset email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public String resetPassword(String token, String password) {
        String tokenVerificationResult = passwordResetTokenService.validatePasswordResetToken(token);

        if (!tokenVerificationResult.equalsIgnoreCase("valid")) {
            return "Invalid token";
        }

        Optional<User> theUser = passwordResetTokenService.findUserByPasswordResetToken(token);
        if (theUser.isPresent()) {
            passwordResetTokenService.resetPassword(theUser.get(), password);
            return "Password reset successfully";
        }

        return "User not found";
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public User saveOrUpdateUser(User user){
        return userRepository.save(user);
    }
}
