package com.seven.marketclip.security.filter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seven.marketclip.exception.filter.JwtCustomException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class FormLoginFilter extends UsernamePasswordAuthenticationFilter{
    final private ObjectMapper objectMapper;

    public FormLoginFilter(final AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws JwtCustomException {
        System.out.println("로그인 필터 1");

        UsernamePasswordAuthenticationToken authRequest;
        try {
            JsonNode requestBody = objectMapper.readTree(request.getInputStream());
            String email = requestBody.get("email").asText(); //email
            String password = requestBody.get("password").asText();

            authRequest = new UsernamePasswordAuthenticationToken(email, password); //사용자가 입력한 아이디(이메일) 비번.
            System.out.println(authRequest.getPrincipal());
        } catch (Exception e) {
            //여기서 예외가 어쩔 때 일어날까? -> 아무것도 입력하지 않았을 때
            throw new JwtCustomException("LOGIN_FILTER_NULL");
            //여기서 왜 응답메시지가 포스트맨에 뜨지 않을까?
        }

        setDetails(request, authRequest);
        System.out.println("로그인 필터 2");

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
