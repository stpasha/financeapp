package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.*;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationType;

import java.math.BigDecimal;

@Entity
@Table(name = "rules", schema = "rule_info")
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Rule {
    @Id
    @Column(nullable = false, name = "rule_id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rule_id_seq_gen")
    @SequenceGenerator(schema = "account_info",
            name = "rule_id_seq_gen",
            sequenceName = "rule_id_seq",
            allocationSize = 1
    )
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "operation_type")
    private OperationType operationType;
    @Column(nullable = false, name = "min_amount")
    private BigDecimal minAmount;
    @Column(nullable = false, name = "max_amount")
    private BigDecimal maxAmount;
    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", nullable = false, length = 3)
    private Currency currencyCode;
}
