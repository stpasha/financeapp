package net.microfin.financeapp.repository;

import net.microfin.financeapp.domain.Account;
import net.microfin.financeapp.util.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findAccountsByUserId(Integer userId);
    @Modifying
    @Query("UPDATE Account a SET a.active = false WHERE a.id = :id")
    void disableAccount(@Param("id") Integer accountId);
    Optional<Account> findByCurrencyCodeAndUserId(Currency currencyCode, Integer userId);
}
