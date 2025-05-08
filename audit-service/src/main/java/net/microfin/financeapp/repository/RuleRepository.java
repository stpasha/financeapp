package net.microfin.financeapp.repository;

import net.microfin.financeapp.domain.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleRepository extends JpaRepository<Rule, Integer> {
}
