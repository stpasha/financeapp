package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.*;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events", schema = "account_info")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    @Column(name = "aggregate_type")
    private String aggregateType;// тип агрегата, например "USER"
    @Column(name = "aggregate_id")
    private Integer aggregateId;        // ID сущности
    @Column(name = "operation_type")
    private OperationType operationType;        // тип события, например "USER_CREATED"
    @Lob
    @Column(name = "payload")
    private String payload;          // JSON-данные события
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OperationStatus status;     // PENDING, SENT, FAILED
    @Column(name = "retry_count")
    private int retryCount;          // количество попыток
    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;
    @Column(name = "next_attempt_at")
    private LocalDateTime nextAttemptAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        status = OperationStatus.PENDING;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
