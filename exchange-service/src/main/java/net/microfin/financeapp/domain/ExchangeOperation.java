package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.microfin.financeapp.util.OperationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_operations", schema = "exchange_info")
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ExchangeOperation {
    @Id
    @Column(name = "operation_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exchange_operation_id_seq_gen")
    @SequenceGenerator(schema = "exchange_info",
            name = "exchange_operation_id_seq_gen",
            sequenceName = "exchange_operations_id_seq",
            allocationSize = 1
    )
    @EqualsAndHashCode.Include
    private Integer id;
    @Column(name = "operation_type", nullable = false)
    private String operationType;
    @Column(name = "target_account_id", nullable = false)
    private Integer targetAccountId;
    @Column(name = "source_account_id", nullable = false)
    private Integer sourceAccountId;
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OperationStatus status;
}
