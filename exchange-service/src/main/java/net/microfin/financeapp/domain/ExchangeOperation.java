package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.*;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.checkerframework.checker.index.qual.Positive;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "exchange_operations", schema = "exchange_info")
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeOperation {
    @Id
    @Column(name = "operation_id")
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @EqualsAndHashCode.Include
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 32)
    private OperationType operationType;
    @Column(name = "target_account_id", nullable = false)
    private UUID targetAccountId;
    @Column(name = "source_account_id", nullable = false)
    private UUID sourceAccountId;
    @Positive
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OperationStatus status;
}
