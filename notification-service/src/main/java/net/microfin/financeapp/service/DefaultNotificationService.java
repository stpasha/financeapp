package net.microfin.financeapp.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.transaction.annotation.Transactional;import net.microfin.financeapp.dto.NotificationDTO;
import net.microfin.financeapp.mapper.NotificationMapper;
import net.microfin.financeapp.producer.UserNotificationProducer;
import net.microfin.financeapp.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service

public class DefaultNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserNotificationProducer userNotificationProducer;
    private final MeterRegistry meterRegistry;
    private final boolean testPrometheus;

    public DefaultNotificationService(NotificationRepository notificationRepository,
                                      NotificationMapper notificationMapper,
                                      UserNotificationProducer userNotificationProducer, MeterRegistry meterRegistry,
                                      @Value("${testPrometheus:false}") boolean testPrometheus) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.userNotificationProducer = userNotificationProducer;
        this.meterRegistry = meterRegistry;
        this.testPrometheus = testPrometheus;
    }

    @Override
    @Transactional
    public Optional<NotificationDTO> saveNotification(NotificationDTO notificationDTO) {
        try {
            NotificationDTO dto = notificationMapper.toDto(notificationRepository.save(notificationMapper.toEntity(notificationDTO)));
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    userNotificationProducer.produceUserNotification(dto);
                }
            });
            if (testPrometheus && ThreadLocalRandom.current().nextInt(10) > 5) {
                throw new RuntimeException("internal error");
            }

            return Optional.of(dto);
        } catch (Exception e) {
            meterRegistry.counter("financeapp_failed_notification_total",
                    "userId", String.valueOf(notificationDTO.getUserId())
            ).increment();
            throw e;
        }
    }

    @Override
    @Transactional
    public List<NotificationDTO> receiveNotification(UUID userId) {
        return notificationRepository.findNotificationByUserIdAndDelivered(userId, false).stream().map(notification -> {
            notification.setDelivered(true);
            return notificationMapper.toDto(notification);
        }).toList();
    }
}
