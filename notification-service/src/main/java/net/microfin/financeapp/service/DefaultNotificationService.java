package net.microfin.financeapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.NotificationDTO;
import net.microfin.financeapp.mapper.NotificationMapper;
import net.microfin.financeapp.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public Optional<NotificationDTO> saveNotification(NotificationDTO notificationDTO) {
        return Optional.of(notificationMapper.toDto(notificationRepository.save(notificationMapper.toEntity(notificationDTO))));
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
