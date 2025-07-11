package net.microfin.financeapp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.microfin.financeapp.util.OperationType;

import java.time.LocalDateTime;

@Entity
@Table(schema = "notification_info", name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Notification {
    @Id
    @Column(name = "notification_id", nullable = false)
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_id_seq_gen")
    @SequenceGenerator(name="notification_id_seq_gen", sequenceName = "notification_id_seq", schema = "notification_info")
    private Integer id;
    @Column(name = "user_id", nullable = false)
    private Integer userId;
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
