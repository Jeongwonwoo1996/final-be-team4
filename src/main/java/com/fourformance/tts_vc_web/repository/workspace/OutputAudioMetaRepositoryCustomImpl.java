package com.fourformance.tts_vc_web.repository.workspace;

import com.fourformance.tts_vc_web.domain.entity.QAPIStatus;
import com.fourformance.tts_vc_web.domain.entity.QConcatDetail;
import com.fourformance.tts_vc_web.domain.entity.QConcatProject;
import com.fourformance.tts_vc_web.domain.entity.QMember;
import com.fourformance.tts_vc_web.domain.entity.QOutputAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.QTTSDetail;
import com.fourformance.tts_vc_web.domain.entity.QTTSProject;
import com.fourformance.tts_vc_web.domain.entity.QVCDetail;
import com.fourformance.tts_vc_web.domain.entity.QVCProject;
import com.fourformance.tts_vc_web.dto.workspace.ExportListDto;
import com.fourformance.tts_vc_web.dto.workspace.ExportWithDownloadLinkDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class OutputAudioMetaRepositoryCustomImpl implements OutputAudioMetaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 이번에 추가한거
    QConcatDetail subConcatDetail = new QConcatDetail("subConcatDetail");
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

            keywordConditions.or(ttsDetail.unitScript.containsIgnoreCase(keyword));
            keywordConditions.or(vcDetail.unitScript.containsIgnoreCase(keyword));
