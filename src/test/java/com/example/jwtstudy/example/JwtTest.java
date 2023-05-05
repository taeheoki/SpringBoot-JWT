package com.example.jwtstudy.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class JwtTest {

    @Test
    public void createJwt_test() {
        // given

        // when
        String jwt = JWT.create()
                .withSubject("토큰공부")
//                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000))
                .withClaim("id", 1)
                .withClaim("role", "guest")
                .sign(Algorithm.HMAC512("김태헌짱짱맨"));
        System.out.println(jwt);
        // then
    }

    @Test
    public void verifyJwt_test() {
        // given
        String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiLthqDtgbDqs7XrtoAiLCJyb2xlIjoiZ3Vlc3QiLCJpZCI6MSwiZXhwIjoxNjgzMzAxMDMxfQ.PwQYdvmdBmie7a1hPHJkj-GuplFaBILSX8O9gpXql1uFOJQAl5su3CCFXeh20KatFILb5HFbMg6w";

        // when
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512("김태헌짱짱맨"))
                    .build().verify(jwt);
            Long id = decodedJWT.getClaim("id").asLong();
            String role = decodedJWT.getClaim("role").asString();
            System.out.println(id);
            System.out.println(role);
        } catch (TokenExpiredException tee) {
            System.out.println("토큰 만료 " + tee.getMessage()); // 오래됨
        } catch (SignatureVerificationException sve) {
            System.out.println("토큰 검증 실패 " + sve.getMessage()); // 위조
        }
        // then
    }
}
