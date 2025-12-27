package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.*;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationType;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "rules", schema = "rule_info")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rule {
    @Id
    @Column(nullable = false, name = "rule_id")
    @EqualsAndHashCode.Include
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "operation_type")
    private OperationType operationType;
    @Column(nullable = false, name = "min_amount", precision = 19, scale = 2)
    private BigDecimal minAmount;
    @Column(nullable = false, name = "max_amount", precision = 19, scale = 2)
    private BigDecimal maxAmount;
    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", nullable = false, length = 3)
    private Currency currencyCode;
}
