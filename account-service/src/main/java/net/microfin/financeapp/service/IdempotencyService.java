package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.domain.IdempotencyRecord;
import net.microfin.financeapp.repository.IdempotencyRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private Long ttlDays;

    public IdempotencyService(IdempotencyRecordRepository idempotencyRecordRepository,@Value("${spring.scheduler.ttl.idempotency:30}") Long ttlDays) {
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.ttlDays = ttlDays;
    }

    public boolean tryStart(IdempotencyRecord idempotencyRecord) {
        try {
            idempotencyRecordRepository.save(idempotencyRecord);
        } catch (DataIntegrityViolationException e) {
            log.warn("Idempotency record already exists for event {}", idempotencyRecord.getOutboxId());
            return false;
        }
        return true;
    }

    @Transactional
    public void deleteAllOutdated() {
        idempotencyRecordRepository.deleteAllByTtl(LocalDateTime.now().minusDays(ttlDays));
    }
}
