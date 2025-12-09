package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.domain.IdempotencyRecord;
import net.microfin.financeapp.repository.IdempotencyRecordRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {
    private final IdempotencyRecordRepository idempotencyRecordRepository;

    public boolean tryStart(IdempotencyRecord idempotencyRecord) {
        try {
            idempotencyRecordRepository.save(idempotencyRecord);
        } catch (DataIntegrityViolationException e) {
            log.error("Idempotent exception {} exists {}", idempotencyRecord.getOutboxId(), e);
            return false;
        }
        return true;
    }
}
