package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_operations", schema = "cash_info")
@EqualsAndHashCode
@Getter
@Setter
@Builder
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
    @Column(name = "operation_type", nullable = false)
    private Integer operationType;
    @Column(name = "account_id", nullable = false)
    private Integer accountId;
    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "status_id", nullable = false)
    private Integer statusId;
}
