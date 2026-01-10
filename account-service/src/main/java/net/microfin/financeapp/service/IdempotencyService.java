package net.microfin.financeapp.service;

import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.jooq.tables.records.IdempotencyRecordsRecord;
import net.microfin.financeapp.repository.IdempotencyRecordWriteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class IdempotencyService {
    private final IdempotencyRecordWriteRepository idempotencyRecordRepository;
    private final Long ttlDays;

    public IdempotencyService(IdempotencyRecordWriteRepository idempotencyRecordRepository, @Value("${spring.scheduler.ttl.idempotency:30}") Long ttlDays) {
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.ttlDays = ttlDays;
    }

    public boolean tryStart(IdempotencyRecordsRecord idempotencyRecord) {
        try {
            idempotencyRecordRepository.insert(idempotencyRecord.getOutboxId(), idempotencyRecord.getCreatedAt(), idempotencyRecord.getExpireAt());
        } catch (DataIntegrityViolationException e) {
            log.warn("Idempotency record already exists for event {}", idempotencyRecord.getOutboxId());
            return false;
        }
        return true;
    }

    @Transactional
    public void deleteAllOutdated() {
        int deleted = idempotencyRecordRepository.deleteAllByTtl(LocalDateTime.now().minusDays(ttlDays));
        log.debug("Deleted {} old idempotency rows", deleted);
    }
}
