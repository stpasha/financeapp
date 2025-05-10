package net.microfin.financeapp.repository;

import net.microfin.financeapp.domain.ExchangeOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeOperationRepository extends JpaRepository<ExchangeOperation, Integer> {
}
