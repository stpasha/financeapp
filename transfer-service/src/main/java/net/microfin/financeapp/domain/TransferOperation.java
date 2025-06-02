package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.*;
import net.microfin.financeapp.util.OperationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_operations", schema = "transfer_info")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TransferOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transfer_operation_id_seq_gen")
    @SequenceGenerator(
            name = "transfer_operation_id_seq_gen",
            schema = "transfer_info",
            sequenceName = "transfer_operations_id_seq",
            allocationSize = 1
    )
    @Column(name = "operation_id")
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "target_account_id", nullable = false)
    private Integer targetAccountId;

    @Column(name = "source_account_id", nullable = false)
    private Integer sourceAccountId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "operation_type", nullable = false)
    private String operationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OperationStatus status;
}

