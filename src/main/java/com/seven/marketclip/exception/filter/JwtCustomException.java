package com.seven.marketclip.exception.filter;

import lombok.Getter;


//여기다가 response 넣기
@Getter
public class JwtCustomException extends RuntimeException {

    private final String responseCode;

    public JwtCustomException(String str) {
        this.responseCode = str;
        System.out.println("asdasdassdaa1121212");
    }
}
