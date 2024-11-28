package com.fourformance.tts_vc_web.repository.workspace;

import com.fourformance.tts_vc_web.domain.entity.*;
import com.fourformance.tts_vc_web.dto.workspace.ExportListDto;
import com.fourformance.tts_vc_web.service.common.S3Service;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class OutputAudioMetaRepositoryCustomImpl implements OutputAudioMetaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // Q-타입 인스턴스
    private final QTTSProject ttsProject = QTTSProject.tTSProject;
    private final QVCProject vcProject = QVCProject.vCProject;
    private final QOutputAudioMeta outputAudioMeta = QOutputAudioMeta.outputAudioMeta;
    private final QTTSDetail ttsDetail = QTTSDetail.tTSDetail;
    private final QVCDetail vcDetail = QVCDetail.vCDetail;
    private final QConcatProject concatProject = QConcatProject.concatProject;
    private final QConcatDetail concatDetail = QConcatDetail.concatDetail;
    private final QAPIStatus apiStatus = QAPIStatus.aPIStatus;

    // 각 프로젝트의 멤버를 위한 QMember 인스턴스
    private final QMember ttsMember = new QMember("ttsMember");
    private final QMember vcMember = new QMember("vcMember");
    private final QMember concatMember = new QMember("concatMember");

    public OutputAudioMetaRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ExportListDto> findExportHistoryBySearchCriteria(Long memberId, String keyword) {
        BooleanBuilder whereClause = new BooleanBuilder();

        // 멤버 조건 추가 (null 체크 포함)
        BooleanBuilder memberCondition = new BooleanBuilder();
        memberCondition.or(
                ttsDetail.isNotNull()
                        .and(ttsProject.isNotNull())
                        .and(ttsMember.isNotNull())
                        .and(ttsMember.id.eq(memberId))
        );
        memberCondition.or(
                vcDetail.isNotNull()
                        .and(vcProject.isNotNull())
                        .and(vcMember.isNotNull())
                        .and(vcMember.id.eq(memberId))
        );
        memberCondition.or(
                concatProject.isNotNull()
                        .and(concatMember.isNotNull())
                        .and(concatMember.id.eq(memberId))
        );

        whereClause.and(memberCondition);
        whereClause.and(outputAudioMeta.isDeleted.isFalse()); // 삭제되지 않은 히스토리 기준

        if (keyword != null && !keyword.trim().isEmpty()) {
            BooleanBuilder keywordConditions = new BooleanBuilder();
            // 각 프로젝트명
            keywordConditions.or(ttsProject.projectName.containsIgnoreCase(keyword));
            keywordConditions.or(vcProject.projectName.containsIgnoreCase(keyword));
            keywordConditions.or(concatProject.projectName.containsIgnoreCase(keyword));

            // 파일명 추출 및 필터링 (대소문자 무시)
//            keywordConditions.or(
//                    Expressions.stringTemplate(
//                            "LOWER(SUBSTRING({0}, LOCATE('/', REVERSE({0})) + 1)) LIKE {1}",
//                            outputAudioMeta.bucketRoute,
//                            "%" + keyword.toLowerCase() + "%"
//                    )
//            );

            // 최신 상태 필터링 (LOWER + LIKE 사용)
            keywordConditions.or(
                    Expressions.booleanTemplate(
                            "LOWER({0}) LIKE {1}",
                            JPAExpressions.select(apiStatus.apiUnitStatusConst.stringValue())
                                    .from(apiStatus)
                                    .where(apiStatus.ttsDetail.eq(ttsDetail)
                                            .or(apiStatus.vcDetail.eq(vcDetail)))
                                    .orderBy(apiStatus.responseAt.desc())
                                    .limit(1),
                            "%" + keyword.toLowerCase() + "%"
                    )
            );

            // 스크립트 필터링
            keywordConditions.or(ttsDetail.unitScript.containsIgnoreCase(keyword));
            keywordConditions.or(vcDetail.unitScript.containsIgnoreCase(keyword));
            keywordConditions.or(
                    concatDetail.unitScript.containsIgnoreCase(keyword)
                            .and(concatDetail.concatProject.eq(concatProject))
            );

            // 프로젝트 타입 필터링
            keywordConditions.or(ttsProject.projectType.containsIgnoreCase(keyword));
            keywordConditions.or(vcProject.projectType.containsIgnoreCase(keyword));
            keywordConditions.or(concatProject.projectType.containsIgnoreCase(keyword));

            whereClause.and(keywordConditions);
        }

        // 쿼리 작성
        List<ExportListDto> outputAudioMetas = queryFactory.select(
                        Projections.constructor(ExportListDto.class,
                                outputAudioMeta.id,
                                outputAudioMeta.projectType.stringValue(),
                                concatProject.projectName.coalesce(ttsProject.projectName, vcProject.projectName),
                                Expressions.stringTemplate(
                                        "SUBSTRING({0}, LOCATE('/', REVERSE({0})) + 1)",
                                        outputAudioMeta.audioUrl
                                ), // 파일명 추출
                                concatDetail.unitScript.coalesce(ttsDetail.unitScript, vcDetail.unitScript),
                                JPAExpressions.select(apiStatus.apiUnitStatusConst.stringValue())
                                        .from(apiStatus)
                                        .where(apiStatus.ttsDetail.eq(ttsDetail)
                                                .or(apiStatus.vcDetail.eq(vcDetail)))
                                        .orderBy(apiStatus.responseAt.desc())
                                        .limit(1), // 최신 상태
                                outputAudioMeta.createdAt, // 생성 날짜
//                                outputAudioMeta.audioUrl, // 오디오 URL
                                outputAudioMeta.bucketRoute // 버킷루트 추가
                        )
                )
                .from(outputAudioMeta)
                .leftJoin(outputAudioMeta.ttsDetail, ttsDetail)
                .leftJoin(ttsDetail.ttsProject, ttsProject)
                .leftJoin(ttsProject.member, ttsMember) // TTS 멤버 조인
                .leftJoin(outputAudioMeta.vcDetail, vcDetail)
                .leftJoin(vcDetail.vcProject, vcProject)
                .leftJoin(vcProject.member, vcMember) // VC 멤버 조인
                .leftJoin(outputAudioMeta.concatProject, concatProject)
                .leftJoin(concatProject.member, concatMember) // CONCAT 멤버 조인
                .leftJoin(concatDetail).on(concatDetail.concatProject.id.eq(concatProject.id)) // ConcatDetail 조인 (단방향)
                .where(whereClause)
                .fetch();

        return outputAudioMetas;
    }

    @Override
    public Page<ExportListDto> findExportHistoryBySearchCriteria(Long memberId, String keyword, Pageable pageable) {
        BooleanBuilder whereClause = new BooleanBuilder();

        // 멤버 조건 추가
        BooleanBuilder memberCondition = new BooleanBuilder();
        memberCondition.or(
                ttsDetail.isNotNull()
                        .and(ttsProject.isNotNull())
                        .and(ttsMember.isNotNull())
                        .and(ttsMember.id.eq(memberId))
        );
        memberCondition.or(
                vcDetail.isNotNull()
                        .and(vcProject.isNotNull())
                        .and(vcMember.isNotNull())
                        .and(vcMember.id.eq(memberId))
        );
        memberCondition.or(
                concatProject.isNotNull()
                        .and(concatMember.isNotNull())
                        .and(concatMember.id.eq(memberId))
        );

        whereClause.and(memberCondition);
        whereClause.and(outputAudioMeta.isDeleted.isFalse());

        // 키워드 조건 추가
        if (keyword != null && !keyword.trim().isEmpty()) {
            BooleanBuilder keywordConditions = new BooleanBuilder();
            keywordConditions.or(ttsProject.projectName.containsIgnoreCase(keyword));
            keywordConditions.or(vcProject.projectName.containsIgnoreCase(keyword));
            keywordConditions.or(concatProject.projectName.containsIgnoreCase(keyword));
            keywordConditions.or(
                    Expressions.stringTemplate(
                            "SUBSTRING({0}, LOCATE('/', {0}) + 1)",
                            outputAudioMeta.bucketRoute
                    ).containsIgnoreCase(keyword)
            );

            whereClause.and(keywordConditions);
        }

        // 전체 개수 조회
        long total = queryFactory.select(outputAudioMeta.countDistinct())
                .from(outputAudioMeta)
                .leftJoin(outputAudioMeta.ttsDetail, ttsDetail)
                .leftJoin(ttsDetail.ttsProject, ttsProject)
                .leftJoin(ttsProject.member, ttsMember)
                .leftJoin(outputAudioMeta.vcDetail, vcDetail)
                .leftJoin(vcDetail.vcProject, vcProject)
                .leftJoin(vcProject.member, vcMember)
                .leftJoin(outputAudioMeta.concatProject, concatProject)
                .leftJoin(concatProject.member, concatMember)
                .where(whereClause)
                .fetchOne();

        // 페이징된 데이터 조회
        List<ExportListDto> results = queryFactory.select(
                        Projections.constructor(ExportListDto.class,
                                outputAudioMeta.id,
                                outputAudioMeta.projectType.stringValue(),
                                concatProject.projectName.coalesce(ttsProject.projectName, vcProject.projectName),
                                Expressions.stringTemplate(
                                        "SUBSTRING_INDEX({0}, '/', -1)",
                                        outputAudioMeta.audioUrl
                                ), // 파일명 추출
                                concatDetail.unitScript.coalesce(ttsDetail.unitScript, vcDetail.unitScript),
                                apiStatus.apiUnitStatusConst.stringValue(), // 최신 상태 직접 조인 후 가져오기
                                outputAudioMeta.createdAt,
                                outputAudioMeta.bucketRoute
                        )
                )
                .from(outputAudioMeta)
                .leftJoin(outputAudioMeta.ttsDetail, ttsDetail)
                .leftJoin(ttsDetail.ttsProject, ttsProject)
                .leftJoin(ttsProject.member, ttsMember)
                .leftJoin(outputAudioMeta.vcDetail, vcDetail)
                .leftJoin(vcDetail.vcProject, vcProject)
                .leftJoin(vcProject.member, vcMember)
                .leftJoin(outputAudioMeta.concatProject, concatProject)
                .leftJoin(concatProject.member, concatMember)
                .leftJoin(concatDetail).on(concatDetail.concatProject.id.eq(concatProject.id))
                .leftJoin(apiStatus).on(apiStatus.ttsDetail.eq(ttsDetail).or(apiStatus.vcDetail.eq(vcDetail))) // APIStatus와 조인
                .where(whereClause)
                .orderBy(outputAudioMeta.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        // Page 객체로 반환
        return new PageImpl<>(results, pageable, total);
    }
}
