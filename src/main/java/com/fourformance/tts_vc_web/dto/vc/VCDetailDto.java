package com.fourformance.tts_vc_web.dto.vc;

import com.fourformance.tts_vc_web.domain.entity.VCDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VCDetailDto {

    private Long id; // 상세 정보 ID
    private Long projectId; // 프로젝트 ID
    private Long memberAudioMetaId; // MemberAudioMeta ID
    private Boolean isChecked; // 체크 여부
    private String unitScript; // 단위 스크립트
    private Boolean isDeleted; // 삭제 여부
    private String localFileName; // 추가된 멀티파트 파일

    private static ModelMapper modelMapper = new ModelMapper();

    // VCDetailDto -> VCDetail 매핑 메서드
    public VCDetail createVCDetail() {
        modelMapper.getConfiguration()
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true);
        return modelMapper.map(this, VCDetail.class);
    }

    // VCDetail -> VCDetailDto 매핑 메서드
    public static VCDetailDto createVCDetailDto(VCDetail vcDetail) {
        return modelMapper.map(vcDetail, VCDetailDto.class);
    }
}
