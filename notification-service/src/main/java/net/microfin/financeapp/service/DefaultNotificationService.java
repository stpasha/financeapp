package net.microfin.financeapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.NotificationDTO;
import net.microfin.financeapp.mapper.NotificationMapper;
import net.microfin.financeapp.producer.UserNotificationProducer;
import net.microfin.financeapp.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserNotificationProducer userNotificationProducer;

    @Override
    @Transactional
    public Optional<NotificationDTO> saveNotification(NotificationDTO notificationDTO) {
        NotificationDTO dto = notificationMapper.toDto(notificationRepository.save(notificationMapper.toEntity(notificationDTO)));
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                userNotificationProducer.produceUserNotification(dto);
            }
        });

        return Optional.of(dto);
    }

    @Override
    @Transactional
    public List<NotificationDTO> receiveNotification(Integer userId) {
        return notificationRepository.findNotificationByUserIdAndDelivered(userId, false).stream().map(notification -> {
            notification.setDelivered(true);
            return notificationMapper.toDto(notification);
        }).toList();
    }
}
