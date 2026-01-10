package net.microfin.financeapp.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.jooq.tables.records.OutboxEventsRecord;
import net.microfin.financeapp.util.OperationStatus;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.microfin.financeapp.jooq.tables.OutboxEvents.OUTBOX_EVENTS;

@Repository
@RequiredArgsConstructor
public class OutboxEventWriteRepository {

    private final DSLContext dsl;


    public List<UUID> findOutboxEventIdByPendingStatus(int limit) {
        return dsl.select(OUTBOX_EVENTS.OUTBOX_ID)
                .from(OUTBOX_EVENTS)
                .where(
                        OUTBOX_EVENTS.STATUS.eq(OperationStatus.PENDING.name())
                                .or(OUTBOX_EVENTS.STATUS.isNull())
                ).orderBy(OUTBOX_EVENTS.CREATED_AT).limit(limit).fetch(OUTBOX_EVENTS.OUTBOX_ID);
    }

    public OutboxEventsRecord insert(OutboxEventsRecord record) {
        if (record.getOutboxId() == null) {
            record.setOutboxId(UuidCreator.getTimeOrderedEpoch());
        }
        if (record.getCreatedAt() == null) {
            record.setCreatedAt(LocalDateTime.now());
        }
        if (record.getUpdatedAt() == null) {
            record.setUpdatedAt(record.getCreatedAt());
        }

        return dsl.insertInto(OUTBOX_EVENTS)
                .set(record)
                .returning()
                .fetchSingle();
    }



    public List<UUID> findRetryableOutboxEvent(LocalDateTime now, int limit) {
        return dsl.select(OUTBOX_EVENTS.OUTBOX_ID)
                .from(OUTBOX_EVENTS)
                .where(OUTBOX_EVENTS.STATUS
                        .eq(OperationStatus.RETRYABLE.name())
                        .and(OUTBOX_EVENTS.RETRY_COUNT.lessThan(5))
                        .and(OUTBOX_EVENTS.NEXT_ATTEMPT_AT.lessThan(now)))
                .orderBy(OUTBOX_EVENTS.NEXT_ATTEMPT_AT)
                .limit(limit).fetch(OUTBOX_EVENTS.OUTBOX_ID);
    }

    public Optional<OutboxEventsRecord> findByIdForUpdateSkipLocked(UUID id) {
        return dsl.selectFrom(OUTBOX_EVENTS).where(OUTBOX_EVENTS.OUTBOX_ID.eq(id)).forUpdate().skipLocked().fetchOptional();
    }

    public OutboxEventsRecord updateStatus(UUID id, OperationStatus status) {
        return dsl.update(OUTBOX_EVENTS)
                .set(OUTBOX_EVENTS.STATUS, status.name())
                .set(OUTBOX_EVENTS.UPDATED_AT, LocalDateTime.now())
                .where(OUTBOX_EVENTS.OUTBOX_ID.eq(id))
                .and(
                        OUTBOX_EVENTS.STATUS.isNull()
                                .or(OUTBOX_EVENTS.STATUS.ne(status.name()))
                )
                .returning()
                .fetchOptional()
                .orElseGet(() ->
                        dsl.selectFrom(OUTBOX_EVENTS)
                                .where(OUTBOX_EVENTS.OUTBOX_ID.eq(id))
                                .fetchSingle()
                );
    }

    public void markSent(UUID eventId) {
        dsl.update(OUTBOX_EVENTS)
                .set(OUTBOX_EVENTS.LAST_ATTEMPT_AT, LocalDateTime.now())
                .set(OUTBOX_EVENTS.NEXT_ATTEMPT_AT, DSL.val((LocalDateTime) null))
                .set(OUTBOX_EVENTS.RETRY_COUNT, 0)
                .set(OUTBOX_EVENTS.STATUS, OperationStatus.SENT.name())
                .set(OUTBOX_EVENTS.UPDATED_AT, LocalDateTime.now())
                .where(OUTBOX_EVENTS.OUTBOX_ID.eq(eventId))
                .and(OUTBOX_EVENTS.STATUS.ne(OperationStatus.SENT.name()))
                .execute();
    }

    public void markFailed(UUID eventId, int retryCount) {
        dsl.update(OUTBOX_EVENTS)
                .set(OUTBOX_EVENTS.LAST_ATTEMPT_AT, LocalDateTime.now())
                .set(OUTBOX_EVENTS.NEXT_ATTEMPT_AT, DSL.val((LocalDateTime) null))
                .set(OUTBOX_EVENTS.RETRY_COUNT, retryCount)
                .set(OUTBOX_EVENTS.STATUS, OperationStatus.FAILED.name())
                .set(OUTBOX_EVENTS.UPDATED_AT, LocalDateTime.now())
                .where(OUTBOX_EVENTS.OUTBOX_ID.eq(eventId))
                .and(OUTBOX_EVENTS.STATUS.ne(OperationStatus.FAILED.name()))
                .execute();
    }

    public void markRetryable(UUID eventId, int retryCount) {
        dsl.update(OUTBOX_EVENTS)
                .set(OUTBOX_EVENTS.LAST_ATTEMPT_AT, LocalDateTime.now())
                .set(OUTBOX_EVENTS.RETRY_COUNT, retryCount)
                .set(OUTBOX_EVENTS.NEXT_ATTEMPT_AT,
                        LocalDateTime.now().plusMinutes(5L * retryCount))
                .set(OUTBOX_EVENTS.STATUS, OperationStatus.RETRYABLE.name())
                .set(OUTBOX_EVENTS.UPDATED_AT, LocalDateTime.now())
                .where(OUTBOX_EVENTS.OUTBOX_ID.eq(eventId))
                .execute();
    }

    //TODO MOVE TO TEST REPO

    public List<OutboxEventsRecord> findAll() {
        return dsl.selectFrom(OUTBOX_EVENTS).fetch();
    }

    public void deleteAll() {
        dsl.deleteFrom(OUTBOX_EVENTS).execute();
    }

}
