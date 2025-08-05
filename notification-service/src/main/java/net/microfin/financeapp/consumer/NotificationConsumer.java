package net.microfin.financeapp.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.NotificationDTO;
import net.microfin.financeapp.service.NotificationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RetryableTopic(attempts = "5",
            backoff = @Backoff(delay = 1_000, multiplier = 2, maxDelay = 8000),
            dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR,
            dltTopicSuffix = "-for-analysis-dlt")
    @KafkaListener(topics = "input-notification")
    public void listen(ConsumerRecord<Integer, NotificationDTO> consumerRecord, Acknowledgment ack) {
        notificationService.saveNotification(consumerRecord.value());
        ack.acknowledge();
    }

    @KafkaListener(topics = "input-notification-for-analysis-dlt")
    public void handleDltMessage(ConsumerRecord<Integer, String> record) {
       log.error("Message landed in DLT {}", record.value());
    }
}
