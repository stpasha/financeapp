package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@Table(name = "users", schema = "account_info")
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(
        name = "id",
        column = @Column(name = "user_id", nullable = false, updatable = false)
)
public class User extends BaseEntity {
    @Column(name = "keycloak_id")
    private UUID keycloakId;
    @Column(name = "user_name", nullable = false, unique = true, updatable = false)
    private String username;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column
    private LocalDate dob;
    @OneToMany(mappedBy = "user")
    private List<Account> accounts;
    @Column(name = "is_enabled", nullable = false)
    private Boolean enabled;
}
