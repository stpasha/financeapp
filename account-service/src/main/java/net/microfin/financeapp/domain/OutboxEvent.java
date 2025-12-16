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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverride(
        name = "id",
        column = @Column(name = "outbox_id", nullable = false, updatable = false)
)
public class OutboxEvent extends BaseEntity {
    @Column(name = "aggregate_type")
    private String aggregateType;// тип агрегата, например "USER"
    @Column(name = "account_id")
    private UUID accountId;        // ID ЛС
    @Column(name = "aggregate_id")
    private UUID aggregateId;        // ID сущности
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;        // тип события, например "USER_CREATED"
    @Lob
    @Column(name = "payload")
    private String payload;          // JSON-данные события
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OperationStatus status;     // PENDING, SENT, FAILED
    @Column(name = "retry_count")
    private int retryCount;          // количество попыток
    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;
    @Column(name = "next_attempt_at")
    private LocalDateTime nextAttemptAt;

    @PrePersist
    private void initStatus() {
        if (status == null) {
            status = OperationStatus.PENDING;
        }
    }
}
