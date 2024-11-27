package com.fourformance.tts_vc_web;

import static org.assertj.core.api.Assertions.assertThat;

import com.fourformance.tts_vc_web.domain.entity.QProject;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QuerydslSetupTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testQuerydslSetup() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QProject project = QProject.project;

        long count = queryFactory.selectFrom(project).fetchCount();
        assertThat(count).isNotNegative(); // 데이터가 없어도 설정이 정상이라면 0 이상의 결과를 반환
    }
}
