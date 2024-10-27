package com.auction.common.exceptions;


import com.auction.usersmanagement.dto.LoginResponseDto;
import com.auction.usersmanagement.repository.AccountRepository;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
@RestControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {

    private final ApplicationEventPublisher publisher;
    private final AccountRepository accountRepository;
    private final HttpMessageConverters messageConverters;


    @ExceptionHandler(value = { AppException.class })
    @ResponseBody
    public ResponseEntity<ErrorDto> handleAppException(AppException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorDto(ex.getMessage()));
    }


    //TokenExpiredException
    @ExceptionHandler(value = { TokenExpiredException.class })
    public ProblemDetail handleTokenExpiredException(TokenExpiredException ex) {
        ProblemDetail errorDetail=null;

        errorDetail=ProblemDetail.forStatus(HttpStatusCode.valueOf(401));
        errorDetail.setProperty("access-denied-reason","Authentication token has expired");

        return errorDetail;
    }



    //data member validation handling
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleInvalidArgument(MethodArgumentNotValidException ex){
        Map<String,String> map=new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error->{
            map.put(error.getField(),error.getDefaultMessage());
        });

        return map;
    }


    @ExceptionHandler(value = { BadCredentialsException.class })
    public ResponseEntity<LoginResponseDto> handleBadCredentialsException(BadCredentialsException ex) {

        LoginResponseDto loginResponseDto =new LoginResponseDto();
        loginResponseDto.setStatus("error");
        loginResponseDto.setMessage("Invalid credentials");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponseDto);
    }


   @ExceptionHandler(value = { DisabledException.class })
    public ResponseEntity<LoginResponseDto> handleException(DisabledException ex) {


        LoginResponseDto loginResponseDto =new LoginResponseDto();
        loginResponseDto.setStatus("inactive");
        loginResponseDto.setMessage("SysAccount is inactive");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponseDto);
    }


    @ExceptionHandler(value = { LockedException.class })
    public ResponseEntity<LoginResponseDto> handleException(LockedException ex) {
        LoginResponseDto loginResponseDto =new LoginResponseDto();
        loginResponseDto.setStatus("blocked");
        loginResponseDto.setMessage("SysAccount is blocked");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(loginResponseDto);
    }


    @ExceptionHandler(value = { JWTVerificationException.class })
    public ProblemDetail handleJWTVerificationException(JWTVerificationException ex) {
        ProblemDetail errorDetail=null;

        errorDetail=ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        errorDetail.setProperty("access-denied-reason","The Token Is Not Valid");

        return errorDetail;
    }


    @ExceptionHandler(value = { Exception.class })
    public ProblemDetail handleException(Exception ex) {
        ProblemDetail errorDetail=null;

        errorDetail=ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        errorDetail.setProperty("access-denied-reason",ex.getMessage());

        return errorDetail;
    }

}