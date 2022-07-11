//package com.seven.marketclip.exception.filter;
//
//import com.seven.marketclip.exception.HttpResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Slf4j
//public class CustomAuthenticationEntryPoint extends ResponseEntityExceptionHandler implements AuthenticationEntryPoint {
//
//    @ExceptionHandler(value = { JwtCustomException.class })
//    protected ResponseEntity<HttpResponse> handleCustomException(JwtCustomException e) {
//        log.error("handleCustomException throw CustomException : {}", e.getResponseCode());
//        return HttpResponse.toResponseEntity(e.getResponseCode());
////        return new ResponseEntity(HttpStatus.MULTI_STATUS);
//    }
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException,JwtCustomException {
//
//
//
//    }
//}
