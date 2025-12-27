package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfer_operations", schema = "transfer_info")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TransferOperation {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "operation_id")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "target_account_id", nullable = false)
    private UUID targetAccountId;

    @Column(name = "source_account_id", nullable = false)
    private UUID sourceAccountId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    @Positive
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 32)
    private OperationType operationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OperationStatus status;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

