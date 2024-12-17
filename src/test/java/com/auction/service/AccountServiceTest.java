package com.auction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.auction.paymentmanagement.service.interfaces.IPaymentService;
import com.auction.usersmanagement.dto.LoginRequestDto;
import com.auction.usersmanagement.dto.SignUpRequestDto;
import com.auction.usersmanagement.dto.UpdateAccountDto;
import com.auction.usersmanagement.event.RegistrationCompleteEvent;
import com.auction.usersmanagement.event.listener.RegistrationCompleteEventListener;
import com.auction.usersmanagement.mapper.IUserMapper;
import com.auction.usersmanagement.model.ProfileImage;
import com.auction.usersmanagement.model.Role;
import com.auction.usersmanagement.model.SysAccount;
import com.auction.usersmanagement.model.Token;
import com.auction.usersmanagement.model.User;
import com.auction.usersmanagement.repository.AccountRepository;
import com.auction.usersmanagement.repository.RoleRepository;
import com.auction.usersmanagement.service.implementation.AccountService;
import com.auction.usersmanagement.service.interfaces.ITokenService;
import com.stripe.model.Customer;

import jakarta.servlet.http.HttpServletRequest;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private IUserMapper userMapper;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private RegistrationCompleteEventListener eventListener;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ITokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IPaymentService paymentService;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 1. Test register
    @Test
    void testRegister() throws Exception {
        // Mock DTO
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("user@example.com", "password", "John", "Doe", null, null);

        // Mock user and roles
        SysAccount mockAccount = mock(SysAccount.class);
        Role role = new Role();
        role.setName("ROLE_USER");
        User mockUser = mock(User.class);

        // Mock payment service
        Customer mockCustomer = mock(Customer.class);
        when(mockCustomer.getId()).thenReturn("cus_12345");
        when(paymentService.createCustomer(mockUser)).thenReturn(mockCustomer);

        // Mock repositories
        when(roleRepository.findByName("ROLE_USER")).thenReturn(java.util.Optional.of(role));
        when(userMapper.signUpToUser(signUpRequestDto)).thenReturn(mockUser);
        when(accountRepository.save(mockUser)).thenReturn((User) mockAccount);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080"));

        // Call the method
        SysAccount registeredAccount = accountService.register(signUpRequestDto, mockRequest);

        // Assertions
        assertNotNull(registeredAccount);
        verify(paymentService, times(1)).createCustomer(mockUser);
        verify(accountRepository, times(1)).save(mockUser);
        verify(publisher, times(1)).publishEvent(any(RegistrationCompleteEvent.class));
    }

    // 2. Test login
    @Test
    void testLogin() {
        // Mock DTO
        LoginRequestDto loginRequestDto = new LoginRequestDto();

        // Mock account
        SysAccount mockAccount = mock(SysAccount.class);
        when(accountRepository.findByEmail("user@example.com")).thenReturn(java.util.Optional.of(mockAccount));

        // Call the method
        SysAccount loggedInAccount = accountService.login(loginRequestDto);

        // Assertions
        assertNotNull(loggedInAccount);
        verify(accountRepository, times(1)).findByEmail("user@example.com");
    }

    // 3. Test activateUser
    @Test
    void testActivateUser() {
        // Mock token and account
        Token token = mock(Token.class);
        SysAccount mockAccount = mock(SysAccount.class);
        when(tokenService.validateToken("validToken")).thenReturn(token);
        when(token.getSysAccount()).thenReturn(mockAccount);
        when(mockAccount.getIsActive()).thenReturn(false);

        // Call the method
        ResponseEntity<String> response = accountService.activateUser("validToken");

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("sysAccount verify successfully", response.getBody());
        verify(accountRepository, times(1)).save(mockAccount);
    }

    // 4. Test resetPassword
    @Test
    void testResetPassword() {
        // Mock token and account
        Token token = mock(Token.class);
        SysAccount mockAccount = mock(SysAccount.class);
        when(tokenService.validateToken("validToken")).thenReturn(token);
        when(token.getSysAccount()).thenReturn(mockAccount);

        // Mock password encoder
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
        when(mockAccount.getMyPassword()).thenReturn("oldPassword");

        // Call the method
        String response = accountService.resetPassword("validToken", "newPassword");

        // Assertions
        assertEquals("Password reset successfully", response);
        verify(accountRepository, times(1)).save(mockAccount);
        verify(tokenService, times(1)).saveToken(token);
    }

    // 5. Test updateAccount
    @Test
    void testUpdateAccount() {
        // Mock DTO and account
        UpdateAccountDto updateAccountDto = new UpdateAccountDto("John", "Doe", mock(ProfileImage.class));
        SysAccount mockAccount = mock(SysAccount.class);
        when(mockAccount.getProfileImage()).thenReturn(new ProfileImage());
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(mockAccount));

        // Call the method
        SysAccount updatedAccount = accountService.updateAccount(updateAccountDto);

        // Assertions
        assertNotNull(updatedAccount);
        verify(accountRepository, times(1)).save(mockAccount);
    }

    // 6. Test resetPasswordRequest
    @Test
    void testResetPasswordRequest() throws Exception {
        // Mock sysAccount and request
        SysAccount mockAccount = mock(SysAccount.class);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockAccount.getEmail()).thenReturn("user@example.com");

        // Call the method
        accountService.resetPasswordRequest(mockAccount, mockRequest);

        // Assertions
        verify(tokenService, times(1)).createPasswordResetTokenForUser(mockAccount, anyString());
        verify(eventListener, times(1)).sendPasswordResetVerificationEmail(anyString(), eq(mockAccount));
    }

    // 7. Test reSendVerifyEmail
    @Test
    void testReSendVerifyEmail() {
        // Mock email and account
        String email = "user@example.com";
        SysAccount mockAccount = mock(SysAccount.class);
        when(accountRepository.findByEmail(email)).thenReturn(java.util.Optional.of(mockAccount));
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080"));

        // Call the method
        accountService.reSendVerifyEmail(email, mockRequest);

        // Assertions
        verify(publisher, times(1)).publishEvent(any(RegistrationCompleteEvent.class));
    }
}
