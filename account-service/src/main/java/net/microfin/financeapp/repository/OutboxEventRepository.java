package net.microfin.financeapp.repository;

import net.microfin.financeapp.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository <OutboxEvent, UUID> {
    @Query(value = "SELECT e.* FROM account_info.outbox_events e WHERE e.status = 'PENDING' OR e.status = 'RETRYABLE'", nativeQuery = true)
    List<OutboxEvent> findOutboxEventByPendingStatus();
    @Query(value = "SELECT e.* FROM account_info.outbox_events e WHERE e.status = 'FAILED' and e.retry_count < 5 and e.next_attempt_at < :now",
    nativeQuery = true)
    List<OutboxEvent> findRetryableOutboxEvent(@Param("now") LocalDateTime now);
}