//            keywordConditions.or(concatDetail.unitScript.containsIgnoreCase(keyword));

            keywordConditions.or(
                    JPAExpressions.select(apiStatus.apiUnitStatusConst.stringValue())
                            .from(apiStatus)
                            .where(
                                    apiStatus.ttsDetail.eq(ttsDetail)
                                            .or(apiStatus.vcDetail.eq(vcDetail))
                                            .and(apiStatus.apiUnitStatusConst.stringValue()
                                                    .containsIgnoreCase(keyword)) // 키워드 조건
                                            .and(apiStatus.createdDate.eq(
                                                    JPAExpressions.select(apiStatus.createdDate.max())
                                                            .from(apiStatus)
                                                            .where(
                                                                    apiStatus.ttsDetail.eq(ttsDetail)
                                                                            .or(apiStatus.vcDetail.eq(vcDetail))
                                                            )
                                            ))
                            )
                            .exists() // 키워드 조건에 맞는 데이터가 있는 경우만
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

        List<ExportListDto> results = queryFactory.select(
                        Projections.constructor(ExportListDto.class,
                                outputAudioMeta.id,
                                outputAudioMeta.projectType.stringValue(),
                                concatProject.projectName.coalesce(ttsProject.projectName, vcProject.projectName),
                                Expressions.stringTemplate("SUBSTRING_INDEX({0}, '/', -1)", outputAudioMeta.audioUrl),
                                concatDetail.unitScript.coalesce(ttsDetail.unitScript, vcDetail.unitScript),
                                JPAExpressions.select(apiStatus.apiUnitStatusConst.stringValue())
                                        .from(apiStatus)
                                        .where(
                                                apiStatus.ttsDetail.eq(ttsDetail) // TTS에 해당
                                                        .or(apiStatus.vcDetail.eq(vcDetail)) // VC에 해당
                                                        .and(apiStatus.createdDate.eq(
                                                                JPAExpressions.select(apiStatus.createdDate.max())
                                                                        .from(apiStatus)
                                                                        .where(
                                                                                apiStatus.ttsDetail.eq(ttsDetail)
                                                                                        .or(apiStatus.vcDetail.eq(vcDetail))
                                                                        )
                                                        ))
                                        ),
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
                .leftJoin(concatDetail).on(concatDetail.concatProject.eq(outputAudioMeta.concatProject))
                .leftJoin(apiStatus).on(
                        apiStatus.createdDate.eq(
                                JPAExpressions.select(apiStatus.createdDate.max())
                                        .from(apiStatus)
                                        .where(
                                                apiStatus.ttsDetail.eq(ttsDetail)
                                                        .or(apiStatus.vcDetail.eq(vcDetail))
                                        )
                        )
                )
                .where(whereClause)
                .orderBy(outputAudioMeta.createdAt.desc())
                .fetch();

        return results;
    }

//    @Override
//    public Page<ExportListDto> findExportHistoryBySearchCriteria(Long memberId, String keyword, Pageable pageable) {
//        BooleanBuilder whereClause = new BooleanBuilder();
//
//        // 멤버 조건 추가
//        BooleanBuilder memberCondition = new BooleanBuilder();
//        memberCondition.or(
//                ttsDetail.isNotNull()
//                        .and(ttsProject.isNotNull())
//                        .and(ttsMember.isNotNull())
//                        .and(ttsMember.id.eq(memberId))
//        );
//        memberCondition.or(
//                vcDetail.isNotNull()
//                        .and(vcProject.isNotNull())
//                        .and(vcMember.isNotNull())
//                        .and(vcMember.id.eq(memberId))
//        );
//        memberCondition.or(
//                concatProject.isNotNull()
//                        .and(concatMember.isNotNull())
//                        .and(concatMember.id.eq(memberId))
//        );
//
//        whereClause.and(memberCondition);
//        whereClause.and(outputAudioMeta.isDeleted.isFalse());
//
//        // 키워드 조건 추가
//        if (keyword != null && !keyword.trim().isEmpty()) {
//            BooleanBuilder keywordConditions = new BooleanBuilder();
//            keywordConditions.or(ttsProject.projectName.containsIgnoreCase(keyword));
//            keywordConditions.or(vcProject.projectName.containsIgnoreCase(keyword));
//            keywordConditions.or(concatProject.projectName.containsIgnoreCase(keyword));
//            keywordConditions.or(
//                    Expressions.stringTemplate(
//                            "SUBSTRING({0}, LOCATE('/', {0}) + 1)",
//                            outputAudioMeta.bucketRoute
//                    ).containsIgnoreCase(keyword)
//            );
//
//            keywordConditions.or(ttsDetail.unitScript.containsIgnoreCase(keyword));
//            keywordConditions.or(vcDetail.unitScript.containsIgnoreCase(keyword));
////            keywordConditions.or(concatDetail.unitScript.containsIgnoreCase(keyword));
//
//            keywordConditions.or(
//                    JPAExpressions.select(apiStatus.apiUnitStatusConst.stringValue())
//                            .from(apiStatus)
//                            .where(
//                                    apiStatus.ttsDetail.eq(ttsDetail)
//                                            .or(apiStatus.vcDetail.eq(vcDetail))
//                                            .and(apiStatus.apiUnitStatusConst.stringValue()
//                                                    .containsIgnoreCase(keyword)) // 키워드 조건
//                                            .and(apiStatus.createdDate.eq(
//                                                    JPAExpressions.select(apiStatus.createdDate.max())
//                                                            .from(apiStatus)
//                                                            .where(
//                                                                    apiStatus.ttsDetail.eq(ttsDetail)
//                                                                            .or(apiStatus.vcDetail.eq(vcDetail))
//                                                            )
//                                            ))
//                            )
//                            .exists() // 키워드 조건에 맞는 데이터가 있는 경우만
//            );
//            whereClause.and(keywordConditions);
//        }
//
//        // 전체 개수 조회
//        long total = queryFactory.select(outputAudioMeta.countDistinct())
//                .from(outputAudioMeta)
//                .leftJoin(outputAudioMeta.ttsDetail, ttsDetail)
//                .leftJoin(ttsDetail.ttsProject, ttsProject)
//                .leftJoin(ttsProject.member, ttsMember)
//                .leftJoin(outputAudioMeta.vcDetail, vcDetail)
//                .leftJoin(vcDetail.vcProject, vcProject)
//                .leftJoin(vcProject.member, vcMember)
//                .leftJoin(outputAudioMeta.concatProject, concatProject)
//                .leftJoin(concatProject.member, concatMember)
//                .where(whereClause)
//                .fetchOne();
//
//        List<ExportListDto> results = queryFactory.select(
//                        Projections.constructor(ExportListDto.class,
//                                outputAudioMeta.id,
//                                outputAudioMeta.projectType.stringValue(),
//                                concatProject.projectName.coalesce(ttsProject.projectName, vcProject.projectName),
//                                Expressions.stringTemplate("SUBSTRING_INDEX({0}, '/', -1)", outputAudioMeta.audioUrl),
//                                concatDetail.unitScript.coalesce(ttsDetail.unitScript, vcDetail.unitScript),
//                                JPAExpressions.select(apiStatus.apiUnitStatusConst.stringValue())
//                                        .from(apiStatus)
//                                        .where(
//                                                apiStatus.ttsDetail.eq(ttsDetail) // TTS에 해당
//                                                        .or(apiStatus.vcDetail.eq(vcDetail)) // VC에 해당
//                                                        .and(apiStatus.createdDate.eq(
//                                                                JPAExpressions.select(apiStatus.createdDate.max())
//                                                                        .from(apiStatus)
//                                                                        .where(
//                                                                                apiStatus.ttsDetail.eq(ttsDetail)
//                                                                                        .or(apiStatus.vcDetail.eq(vcDetail))
//                                                                        )
//                                                        ))
//                                        ),
//                                outputAudioMeta.createdAt,
//                                outputAudioMeta.bucketRoute
//                        )
//                )
//                .from(outputAudioMeta)
//                .leftJoin(outputAudioMeta.ttsDetail, ttsDetail)
//                .leftJoin(ttsDetail.ttsProject, ttsProject)
//                .leftJoin(ttsProject.member, ttsMember)
//                .leftJoin(outputAudioMeta.vcDetail, vcDetail)
//                .leftJoin(vcDetail.vcProject, vcProject)
//                .leftJoin(vcProject.member, vcMember)
//                .leftJoin(outputAudioMeta.concatProject, concatProject)
//                .leftJoin(concatProject.member, concatMember)
//                .leftJoin(concatDetail).on(concatDetail.concatProject.eq(outputAudioMeta.concatProject))
//                .leftJoin(apiStatus).on(
//                        apiStatus.createdDate.eq(
//                                JPAExpressions.select(apiStatus.createdDate.max())
//                                        .from(apiStatus)
//                                        .where(
//                                                apiStatus.ttsDetail.eq(ttsDetail)
//                                                        .or(apiStatus.vcDetail.eq(vcDetail))
//                                        )
//                        )
//                )
//                .where(whereClause)
//                .orderBy(outputAudioMeta.createdAt.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        // Page 객체로 반환
//        return new PageImpl<>(results, pageable, total);
//    }


    @Override
    public Page<ExportWithDownloadLinkDto> findExportHistoryBySearchCriteria(Long memberId, String keyword,
                                                                             Pageable pageable) {
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

            keywordConditions.or(ttsDetail.unitScript.containsIgnoreCase(keyword));
            keywordConditions.or(vcDetail.unitScript.containsIgnoreCase(keyword));
            keywordConditions.or(concatDetail.unitScript.containsIgnoreCase(keyword));

            keywordConditions.or(
                    JPAExpressions.select(apiStatus.apiUnitStatusConst.stringValue())
                            .from(apiStatus)
                            .where(
                                    apiStatus.ttsDetail.eq(ttsDetail)
                                            .or(apiStatus.vcDetail.eq(vcDetail))
                                            .and(apiStatus.apiUnitStatusConst.stringValue()
                                                    .containsIgnoreCase(keyword))
                                            .and(apiStatus.createdDate.eq(
                                                    JPAExpressions.select(apiStatus.createdDate.max())
                                                            .from(apiStatus)
                                                            .where(
                                                                    apiStatus.ttsDetail.eq(ttsDetail)
                                                                            .or(apiStatus.vcDetail.eq(vcDetail))
                                                            )
                                            ))
                            )
                            .exists()
            );
            whereClause.and(keywordConditions);
        }

        // QueryResults로 데이터와 총 개수 조회
        QueryResults<ExportWithDownloadLinkDto> queryResults = queryFactory
                .select(
                        Projections.constructor(ExportWithDownloadLinkDto.class,
                                outputAudioMeta.id,
                                Expressions.stringTemplate("SUBSTRING_INDEX({0}, '/', -1)", outputAudioMeta.audioUrl),
                                outputAudioMeta.audioUrl,
                                JPAExpressions.select(apiStatus.apiUnitStatusConst.stringValue())
                                        .from(apiStatus)
                                        .where(
                                                apiStatus.ttsDetail.eq(ttsDetail)
                                                        .or(apiStatus.vcDetail.eq(vcDetail))
                                                        .and(apiStatus.createdDate.eq(
                                                                JPAExpressions.select(apiStatus.createdDate.max())
                                                                        .from(apiStatus)
                                                                        .where(
                                                                                apiStatus.ttsDetail.eq(ttsDetail)
                                                                                        .or(apiStatus.vcDetail.eq(
                                                                                                vcDetail))
                                                                        )
                                                        ))
                                        ),
                                concatProject.projectName.coalesce(ttsProject.projectName, vcProject.projectName),
                                outputAudioMeta.projectType.stringValue(),
                                concatDetail.unitScript.coalesce(ttsDetail.unitScript, vcDetail.unitScript),
                                outputAudioMeta.createdAt
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
                .leftJoin(concatDetail).on(
                        concatDetail.concatProject.eq(outputAudioMeta.concatProject)
                                .and(concatDetail.createdAt.eq(
                                        JPAExpressions.select(concatDetail.createdAt.max())
                                                .from(concatDetail)
                                                .where(concatDetail.concatProject.eq(outputAudioMeta.concatProject))
                                ))
                ).leftJoin(apiStatus).on(
                        apiStatus.createdDate.eq(
                                JPAExpressions.select(apiStatus.createdDate.max())
                                        .from(apiStatus)
                                        .where(
                                                apiStatus.ttsDetail.eq(ttsDetail)
                                                        .or(apiStatus.vcDetail.eq(vcDetail))
                                        )
                        )
                )
                .where(whereClause)
                .distinct() // 중복 제거
                .orderBy(outputAudioMeta.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        // QueryResults에서 데이터와 전체 개수 추출
        List<ExportWithDownloadLinkDto> results = queryResults.getResults();
        long total = queryResults.getTotal();

        // Page 객체로 반환
        return new PageImpl<>(results, pageable, total);
    }
}

