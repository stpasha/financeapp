package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Builder
@Table(name = "accounts", schema = "account_info")
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
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;
    @Column(name = "is_active", nullable = false)
    private Boolean active;
}
