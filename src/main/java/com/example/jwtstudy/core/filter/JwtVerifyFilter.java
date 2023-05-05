package com.example.jwtstudy.core.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.jwtstudy.core.exception.Exception400;
import com.example.jwtstudy.core.exception.Exception401;
import com.example.jwtstudy.core.jwt.JwtProvider;
import com.example.jwtstudy.core.session.LoginUser;
import com.example.jwtstudy.dto.ResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class JwtVerifyFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("디버그 : JwtVerigyFilter 동작함");

        // 1. 다운캐스팅
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 2. 헤더 검증
        String prefixJwt = req.getHeader(JwtProvider.HEADER);
        if (prefixJwt == null) {
            error(resp, new Exception401("토큰이 전달되지 않았습니다."));
            return;
        }

        // 3. Bearer 제거
        String jwt = prefixJwt.replace(JwtProvider.TOKEN_PREFIX, "");

        try {
            // 4. 검증
            DecodedJWT decodedJWT = JwtProvider.verify(jwt);
            Long id = decodedJWT.getClaim("id").asLong();
            String role = decodedJWT.getClaim("role").asString();

            // 5. 세션 생성 - 세션값으로 권한처리하기 위해
            HttpSession session = req.getSession();
            LoginUser loginUser = LoginUser.builder().id(id).role(role).build();
            session.setAttribute("loginUser", loginUser);

            // 6. 다음 필터로 가!! - 없으면 DS로!!
            chain.doFilter(req, resp);
        } catch (SignatureVerificationException sve){
            error(resp, sve);
        }catch (TokenExpiredException tee){
            error(resp, tee);
        }
    }

    private void error(HttpServletResponse resp, Exception e) throws IOException {
        resp.setStatus(401);
        resp.setContentType("application/json; charset=utf-8");
        ResponseDTO<?> responseDTO = new ResponseDTO<>().fail(HttpStatus.UNAUTHORIZED, "인증 안됨", e.getMessage());
        ObjectMapper om = new ObjectMapper();
        String responseBody = om.writeValueAsString(responseDTO);
        resp.getWriter().println(responseBody);
    }
}
