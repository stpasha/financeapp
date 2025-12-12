package net.microfin.financeapp.repository;

import net.microfin.financeapp.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository <OutboxEvent, UUID> {
    @Query(
            value = """
        SELECT id
        FROM account_info.outbox_events 
        WHERE status = 'PENDING' OR status IS NULL
        ORDER BY created_at
        LIMIT :limit
    """,
            nativeQuery = true
    )
    List<UUID> findOutboxEventIdByPendingStatus(@Param("limit") int limit);
    @Query(
            value = """
        SELECT id 
        FROM account_info.outbox_events 
        WHERE status = 'RETRYABLE'
          AND retry_count < 5
          AND next_attempt_at < :now
        ORDER BY next_attempt_at
        LIMIT :limit
    """,
            nativeQuery = true
    )
    List<UUID> findRetryableOutboxEvent(@Param("now") LocalDateTime now, @Param("limit") int limit);

    @Query(
            value = "SELECT * FROM account_info.outbox_events e WHERE e.id = :id FOR UPDATE SKIP LOCKED",
            nativeQuery = true
    )
    Optional<OutboxEvent> findByIdForUpdateSkipLocked(@Param("id") UUID id);
}
