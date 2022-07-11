package com.seven.marketclip.security;

import com.seven.marketclip.exception.HttpResponse;
import com.seven.marketclip.exception.filter.JwtCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

import static com.seven.marketclip.exception.ResponseCode.TEST_SUCCESS;

@Transactional
//@Component
@RequiredArgsConstructor
public class FormLoginFailHandler implements AuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException, JwtCustomException {
        System.out.println("sdd");
    }

    private void sendError(HttpServletResponse response, int code, String message, Exception e) throws IOException, JwtCustomException {
        System.out.println("sdads");
        SecurityContextHolder.clearContext();

        ResponseEntity<HttpResponse> exceptionResponse =

                new ResponseEntity<>(TEST_SUCCESS.getHttpStatus());

//        exceptionResponse.send(response, code);
        response.getWriter().println("asd");
    }

}
