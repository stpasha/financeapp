package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.*;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_operations", schema = "cash_info")
@EqualsAndHashCode
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashOperation {
    @Id
    @Column(name = "operation_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cash_operation_id_seq_gen")
    @SequenceGenerator(schema = "cash_info",
            name = "cash_operation_id_seq_gen",
            sequenceName = "cash_operations_id_seq",
            allocationSize = 1
    )
    @EqualsAndHashCode.Include
    private Integer id;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;
    @Column(name = "account_id")
    private Integer accountId;
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OperationStatus status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
