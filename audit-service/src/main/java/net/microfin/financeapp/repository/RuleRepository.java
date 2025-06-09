package net.microfin.financeapp.repository;

import net.microfin.financeapp.domain.Rule;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Integer> {
    Optional<Rule> findRuleByOperationTypeAndCurrencyCode(OperationType operationType, Currency currencyCode);
}
