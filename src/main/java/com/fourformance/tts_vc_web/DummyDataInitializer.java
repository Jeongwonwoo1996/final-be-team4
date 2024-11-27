package com.fourformance.tts_vc_web;

import com.fourformance.tts_vc_web.common.constant.APIStatusConst;
import com.fourformance.tts_vc_web.common.constant.AudioType;
import com.fourformance.tts_vc_web.common.constant.ProjectType;
import com.fourformance.tts_vc_web.domain.entity.ConcatDetail;
import com.fourformance.tts_vc_web.domain.entity.ConcatProject;
import com.fourformance.tts_vc_web.domain.entity.Member;
import com.fourformance.tts_vc_web.domain.entity.MemberAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.OutputAudioMeta;
import com.fourformance.tts_vc_web.domain.entity.TTSDetail;
import com.fourformance.tts_vc_web.domain.entity.TTSProject;
import com.fourformance.tts_vc_web.domain.entity.VCDetail;
import com.fourformance.tts_vc_web.domain.entity.VCProject;
import com.fourformance.tts_vc_web.domain.entity.VoiceStyle;
import com.fourformance.tts_vc_web.repository.ConcatDetailRepository;
import com.fourformance.tts_vc_web.repository.ConcatProjectRepository;
import com.fourformance.tts_vc_web.repository.MemberAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.MemberRepository;
import com.fourformance.tts_vc_web.repository.OutputAudioMetaRepository;
import com.fourformance.tts_vc_web.repository.ProjectRepository;
import com.fourformance.tts_vc_web.repository.TTSDetailRepository;
import com.fourformance.tts_vc_web.repository.TTSProjectRepository;
import com.fourformance.tts_vc_web.repository.VCDetailRepository;
import com.fourformance.tts_vc_web.repository.VCProjectRepository;
import com.fourformance.tts_vc_web.repository.VoiceStyleRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DummyDataInitializer {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final TTSProjectRepository ttsProjectRepository;
    private final TTSDetailRepository ttsDetailRepository;
    private final VCProjectRepository vcProjectRepository;
    private final VCDetailRepository vcDetailRepository;
    private final ConcatProjectRepository concatProjectRepository;
    private final ConcatDetailRepository concatDetailRepository;
    private final VoiceStyleRepository voiceStyleRepository;
    private final OutputAudioMetaRepository outputAudioMetaRepository;
    private final MemberAudioMetaRepository memberAudioMetaRepository;

    @Bean
    public ApplicationRunner initializeDummyData() {
        return args -> initializeData();
    }

    private void initializeData() {
        // VoiceStyle 데이터 확인
        List<VoiceStyle> voiceStyles = voiceStyleRepository.findAll();
        if (voiceStyles.isEmpty()) {
            throw new IllegalStateException("VoiceStyle 데이터가 없습니다. data.sql을 확인하세요.");
        }

        // 멤버 생성
        List<Member> members = List.of(
                Member.createMember("user1@example.com", "password123", "User One", 0,
                        LocalDateTime.of(1990, 1, 1, 0, 0), "123-456-7890"),
                Member.createMember("user2@example.com", "password456", "User Two", 1,
                        LocalDateTime.of(1995, 5, 15, 0, 0), "234-567-8901"),
                Member.createMember("user3@example.com", "password789", "User Three", 0,
                        LocalDateTime.of(1985, 3, 20, 0, 0), "345-678-9012")
        );
        memberRepository.saveAll(members);

        Member firstMember = members.get(0);

        // TTS 프로젝트 및 디테일 생성
        for (int i = 1; i <= 10; i++) { // 멤버 1에게 10개의 TTS 프로젝트 생성
            VoiceStyle voiceStyle = voiceStyles.get((i - 1) % voiceStyles.size());
            TTSProject ttsProject = TTSProject.createTTSProject(
                    firstMember,
                    "티티에스 프로젝트 " + i,
                    voiceStyle,
                    "This is the full script for TTS project " + i,
                    1.0f + i * 0.1f,
                    i % 2 == 0 ? 0.5f : -0.3f,
                    i % 3 == 0 ? 0.2f : 0.0f
            );

            // 다양한 상태 추가
            ttsProject.updateAPIStatus(APIStatusConst.values()[i % APIStatusConst.values().length]);
            ttsProjectRepository.save(ttsProject);

            for (int j = 1; j <= 5; j++) {
                TTSDetail ttsDetail = TTSDetail.createTTSDetail(
                        ttsProject,
                        "Unit script " + j, // 기본 스크립트
                        j
                );

                // 짝수 디테일만 업데이트
                if (j % 2 == 0) {
                    ttsDetail.updateTTSDetail(
                            voiceStyle,
                            "Updated script " + j + " of TTS Project " + i,
                            1.0f + j * 0.1f,
                            j % 2 == 0 ? 0.3f : -0.2f,
                            j % 3 == 0 ? 0.5f : 0.1f,
                            j,
                            false
                    );
                }

                ttsDetailRepository.save(ttsDetail);

                // OutputAudioMeta 생성
                OutputAudioMeta outputAudioMeta = OutputAudioMeta.createOutputAudioMeta(
                        "bucket/tts_project_" + i + "_detail_" + j,
                        ttsDetail,
                        null,
                        null,
                        ProjectType.TTS,
                        "https://audio.example.com/tts_project_" + i + "_detail_" + j + ".wav"
                );
                outputAudioMetaRepository.save(outputAudioMeta);
            }
        }

        // VC 프로젝트 및 디테일 생성
        for (int i = 1; i <= 7; i++) { // 멤버 1에게 7개의 VC 프로젝트 생성
            VCProject vcProject = VCProject.createVCProject(
                    firstMember,
                    "브이씨 프로젝트 " + i
            );

            // 다양한 상태 추가
            vcProject.updateAPIStatus(APIStatusConst.values()[i % APIStatusConst.values().length]);
            vcProject.updateTrgVoiceId("Voice_" + i);
            vcProjectRepository.save(vcProject);

            for (int j = 1; j <= 4; j++) {
                VCDetail vcDetail = VCDetail.createVCDetail(
                        vcProject,
                        null
                );

                // 짝수 디테일만 업데이트
                if (j % 2 == 0) {
                    vcDetail.updateDetails(j % 2 == 0, "Updated script " + j + " for VC Project " + i);
                }

                vcDetailRepository.save(vcDetail);

                // OutputAudioMeta 생성
                OutputAudioMeta outputAudioMeta = OutputAudioMeta.createOutputAudioMeta(
                        "bucket/vc_project_" + i + "_detail_" + j,
                        null,
                        vcDetail,
                        null,
                        ProjectType.VC,
                        "https://audio.example.com/vc_project_" + i + "_detail_" + j + ".wav"
                );
                outputAudioMetaRepository.save(outputAudioMeta);
            }
        }

        // Concat 프로젝트 및 디테일 생성
        for (int i = 1; i <= 6; i++) { // 멤버 1에게 6개의 Concat 프로젝트 생성
            ConcatProject concatProject = ConcatProject.createConcatProject(
                    firstMember,
                    "컨캣 프로젝트 " + i
            );

            concatProject.updateConcatProject("Updated Concat Project " + i, 0.1f * i, 0.2f * i);
            concatProjectRepository.save(concatProject);

            for (int j = 1; j <= 5; j++) {
                MemberAudioMeta memberAudioMeta = MemberAudioMeta.createMemberAudioMeta(
                        firstMember,
                        "bucket/concat_project_" + i + "_detail_" + j,
                        "https://audio.example.com/concat_project_" + i + "_detail_" + j + ".wav",
                        AudioType.CONCAT
                );
                memberAudioMetaRepository.save(memberAudioMeta);

                ConcatDetail concatDetail = ConcatDetail.createConcatDetail(
                        concatProject,
                        j,
                        true,
                        "Concat unit script " + j + " of Concat Project " + i, // 기본 스크립트
                        0.2f * j,
                        memberAudioMeta
                );

                // 짝수 디테일만 업데이트
                if (j % 2 == 0) {
                    concatDetail.updateDetails(j, true, "Updated concat script " + j, 0.2f * j);
                }

                concatDetailRepository.save(concatDetail);
            }

            // OutputAudioMeta 생성
            OutputAudioMeta outputAudioMeta = OutputAudioMeta.createOutputAudioMeta(
                    "bucket/concat_project_" + i,
                    null,
                    null,
                    concatProject,
                    ProjectType.CONCAT,
                    "https://audio.example.com/concat_project_" + i + ".wav"
            );
            outputAudioMetaRepository.save(outputAudioMeta);
        }
    }
}