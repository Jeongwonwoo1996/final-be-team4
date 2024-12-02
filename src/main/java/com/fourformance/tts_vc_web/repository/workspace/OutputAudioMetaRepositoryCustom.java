package com.fourformance.tts_vc_web.repository.workspace;

import com.fourformance.tts_vc_web.dto.workspace.ExportListDto;
import com.fourformance.tts_vc_web.dto.workspace.ExportWithDownloadLinkDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OutputAudioMetaRepositoryCustom {

    List<ExportListDto> findExportHistoryBySearchCriteria(Long memberId, String keyword);

    //    Page<ExportListDto> findExportHistoryBySearchCriteria(Long memberId, String keyword, Pageable pageable);
    Page<ExportWithDownloadLinkDto> findExportHistoryBySearchCriteria(Long memberId, String keyword, Pageable pageable);

}
