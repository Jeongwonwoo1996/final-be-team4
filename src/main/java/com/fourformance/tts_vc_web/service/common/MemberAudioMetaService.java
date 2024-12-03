package com.fourformance.tts_vc_web.service.common;

import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.domain.entity.MemberAudioMeta;
import com.fourformance.tts_vc_web.repository.MemberAudioMetaRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAudioMetaService {

    private final MemberAudioMetaRepository memberAudioMetaRepository;
    private final S3Service s3Service;

    @Transactional
    public void deleteOldVcAudios() {
        // 한 달 이전의 날짜
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        // 조건에 맞는 오디오 조회
        List<MemberAudioMeta> oldAudios = memberAudioMetaRepository.findOldVcAudiosToDelete(
                List.of(AudioType.VC_TRG, AudioType.VC_SRC), oneMonthAgo);

        // 버킷에서 삭제 처리
        for (MemberAudioMeta audioMeta : oldAudios) {
            // 버킷에서 오디오 파일 삭제
            s3Service.deleteAudioMember(audioMeta.getId());

            // db is_deleted 컬럼 업데이트
            audioMeta.delete();
            memberAudioMetaRepository.save(audioMeta);
        }
    }

    @Transactional
    public void deleteOldVcAudiosTest() {

        // 조건에 맞는 오디오 조회
        List<MemberAudioMeta> oldAudios = memberAudioMetaRepository.findConcatAudioMeta();

        System.out.println(
                "지정된 시간입니다!!!!! concat 데이터 전부 날리겠습니다!!!! @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        // 버킷에서 삭제 처리
        for (MemberAudioMeta audioMeta : oldAudios) {

            System.out.println(audioMeta.toString());

            // 버킷에서 오디오 파일 삭제
            s3Service.deleteAudioMember(audioMeta.getId());

            // db is_deleted 컬럼 업데이트
            audioMeta.delete();
            memberAudioMetaRepository.save(audioMeta);
        }
    }
}
