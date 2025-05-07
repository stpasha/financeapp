package net.microfin.financeapp.repository;

import net.microfin.financeapp.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
}
