package net.microfin.financeapp.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

import static net.microfin.financeapp.jooq.tables.IdempotencyRecords.IDEMPOTENCY_RECORDS;

@Repository
@RequiredArgsConstructor
public class IdempotencyRecordWriteRepository {

    private final DSLContext dsl;

    public void insert(UUID outboxId, LocalDateTime createdAt, LocalDateTime expireAt) {
        dsl.insertInto(IDEMPOTENCY_RECORDS)
                .set(IDEMPOTENCY_RECORDS.OUTBOX_ID, outboxId)
                .set(IDEMPOTENCY_RECORDS.CREATED_AT, createdAt)
                .set(IDEMPOTENCY_RECORDS.EXPIRE_AT, expireAt)
                .execute();
    }

    public int deleteAllByTtl(LocalDateTime ttl) {
        return dsl.deleteFrom(IDEMPOTENCY_RECORDS).where(IDEMPOTENCY_RECORDS.EXPIRE_AT.lessThan(ttl)).execute();
    }
}
