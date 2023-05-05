package com.example.jwtstudy.core.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.jwtstudy.model.user.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class JwtProvider {

    public static final String SECRET = "김태헌짱짱맨";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String SUBJECT = "토큰공부";
    public static final int EXP = 1000 * 60 * 60;
    public static final String HEADER = "Authorization";

    // 로그인 완료시
    public static String create(User user) {
        String jwt = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP))
                .withClaim("id", user.getId())
                .withClaim("role", user.getRole())
                .sign(Algorithm.HMAC512(SECRET));
        log.info("디버그 : 토큰 생성됨");
        return TOKEN_PREFIX + jwt;
    }

    public static DecodedJWT verify(String jwt) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                .build().verify(jwt);
        log.info("디버그 : 토큰 검증됨");
        return decodedJWT;
    }
}
