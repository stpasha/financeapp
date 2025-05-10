package net.microfin.financeapp.repository;

import net.microfin.financeapp.domain.CashOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashOperationRepository extends JpaRepository<CashOperation, Integer> {
}
