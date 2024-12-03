package com.fourformance.tts_vc_web.service.common;


import com.amazonaws.services.s3.AmazonS3;
import com.fourformance.tts_vc_web.domain.entity.MemberAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.OutputAudioMeta;
import com.fourformance.tts_vc_web.repository.MemberAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.OutputAudioMetaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class SchedulerService {


    private final AmazonS3 amazonS3;
    private final S3Service s3service;
    private final OutputAudioMetaRepository outputAudioMetaRepository;
    private final MemberAudioMetaRepository memberAudioMetaRepository;

    @Transactional
    public void recheckDeleteAllS3Audio() {

        List<OutputAudioMeta> deleteMetaListOutput = outputAudioMetaRepository.findByIsDeletedTrue();
        List<MemberAudioMeta> deleteMetaListMember = memberAudioMetaRepository.findByIsDeletedTrue();

        for (OutputAudioMeta meta : deleteMetaListOutput) {
            s3service.deleteAudioOutput(meta.getId());
        }

        for (MemberAudioMeta meta : deleteMetaListMember) {
            s3service.deleteAudioOutput(meta.getId());
        }
    }
}