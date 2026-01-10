package net.microfin.financeapp.repository;

import net.microfin.financeapp.domain.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface IdempotencyRecordLegacyRepository extends JpaRepository<IdempotencyRecord, UUID> {
    @Query(value = "DELETE FROM IdempotencyRecord ir WHERE ir.expireAt < :ttl")
    @Modifying
    int deleteAllByTtl(@Param("ttl") LocalDateTime ttl);
}
