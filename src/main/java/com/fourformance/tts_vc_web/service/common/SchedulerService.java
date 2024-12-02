package com.fourformance.tts_vc_web.service.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.fourformance.tts_vc_web.domain.entity.OutputAudioMeta;
import com.fourformance.tts_vc_web.repository.OutputAudioMetaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class SchedulerService {


    private final AmazonS3 amazonS3;

    private final OutputAudioMetaRepository outputAudioMetaRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    public SchedulerService(AmazonS3 amazonS3, OutputAudioMetaRepository outputAudioMetaRepository) {
        this.amazonS3 = amazonS3;
        this.outputAudioMetaRepository = outputAudioMetaRepository;
    }

    public void RecheckdeleteAllS3Audio() {

//        // 1. 파일경로 생성
//        List<OutputAudioMeta> deleteMetaList = outputAudioMetaRepository.findByIsDeletedTrue();
//        // 2. 그 id의 isDeleted가 true면 그 id 의 파일이름으로 객체를 삭제한다.
////
//        for(OutputAudioMeta meta : deleteMetaList) {
//            String filePath = meta.getBucketRoute();
//
//            try {
//                // 파일존개 여부 확인.
//                if (amazonS3.doesObjectExist(bucket, filePath)) {
//                    amazonS3.deleteObject(bucket, filePath);
//                    System.out.println("Deleted S3 filePath = " + filePath);
//                } else {
//                    System.out.println("S3 file not found (already deleted): " + filePath);
//                }
//            }catch (AmazonS3Exception e) {
//                System.out.println("Failed to delete S3 file: " + filePath);
//            }
//
//        }

        List<OutputAudioMeta> outputAudioMetaList = outputAudioMetaRepository.findByIsDeletedTrue();

        int threadPoolSize = 10; // 사용할 스레드 갯수 일반적으로 10~20개의 스레드로 설정
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

        // 작업시작
        for(OutputAudioMeta outputAudioMeta : outputAudioMetaList) {
            executorService.submit(() -> { // 각 파일 삭제 작업을 스레드 풀에 제출
                String filePath = outputAudioMeta.getBucketRoute();
                try{
                    // S3파일 존재 여부 확인 후 삭제
                    if(amazonS3.doesObjectExist(bucket, filePath)) {
                        amazonS3.deleteObject(bucket, filePath);
                    } else {

                    }
                }catch (AmazonS3Exception e) {
                    e.printStackTrace(); // 임시
                }
            });
        }

        // 스레드풀 셧다운
        executorService.shutdown();
        try{
            // 스레드풀이 모든 작업을 완료할 때까지 대기
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) { // 기다리는 텀 초단위 60초임
                System.err.println("Timeout while waiting for S3 deletion tasks to complete.");
            }
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();

        }

    }

}
