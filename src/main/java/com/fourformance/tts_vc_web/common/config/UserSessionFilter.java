package com.fourformance.tts_vc_web.common.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserSessionFilter implements Filter {

    @Override
    public void doFilter(
            jakarta.servlet.ServletRequest request,
            jakarta.servlet.ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false); // 기존 세션 가져오기

        Long memberId = null;

        if (session != null) {
            Object sessionMemberId = session.getAttribute("memberId");
            if (sessionMemberId instanceof Long) { // 세션 값이 Long인지 확인
                memberId = (Long) sessionMemberId;
            } else if (sessionMemberId != null) { // Long이 아닌 경우 문자열로 처리
                try {
                    memberId = Long.valueOf(sessionMemberId.toString());
                } catch (NumberFormatException e) {
                    memberId = null; // 변환 실패 시 null 처리
                }
            }
        }

        if (memberId == null) {
            memberId = -1L; // 기본값 설정 (-1은 익명 사용자를 나타냄)
        }

        // UserSessionContext에 사용자 ID 설정
        UserSessionContext.setCurrentUser(memberId.toString()); // String 타입으로 변환하여 전달

        try {
            chain.doFilter(request, response);
        } finally {
            UserSessionContext.clear(); // 요청 처리 후 컨텍스트 초기화
        }
    }
}
