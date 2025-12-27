package net.microfin.financeapp.repository;

import net.microfin.financeapp.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findNotificationByUserIdAndDelivered(UUID userId, boolean delivered);
}
