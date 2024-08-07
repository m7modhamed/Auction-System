package com.auction.exceptions;

import com.auction.Dtos.ErrorDto;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
@RestControllerAdvice
public class RestExceptionHandler {

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
    public ProblemDetail handleBadCredentialsException(BadCredentialsException ex) {
        ProblemDetail errorDetail=null;

        errorDetail=ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        errorDetail.setProperty("access-denied-reason","Invalid username or password");

        return errorDetail;
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