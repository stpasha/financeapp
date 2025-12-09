package net.microfin.financeapp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "idempotency_records", schema = "account_info")
public class IdempotencyRecord {
    @Id
    @Column(name = "outbox_id")
    private UUID outboxId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "expire_at")
    private LocalDateTime expireAt;
}
