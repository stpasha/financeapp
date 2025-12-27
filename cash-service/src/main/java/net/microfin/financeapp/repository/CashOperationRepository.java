package net.microfin.financeapp.repository;

import net.microfin.financeapp.domain.CashOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CashOperationRepository extends JpaRepository<CashOperation, UUID> {
}
