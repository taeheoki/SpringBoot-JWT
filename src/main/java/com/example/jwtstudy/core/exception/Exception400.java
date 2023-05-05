package com.example.jwtstudy.core.exception;

import com.example.jwtstudy.dto.ResponseDTO;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 유효성 실패
@Getter
public class Exception400 extends RuntimeException {
    public Exception400(String message) {
        super(message);
    }

    public ResponseDTO<?> body(){
        ResponseDTO<String> responseDto = new ResponseDTO<>();
        responseDto.fail(HttpStatus.BAD_REQUEST, "badRequest", getMessage());
        return responseDto;
    }

    public HttpStatus status(){
        return HttpStatus.BAD_REQUEST;
    }
}