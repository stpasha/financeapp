package net.microfin.financeapp.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.jooq.tables.records.AccountsRecord;
import net.microfin.financeapp.util.Currency;
import org.jooq.DSLContext;
import org.jooq.UpdateConditionStep;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.microfin.financeapp.jooq.tables.Accounts.ACCOUNTS;

@Repository
@RequiredArgsConstructor
public class AccountWriteRepository {
    private final DSLContext dsl;


    public Optional<AccountsRecord> findActiveByUserAndCurrencyForUpdate (
            UUID userId,
            Currency currency
    ) {
        return dsl.selectFrom(ACCOUNTS)
                .where(ACCOUNTS.USER_ID.eq(userId))
                .and(ACCOUNTS.IS_ACTIVE.eq(true))
                .and(ACCOUNTS.CURRENCY_CODE.eq(currency.name()))
                .forUpdate()
                .fetchOptional();
    }


    public Optional<AccountsRecord> findByIdForUpdate(UUID id) {
        return dsl.selectFrom(ACCOUNTS).where(ACCOUNTS.ACCOUNT_ID.eq(id)).forUpdate().fetchOptional();
    }

    public void disableAccount(UUID accountId) {
        dsl.update(ACCOUNTS).set(ACCOUNTS.IS_ACTIVE, false).where(ACCOUNTS.ACCOUNT_ID.eq(accountId)).execute();
    }

    public void updateAllBalances(List<AccountsRecord> accountsRecords) {
        List<UpdateConditionStep<AccountsRecord>> list = accountsRecords.stream().map(accountsRecord -> dsl.update(ACCOUNTS)
                .set(ACCOUNTS.BALANCE, accountsRecord.getBalance())
                .set(ACCOUNTS.UPDATED_AT, LocalDateTime.now())
                .where(ACCOUNTS.ACCOUNT_ID.eq(accountsRecord.getAccountId()))).toList();
        dsl.batch(list).execute();
    }

    public void deleteAll() {
        dsl.delete(ACCOUNTS).execute();
    }

    public void deleteAllById(List<UUID> accountIds) {
        dsl.delete(ACCOUNTS).where(ACCOUNTS.ACCOUNT_ID.in(accountIds)).execute();
    }

    public void updateBalance(AccountsRecord accountsRecord) {
        dsl.update(ACCOUNTS)
                .set(ACCOUNTS.BALANCE, accountsRecord.getBalance())
                .set(ACCOUNTS.UPDATED_AT, LocalDateTime.now())
                .where(ACCOUNTS.ACCOUNT_ID.eq(accountsRecord.getAccountId()))
                .execute();
    }

    public AccountsRecord insert(AccountsRecord account) {
        LocalDateTime operationDate = LocalDateTime.now();
        return dsl.insertInto(ACCOUNTS,
                ACCOUNTS.ACCOUNT_ID,
                ACCOUNTS.BALANCE,
                ACCOUNTS.CREATED_AT,
                ACCOUNTS.CURRENCY_CODE,
                ACCOUNTS.IS_ACTIVE,
                ACCOUNTS.UPDATED_AT,
                ACCOUNTS.USER_ID).values(
                UuidCreator.getTimeOrderedEpoch(),
                account.getBalance(),
                operationDate,
                account.getCurrencyCode(),
                account.getIsActive(),
                operationDate,
                account.getUserId()
        ).returning().fetchSingle();
    }

    public List<AccountsRecord> insertAll(List<AccountsRecord> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            return List.of();
        }

        var insert = dsl.insertInto(
                ACCOUNTS,
                ACCOUNTS.ACCOUNT_ID,
                ACCOUNTS.BALANCE,
                ACCOUNTS.CREATED_AT,
                ACCOUNTS.CURRENCY_CODE,
                ACCOUNTS.IS_ACTIVE,
                ACCOUNTS.UPDATED_AT,
                ACCOUNTS.USER_ID
        );

        for (AccountsRecord account : accounts) {
            insert = insert.values(
                    account.getAccountId() != null
                            ? account.getAccountId()
                            : UuidCreator.getTimeOrderedEpoch(),
                    account.getBalance(),
                    account.getCreatedAt() != null
                            ? account.getCreatedAt()
                            : LocalDateTime.now(),
                    account.getCurrencyCode(),
                    account.getIsActive(),
                    account.getUpdatedAt() != null
                            ? account.getUpdatedAt()
                            : LocalDateTime.now(),
                    account.getUserId()
            );
        }

        return insert.returning().fetch();
    }



}
