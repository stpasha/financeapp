package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.*;
import net.microfin.financeapp.util.Currency;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Builder
@Table(name = "accounts", schema = "account_info")
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(
        name = "id",
        column = @Column(name = "account_id", nullable = false, updatable = false)
)
public class Account extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", nullable = false)
    private Currency currencyCode;
    @Column(name = "is_active", nullable = false)
    private boolean active;
}
