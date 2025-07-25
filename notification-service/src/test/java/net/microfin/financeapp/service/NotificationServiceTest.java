package net.microfin.financeapp.service;

import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.FinanceAppTest;
import net.microfin.financeapp.domain.Notification;
import net.microfin.financeapp.dto.NotificationDTO;
import net.microfin.financeapp.repository.NotificationRepository;
import net.microfin.financeapp.util.OperationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@FinanceAppTest
public class NotificationServiceTest extends AbstractTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldSaveNotificationSuccessfully() {
        NotificationDTO dto = NotificationDTO.builder()
                .userId(1001)
                .operationType(OperationType.EXCHANGE.name())
                .notificationDescription("Description")
                .createdAt(LocalDateTime.now())
                .delivered(false)
                .build();

        var saved = notificationService.saveNotification(dto);

        assertThat(saved).isPresent();
        var result = saved.get();

        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo(dto.getUserId());
        assertThat(result.getOperationType()).isEqualTo(dto.getOperationType());
        assertThat(result.getNotificationDescription()).isEqualTo(dto.getNotificationDescription());
        assertThat(result.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(result.isDelivered()).isFalse();
    }

    @Test
    void shouldReceiveUndeliveredNotificationsAndMarkThemDelivered() {
        var now = LocalDateTime.now();

        var n1 = Notification.builder()
                .userId(1002)
                .operationType(OperationType.CASH_DEPOSIT)
                .notificationDescription("Message 1")
                .createdAt(now.minusMinutes(5))
                .delivered(false)
                .build();

        var n2 = Notification.builder()
                .userId(1002)
                .operationType(OperationType.TRANSFER)
                .notificationDescription("Message 2")
                .createdAt(now.minusMinutes(3))
                .delivered(false)
                .build();

        var alreadyDelivered = Notification.builder()
                .userId(1002)
                .operationType(OperationType.EXCHANGE)
                .notificationDescription("Old message")
                .createdAt(now.minusHours(1))
                .delivered(true)
                .build();

        notificationRepository.saveAll(List.of(n1, n2, alreadyDelivered));

        List<NotificationDTO> received = notificationService.receiveNotification(1002);

        assertThat(received).hasSize(2);
        assertThat(received).allMatch(NotificationDTO::isDelivered);
        assertThat(received).extracting(NotificationDTO::getNotificationDescription)
                .containsExactlyInAnyOrder("Message 1", "Message 2");

        var stillUndelivered = notificationRepository.findNotificationByUserIdAndDelivered(1002, false);
        assertThat(stillUndelivered).isEmpty();
    }
}
