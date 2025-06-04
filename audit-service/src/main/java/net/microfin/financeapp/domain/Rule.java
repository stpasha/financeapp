package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.*;

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
    @Column(nullable = false, name = "operation_type")
    private Integer operationType;
    @Column(nullable = false, name = "rule_condition")
    private String ruleCondition;
    @Column(nullable = false, name = "field")
    private String field;
}
