package com.fourformance.tts_vc_web.common.exception.common;

import com.fourformance.tts_vc_web.dto.response.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(BusinessException.class)
//    public ResponseEntity<ErrorResponseDto> handleBusinessException(BusinessException e) {
//        e.printStackTrace();
//        ErrorResponseDto errorResponse = ErrorResponseDto.of(e.getErrorCode());
//        return new ResponseEntity<>(errorResponse, e.getErrorCode().getHttpStatus());
//    }
//
//    // 기타 예외에 대한 처리 예시
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
//        e.printStackTrace();
//        ErrorResponseDto errorResponse = ErrorResponseDto.of(ErrorCode.UNKNOWN_ERROR);
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        e.printStackTrace();

        if (isSseRequest(request)) {
            // SSE 요청에 대한 에러 처리
            return handleSseError(e.getMessage());
        }

        // 일반 REST API 요청에 대한 에러 처리
        ErrorResponseDto errorResponse = ErrorResponseDto.of(e.getErrorCode());
        return new ResponseEntity<>(errorResponse, e.getErrorCode().getHttpStatus());
    }

    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e, HttpServletRequest request) {
        e.printStackTrace();

        if (isSseRequest(request)) {
            // SSE 요청에 대한 에러 처리
            return handleSseError("An unknown error occurred");
        }

        // 일반 REST API 요청에 대한 에러 처리
        ErrorResponseDto errorResponse = ErrorResponseDto.of(ErrorCode.UNKNOWN_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * SSE 요청인지 확인하는 유틸리티 메서드
     */
    private boolean isSseRequest(HttpServletRequest request) {
        // 요청 경로가 "/sse"로 시작하면 SSE 요청으로 간주
        String uri = request.getRequestURI();
        return uri.startsWith("/sse");
    }

    /**
     * SSE 에러 응답 처리
     */
    private ResponseEntity<String> handleSseError(String errorMessage) {
        // SSE 형식으로 에러 메시지 전송
        String sseErrorResponse = "data: Error: " + errorMessage + "\n\n";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_EVENT_STREAM) // Content-Type: text/event-stream
                .body(sseErrorResponse);
    }
}