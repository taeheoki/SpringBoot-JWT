package com.example.jwtstudy.controller;

import com.example.jwtstudy.core.exception.Exception400;
import com.example.jwtstudy.core.exception.Exception403;
import com.example.jwtstudy.core.jwt.JwtProvider;
import com.example.jwtstudy.core.session.LoginUser;
import com.example.jwtstudy.dto.ResponseDTO;
import com.example.jwtstudy.dto.user.UserRequest;
import com.example.jwtstudy.model.log.LoginLog;
import com.example.jwtstudy.model.log.LoginLogRepository;
import com.example.jwtstudy.model.user.User;
import com.example.jwtstudy.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final LoginLogRepository loginLogRepository;
    private final HttpSession session;

    @GetMapping("/") // 인증 불필요
    public ResponseEntity<?> main() {
        return ResponseEntity.ok().build();
    }

    @Transactional
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest.LoginInDTO loginInDTO, HttpServletRequest request) {
        Optional<User> userOP = userRepository.findByUsernameAndPassword(loginInDTO.getUsername(), loginInDTO.getPassword());
        if (userOP.isPresent()) {
            // 1. 유저 정보 꺼내기
            User loginUser = userOP.get();

            // 2. JWT 생성하기
            String jwt = JwtProvider.create(userOP.get());

            // 3. 최종 로그인 날짜 기록 (더티체킹 - update 쿼리 발생)
            loginUser.setUpdatedAt(LocalDateTime.now());

            // 4. 로그 테이블 기록
            LoginLog loginLog = LoginLog.builder()
                    .userId(loginUser.getId())
                    .userAgent(request.getHeader("User-Agent"))
                    .clientIP(request.getRemoteAddr())
                    .build();
            loginLogRepository.save(loginLog);

            // 5. 응답 DTO 생성
            ResponseDTO<?> responseDTO = new ResponseDTO<>().data(loginUser);
            return ResponseEntity.ok().header(JwtProvider.HEADER, jwt).body(responseDTO);
        } else {
            throw new Exception400("유저네임 혹은 아이디가 잘못되었습니다");
        }
    }

    // taeheoki 로그인해서 /user/2/v1 호출해보기
    @GetMapping("/user/{id}/v1") // 인증, 권한 필요
    public ResponseEntity<?> userV1(@PathVariable Long id) {
        // 권한처리 이 사람이 이 정보의 주인
        LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
        if (loginUser.getId() == id) {
            return ResponseEntity.ok().build();
        } else {
            throw new Exception403("해당 리소스의 주인이 아닙니다");
        }
    }

    // 관리자로 로그인 해보기
    @GetMapping("/user/{id}/v2") // 인증, 권한 필요 and 관리자 접근 가능
    public ResponseEntity<?> userV2(@PathVariable Long id) {
        // 권한처리 이 사람이 이 정보의 주인
        LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
        if (loginUser.getId() == id && loginUser.getRole().equals("ADMIN")) {
            return ResponseEntity.ok().build();
        } else {
            throw new Exception403("해당 리소스의 주인이 아닙니다");
        }
    }
}
