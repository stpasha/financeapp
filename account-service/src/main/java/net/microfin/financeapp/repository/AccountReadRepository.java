package net.microfin.financeapp.repository;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.jooq.tables.records.AccountsRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.microfin.financeapp.jooq.tables.Accounts.ACCOUNTS;

@Repository
@RequiredArgsConstructor
public class AccountReadRepository {
    private final DSLContext dsl;

    public List<AccountsRecord> findAccountsByUserId(UUID userId) {
        return dsl.selectFrom(ACCOUNTS).where(ACCOUNTS.USER_ID.eq(userId)).fetch();
    }

    public Optional<AccountsRecord> findById(UUID id) {
        return dsl.selectFrom(ACCOUNTS).where(ACCOUNTS.ACCOUNT_ID.eq(id)).fetchOptional();
    }
}
