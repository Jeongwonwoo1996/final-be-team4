package com.fourformance.tts_vc_web.repository.workspace;

import com.fourformance.tts_vc_web.domain.entity.ConcatProject;
import com.fourformance.tts_vc_web.domain.entity.Project;
import com.fourformance.tts_vc_web.domain.entity.QConcatDetail;
import com.fourformance.tts_vc_web.domain.entity.QConcatProject;
import com.fourformance.tts_vc_web.domain.entity.QProject;
import com.fourformance.tts_vc_web.domain.entity.QTTSDetail;
import com.fourformance.tts_vc_web.domain.entity.QTTSProject;
import com.fourformance.tts_vc_web.domain.entity.QVCDetail;
import com.fourformance.tts_vc_web.domain.entity.QVCProject;
import com.fourformance.tts_vc_web.domain.entity.TTSProject;
import com.fourformance.tts_vc_web.domain.entity.VCProject;
import com.fourformance.tts_vc_web.dto.workspace.ProjectListDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProjectRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    QProject project = QProject.project;
    QTTSProject ttsProject = QTTSProject.tTSProject;
    QVCProject vcProject = QVCProject.vCProject;
    QConcatProject concatProject = QConcatProject.concatProject;
    QTTSDetail ttsDetail = QTTSDetail.tTSDetail;
    QVCDetail vcDetail = QVCDetail.vCDetail;
    QConcatDetail concatDetail = QConcatDetail.concatDetail;


    @Override
    public List<ProjectListDto> findProjectsBySearchCriteria(Long memberId, String keyword) {

        // 공통 필터 조건
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(project.member.id.eq(memberId)); // 멤버 ID 조건
        whereClause.and(project.isDeleted.isFalse());    // 삭제되지 않은 프로젝트

        // 키워드 검색 조건 (아래 중 하나라도 만족해야 검색 됨)
        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordConditions = new BooleanBuilder();
            keywordConditions.or(project.projectName.containsIgnoreCase(keyword)); // 프로젝트 이름 검색

            keywordConditions.or(ttsDetail.unitScript.containsIgnoreCase(keyword)); // TTS 스크립트 검색
            keywordConditions.or(vcDetail.unitScript.containsIgnoreCase(keyword)); // VC 스크립트 검색
            keywordConditions.or(concatDetail.unitScript.containsIgnoreCase(keyword)); // Concat 스크립트 검색

            keywordConditions.or(ttsProject.apiStatus.stringValue().containsIgnoreCase(keyword)); // TTS 상태 검색
            keywordConditions.or(vcProject.apiStatus.stringValue().containsIgnoreCase(keyword)); // VC 상태 검색
//            keywordConditions.or(concatProject..stringValue().containsIgnoreCase(keyword)); // Concat도 그냥 컬럼으로 관리할걸 히스토리 상태에 대한 키가 히스토리 엔티티에 있는데... 일단 보류

            keywordConditions.or(ttsProject.projectType.containsIgnoreCase(keyword)); // TTS 타입
            keywordConditions.or(vcProject.projectType.containsIgnoreCase(keyword)); // VC 타입
            keywordConditions.or(concatProject.projectType.containsIgnoreCase(keyword)); // Concat 타입

            whereClause.and(keywordConditions); // 키워드 조건을 최종적으로 AND 조건에 추가
        }

        // QueryDSL로 데이터 조회
        List<ProjectListDto> result = queryFactory
                .selectFrom(project)
                // TTSDetail과 firstScript 조건으로 디테일 조인
                .leftJoin(ttsProject).on(ttsProject.id.eq(project.id))
                .leftJoin(ttsDetail).on(
                        ttsDetail.ttsProject.id.eq(ttsProject.id)
                                .and(ttsDetail.isDeleted.isFalse())
                                .and(ttsDetail.unitSequence.eq(
                                        JPAExpressions.select(ttsDetail.unitSequence.min())
                                                .from(ttsDetail)
                                                .where(ttsDetail.ttsProject.id.eq(ttsProject.id)
                                                        .and(ttsDetail.isDeleted.isFalse()))
                                ))
                )
                // VCDetail과 firstScript 조건 조인
                .leftJoin(vcProject).on(vcProject.id.eq(project.id))
                .leftJoin(vcDetail).on(
                        vcDetail.vcProject.id.eq(vcProject.id)
                                .and(vcDetail.isDeleted.isFalse())
                                .and(vcDetail.createdAt.eq(
                                        JPAExpressions.select(vcDetail.createdAt.min())
                                                .from(vcDetail)
                                                .where(vcDetail.vcProject.id.eq(vcProject.id)
                                                        .and(vcDetail.isDeleted.isFalse()))
                                ))
                )
                // ConcatDetail과 firstScript 조건 조인
                .leftJoin(concatProject).on(concatProject.id.eq(project.id))
                .leftJoin(concatDetail).on(
                        concatDetail.concatProject.id.eq(concatProject.id)
                                .and(concatDetail.isDeleted.isFalse())
                                .and(concatDetail.audioSeq.eq(
                                        JPAExpressions.select(concatDetail.audioSeq.min())
                                                .from(concatDetail)
                                                .where(concatDetail.concatProject.id.eq(concatProject.id)
                                                        .and(concatDetail.isDeleted.isFalse()))
                                ))
                )
                .where(whereClause) // 검색 필터 조건 적용
                .orderBy(project.updatedAt.desc()) // 프로젝트 업데이트 날짜 기준 내림차순 정렬
                .fetch()
                .stream()
                .map(p -> {
                    ProjectListDto dto = new ProjectListDto();
                    dto.setProjectId(p.getId());
                    dto.setProjectName(p.getProjectName());
                    dto.setUpdatedAt(p.getUpdatedAt());
                    dto.setCreatedAt(p.getCreatedAt());

                    // TTS 프로젝트 처리
                    if (p instanceof TTSProject) {
                        TTSProject tts = (TTSProject) p;
                        String firstScript = queryFactory
                                .select(ttsDetail.unitScript)
                                .from(ttsDetail)
                                .where(ttsDetail.ttsProject.id.eq(tts.getId())
                                        .and(ttsDetail.isDeleted.isFalse()))
                                .orderBy(ttsDetail.unitSequence.asc())
                                .fetchFirst();
                        dto.setScript(firstScript);
                        dto.setProjectStatus(tts.getApiStatus().toString());
                        dto.setProjectType("TTS");

                        // VC 프로젝트 처리
                    } else if (p instanceof VCProject) {
                        VCProject vc = (VCProject) p;
                        String firstScript = queryFactory
                                .select(vcDetail.unitScript)
                                .from(vcDetail)
                                .where(vcDetail.vcProject.id.eq(vc.getId())
                                        .and(vcDetail.isDeleted.isFalse()))
                                .orderBy(vcDetail.createdAt.asc())
                                .fetchFirst();
                        dto.setScript(firstScript);
                        dto.setProjectStatus(vc.getApiStatus().toString());
                        dto.setProjectType("VC");

                        // Concat 프로젝트 처리
                    } else if (p instanceof ConcatProject) {
                        ConcatProject concat = (ConcatProject) p;
                        String firstScript = queryFactory
                                .select(concatDetail.unitScript)
                                .from(concatDetail)
                                .where(concatDetail.concatProject.id.eq(concat.getId())
                                        .and(concatDetail.isDeleted.isFalse()))
                                .orderBy(concatDetail.audioSeq.asc())
                                .fetchFirst();
                        dto.setScript(firstScript);
                        System.out.println("concatScript = " + firstScript);
                        dto.setProjectType("CONCAT");
                    }

                    return dto;
                })
                .toList();

        return result;
    }

    @Override
    public Page<ProjectListDto> findProjectsBySearchCriteria(Long memberId, String keyword, Pageable pageable) {
        QProject project = QProject.project;
        QTTSProject ttsProject = QTTSProject.tTSProject;
        QVCProject vcProject = QVCProject.vCProject;
        QConcatProject concatProject = QConcatProject.concatProject;
        QTTSDetail ttsDetail = QTTSDetail.tTSDetail;
        QVCDetail vcDetail = QVCDetail.vCDetail;
        QConcatDetail concatDetail = QConcatDetail.concatDetail;

        // 공통 필터 조건
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(project.member.id.eq(memberId)); // 멤버 ID 조건
        whereClause.and(project.isDeleted.isFalse());    // 삭제되지 않은 프로젝트

        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordConditions = new BooleanBuilder();
            keywordConditions.or(project.projectName.containsIgnoreCase(keyword));
            whereClause.and(keywordConditions);
        }

        // 전체 개수 조회
        long totalCount = queryFactory
                .select(project.count())
                .from(project)
                .where(whereClause)
                .fetchOne();

        // 페이징 처리된 데이터 조회
        List<Project> projects = queryFactory
                .selectFrom(project)
                .leftJoin(ttsProject).on(ttsProject.id.eq(project.id))
                .leftJoin(ttsDetail).on(
                        ttsDetail.ttsProject.id.eq(ttsProject.id)
                                .and(ttsDetail.isDeleted.isFalse())
                                .and(ttsDetail.unitSequence.eq(
                                        JPAExpressions.select(ttsDetail.unitSequence.min())
                                                .from(ttsDetail)
                                                .where(ttsDetail.ttsProject.id.eq(ttsProject.id)
                                                        .and(ttsDetail.isDeleted.isFalse()))
                                ))
                )
                .leftJoin(vcProject).on(vcProject.id.eq(project.id))
                .leftJoin(vcDetail).on(
                        vcDetail.vcProject.id.eq(vcProject.id)
                                .and(vcDetail.isDeleted.isFalse())
                                .and(vcDetail.createdAt.eq(
                                        JPAExpressions.select(vcDetail.createdAt.min())
                                                .from(vcDetail)
                                                .where(vcDetail.vcProject.id.eq(vcProject.id)
                                                        .and(vcDetail.isDeleted.isFalse()))
                                ))
                )
                .leftJoin(concatProject).on(concatProject.id.eq(project.id))
                .leftJoin(concatDetail).on(
                        concatDetail.concatProject.id.eq(concatProject.id)
                                .and(concatDetail.isDeleted.isFalse())
                                .and(concatDetail.audioSeq.eq(
                                        JPAExpressions.select(concatDetail.audioSeq.min())
                                                .from(concatDetail)
                                                .where(concatDetail.concatProject.id.eq(concatProject.id)
                                                        .and(concatDetail.isDeleted.isFalse()))
                                ))
                )
                .where(whereClause)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(project.updatedAt.desc())
                .fetch();

        // DTO 변환
        List<ProjectListDto> dtos = projects.stream().map(p -> {
            ProjectListDto dto = new ProjectListDto();
            dto.setProjectId(p.getId());
            dto.setProjectName(p.getProjectName());
            dto.setUpdatedAt(p.getUpdatedAt());
            dto.setCreatedAt(p.getCreatedAt());

            if (p instanceof TTSProject) {
                TTSProject tts = (TTSProject) p;
                String firstScript = queryFactory
                        .select(ttsDetail.unitScript)
                        .from(ttsDetail)
                        .where(ttsDetail.ttsProject.id.eq(tts.getId())
                                .and(ttsDetail.isDeleted.isFalse()))
                        .orderBy(ttsDetail.unitSequence.asc())
                        .fetchFirst();
                dto.setScript(firstScript);
                dto.setProjectStatus(tts.getApiStatus().toString());
                dto.setProjectType("TTS");
            } else if (p instanceof VCProject) {
                VCProject vc = (VCProject) p;
                String firstScript = queryFactory
                        .select(vcDetail.unitScript)
                        .from(vcDetail)
                        .where(vcDetail.vcProject.id.eq(vc.getId())
                                .and(vcDetail.isDeleted.isFalse()))
                        .orderBy(vcDetail.createdAt.asc())
                        .fetchFirst();
                dto.setScript(firstScript);
                dto.setProjectStatus(vc.getApiStatus().toString());
                dto.setProjectType("VC");
            } else if (p instanceof ConcatProject) {
                ConcatProject concat = (ConcatProject) p;
                String firstScript = queryFactory
                        .select(concatDetail.unitScript)
                        .from(concatDetail)
                        .where(concatDetail.concatProject.id.eq(concat.getId())
                                .and(concatDetail.isDeleted.isFalse()))
                        .orderBy(concatDetail.audioSeq.asc())
                        .fetchFirst();
                dto.setScript(firstScript);
                dto.setProjectType("CONCAT");
            }

            return dto;
        }).toList();

        return new PageImpl<>(dtos, pageable, totalCount);
    }
}


