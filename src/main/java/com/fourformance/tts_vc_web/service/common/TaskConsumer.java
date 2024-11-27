package com.fourformance.tts_vc_web.service.common;

import com.fourformance.tts_vc_web.common.config.TaskConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TaskConsumer {

//    private static final Logger log = LoggerFactory.getLogger(TaskConsumer.class);
//
//    @RabbitListener(queues = "popomance.queue")
//    public void reciveMessage(final Message message) {
//        String msgBody = new String(message.getBody());
//        log.info(message.toString());
//    }

    @RabbitListener(queues = TaskConfig.TTS_QUEUE)
    public void handleTTSTask(String message) {
        System.out.println("TTS audio task : " + message);
        // 처리 로직 구현
    }

    @RabbitListener(queues = TaskConfig.VC_QUEUE)
    public void handleVCTask(String message) {
        System.out.println("VC audio task : " + message);
        // 처리 로직 구현
    }

    @RabbitListener(queues = TaskConfig.CONCAT_QUEUE)
    public void handleConcatTask(String message) {
        System.out.println("Concat audio task : " + message);
        // 처리 로직 구현
    }
}
