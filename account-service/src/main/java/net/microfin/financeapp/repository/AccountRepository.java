package net.microfin.financeapp.repository;

import jakarta.persistence.LockModeType;
import net.microfin.financeapp.domain.Account;
import net.microfin.financeapp.util.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findAccountsByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Account a SET a.active = false WHERE a.id = :id")
    void disableAccount(@Param("id") Integer accountId);
    Optional<Account> findByCurrencyCodeAndUserId(Currency currencyCode, UUID userId);
}
