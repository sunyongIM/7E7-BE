package com.seven.marketclip.exception.filter;

import com.seven.marketclip.exception.ResponseCode;
import lombok.Getter;


//여기다가 response 넣기
@Getter
public class JwtCustomException extends RuntimeException {

    private final ResponseCode responseCode;

    public JwtCustomException(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }
}
