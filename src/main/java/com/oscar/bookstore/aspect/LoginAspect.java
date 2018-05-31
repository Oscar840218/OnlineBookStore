package com.oscar.bookstore.aspect;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.oscar.bookstore.service.UserLoginInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;


@Aspect
@Component
public class LoginAspect {

    @Pointcut("execution(* com.oscar.bookstore.controller.CustomerController.showProfile(..))")
    private void getProfile(){}

    @Pointcut("execution(* com.oscar.bookstore.controller.BookController.*(..))")
    private void addNewBook(){}

    @Before("getProfile()")
    public void receiveTokenCustomerAdvice(JoinPoint joinPoint) throws Throwable {

        System.out.println(">>>>Before Action\n");

        getToken(joinPoint);

    }

    @Before("addNewBook()")
    public void receiveTokenBookAdvice(JoinPoint joinPoint) throws Throwable {

        System.out.println(">>>>Before Action\n");

        getToken(joinPoint);

    }


    private void getToken(JoinPoint joinPoint) {
        HttpServletRequest request = null;

        DecodedJWT jwt;

        Object[] args = joinPoint.getArgs();

        for(Object o : args) {
            if(o instanceof HttpServletRequest) {
                request = (HttpServletRequest) o;
            }
        }

        try {
            String token = null;

            if (request != null) {
                token = request.getHeader("authorization");
            }

            jwt = decodeToken(token);

            if (jwt != null) {
                System.out.println(jwt.getClaim("userId").asInt());
                UserLoginInfo.getInstance().setUserId(jwt.getClaim("userId").asInt());
            } else {
                UserLoginInfo.getInstance().setUserId(null);
            }

        } catch (NullPointerException e) {
            UserLoginInfo.getInstance().setUserId(null);
        }
    }


    private DecodedJWT decodeToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build(); //Reusable verifier instance
           return verifier.verify(token);
        } catch (UnsupportedEncodingException | JWTVerificationException exception){
            //UTF-8 encoding not supported
            exception.printStackTrace();
        }
        return null;
    }
}
