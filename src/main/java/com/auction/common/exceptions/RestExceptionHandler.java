package com.auction.common.exceptions;


import com.auction.usersmanagement.dto.LoginResponseDto;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
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


    @ExceptionHandler(value = { AppException.class })
    public ResponseEntity<ResponseError> handleAppException(AppException ex) {
        ResponseError error = ResponseError.builder()
                .message(ex.getMessage())
                .status(ex.getStatus())
                .build();

        return new ResponseEntity<>(error,ex.getStatus());
    }


    //TokenExpiredException
    @ExceptionHandler(value = { TokenExpiredException.class })
    public ResponseEntity<ResponseError> handleTokenExpiredException() {

        ResponseError error = ResponseError.builder()
                .message("Authentication token has expired")
                .status(HttpStatus.valueOf(401))
                .build();

        return new ResponseEntity<>(error,HttpStatus.valueOf(401));
    }


    @ExceptionHandler(value = { BadCredentialsException.class })
    public ResponseEntity<LoginResponseDto> handleBadCredentialsException() {

        LoginResponseDto loginResponseDto =new LoginResponseDto();
        loginResponseDto.setStatus("error");
        loginResponseDto.setMessage("Invalid credentials");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponseDto);
    }


   @ExceptionHandler(value = { DisabledException.class })
    public ResponseEntity<LoginResponseDto> handleDisabledException() {


        LoginResponseDto loginResponseDto =new LoginResponseDto();
        loginResponseDto.setStatus("inactive");
        loginResponseDto.setMessage("SysAccount is inactive");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponseDto);
    }


    @ExceptionHandler(value = { LockedException.class })
    public ResponseEntity<LoginResponseDto> handleLockedException() {
        LoginResponseDto loginResponseDto =new LoginResponseDto();
        loginResponseDto.setStatus("blocked");
        loginResponseDto.setMessage("SysAccount is blocked");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(loginResponseDto);
    }


    @ExceptionHandler(value = { JWTVerificationException.class })
    public ResponseEntity<ResponseError> handleJWTVerificationException() {

        ResponseError error = ResponseError.builder()
                .message("The Token Is Not Valid")
                .status(HttpStatus.UNAUTHORIZED)
                .build();

        return new ResponseEntity<>(error,HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<ResponseError> handleException(Exception ex) {

        ResponseError error = ResponseError.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);

    }

    //data member validation handling
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleInvalidArgument(MethodArgumentNotValidException ex){
        Map<String,String> map=new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error->
                map.put(error.getField(),error.getDefaultMessage()));

        return map;
    }
}