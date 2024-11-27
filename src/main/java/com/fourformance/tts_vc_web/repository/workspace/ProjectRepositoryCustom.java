package com.fourformance.tts_vc_web.repository.workspace;

import com.fourformance.tts_vc_web.dto.workspace.ProjectListDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {
    List<ProjectListDto> findProjectsBySearchCriteria(Long memberId, String keyword);

    Page<ProjectListDto> findProjectsBySearchCriteria(Long memberId, String keyword, Pageable pageable);
}
