package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@Table(name = "users", schema = "account_info")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq_gen")
    @SequenceGenerator(schema = "account_info",
            name = "user_id_seq_gen",
            sequenceName = "user_id_seq",
            allocationSize = 1
    )
    @EqualsAndHashCode.Include
    @Column(name = "user_id")
    private Integer id;
    @Column(name = "keycloak_id")
    private UUID keycloakId;
    @EqualsAndHashCode.Include
    @Column(name = "user_name", nullable = false, unique = true, updatable = false)
    private String username;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column
    private LocalDateTime dob;
    @OneToMany(mappedBy = "user")
    private List<Account> accounts;
    @Column(name = "is_enabled", nullable = false)
    private Boolean enabled;
}
