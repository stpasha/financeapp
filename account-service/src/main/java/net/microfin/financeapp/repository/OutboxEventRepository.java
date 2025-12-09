package net.microfin.financeapp.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import net.microfin.financeapp.domain.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository <OutboxEvent, UUID> {
    @Query("""
    SELECT e FROM OutboxEvent e
    WHERE e.status IN ('PENDING') OR e.status IS NULL
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(value = { @QueryHint(name = "jakarta.persistence.lock.timeout", value = "2000")})
    List<OutboxEvent> findOutboxEventByPendingStatus(Pageable pageable);
    @Query(value = "SELECT e FROM OutboxEvent e WHERE e.status = 'RETRYABLE' and e.retry_count < 5 and e.next_attempt_at < :now")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(value = { @QueryHint(name = "jakarta.persistence.lock.timeout", value = "2000")})
    List<OutboxEvent> findRetryableOutboxEvent(@Param("now") LocalDateTime now, Pageable pageable);

    @Query(
            value = "SELECT * FROM account_info.outbox_events e WHERE e.id = :id FOR UPDATE SKIP LOCKED",
            nativeQuery = true
    )
    Optional<OutboxEvent> findByIdForUpdate(@Param("id") UUID id);
}
