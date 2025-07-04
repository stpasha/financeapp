package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.*;
import net.microfin.financeapp.util.Currency;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Builder
@Table(name = "accounts", schema = "account_info")
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_id_seq_gen")
    @SequenceGenerator(schema = "account_info",
            name = "account_id_seq_gen",
            sequenceName = "account_id_seq",
            allocationSize = 1
    )
    @Column(name = "account_id", nullable = false)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "balance", nullable = false, scale = 2)
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", nullable = false)
    private Currency currencyCode;
    @Column(name = "is_active", nullable = false)
    private Boolean active;
}
