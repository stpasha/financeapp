package net.microfin.financeapp.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table(name = "users", schema = "account_info")
@EqualsAndHashCode
public class User implements UserDetails {
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
    @EqualsAndHashCode.Include
    @Column(name = "user_name", nullable = false, unique = true)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column
    private LocalDateTime dob;
    @OneToMany(mappedBy = "user")
    private List<Account> accounts;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
