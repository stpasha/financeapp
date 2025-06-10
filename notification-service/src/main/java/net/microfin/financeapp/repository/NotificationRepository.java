package net.microfin.financeapp.repository;

import net.microfin.financeapp.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findNotificationByUserIdAndDelivered(Integer userId, boolean delivered);
}
