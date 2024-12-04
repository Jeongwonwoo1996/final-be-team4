package com.fourformance.tts_vc_web.common.config;


import org.springframework.data.domain.AuditorAware;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        String currentUser = UserSessionContext.getCurrentUser();
        if (currentUser != null) {
            try {
                return Optional.of(Long.valueOf(currentUser));
            } catch (NumberFormatException e) {
                return Optional.of(-1L); // 익명 사용자
            }
        }
        return Optional.of(-1L); // 익명 사용자
    }


//    @Override
//    public Optional<String> getCurrentAuditor() {
////        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String userId = "test"; // 세션에 저장된 사용자 아이디 이거를 입력을 하시면,
////        if(authentication != null){
////            userId = authentication.getName(); // 현재 로그인한 사용자의 정보를 조회하여 사용자의 이름을 등록자와 수정자로 지정합니다.
////        }
//        return Optional.of(userId);
//    }

}