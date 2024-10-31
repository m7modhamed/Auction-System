package com.auction.common.exceptions;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ResponseError{

    private String message;
    private HttpStatus status;
}
