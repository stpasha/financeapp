package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.*;
import net.microfin.financeapp.util.OperationType;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "notification_info", name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notification {
    @Id
    @Column(name = "notification_id", nullable = false)
    @EqualsAndHashCode.Include
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;
    @Column(name = "notification_description")
    private String notificationDescription;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "delivered")
    private boolean delivered;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
