package net.microfin.financeapp.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.jooq.tables.records.UsersRecord;
import net.microfin.financeapp.mapper.UserLegacyMapper;
import org.jooq.DSLContext;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

import static net.microfin.financeapp.jooq.tables.Users.USERS;

@Repository
@RequiredArgsConstructor
public class UserWriteRepository {
    private final DSLContext dsl;

    private final UserLegacyMapper userLegacyMapper;


    public UsersRecord insert(UsersRecord usersRecord) {
        if (usersRecord.getUserId() == null) {
            usersRecord.setUserId(UuidCreator.getTimeOrderedEpoch());
        }
        if (usersRecord.getCreatedAt() == null) {
            usersRecord.setCreatedAt(LocalDateTime.now());
        }
        if (usersRecord.getUpdatedAt() == null) {
            usersRecord.setUpdatedAt(LocalDateTime.now());
        }
        return dsl.insertInto(USERS).set(usersRecord)
                .returning()
                .fetchSingle();

    }

    public UsersRecord updateKeycloakData(UUID userId, UUID keycloakId) {
        return dsl.update(USERS)
                .set(USERS.KEYCLOAK_ID, keycloakId)
                .set(USERS.IS_ENABLED, true)
                .set(USERS.UPDATED_AT, LocalDateTime.now())
                .where(USERS.USER_ID.eq(userId))
                .and(
                        USERS.KEYCLOAK_ID.isNull()
                                .or(USERS.KEYCLOAK_ID.ne(keycloakId))
                )
                .returning()
                .fetchOptional()
                .orElseGet(() ->
                        dsl.selectFrom(USERS)
                                .where(USERS.USER_ID.eq(userId))
                                .fetchSingle()
                );
    }

    public UsersRecord update(UsersRecord usersRecord) {
        UpdateSetFirstStep<UsersRecord> update = dsl.update(USERS);
        UpdateSetMoreStep<UsersRecord> set = null;

        boolean changed = false;

        if (usersRecord.getDob() != null) {
            set = (set == null ? update : set)
                    .set(USERS.DOB, usersRecord.getDob());
            changed = true;
        }

        if (usersRecord.getFullName() != null) {
            set = (set == null ? update : set)
                    .set(USERS.FULL_NAME, usersRecord.getFullName());
            changed = true;
        }

        if (!changed) {
            return dsl.selectFrom(USERS)
                    .where(USERS.USER_ID.eq(usersRecord.getUserId()))
                    .fetchSingle();
        }

        return set
                .set(USERS.UPDATED_AT, LocalDateTime.now())
                .where(USERS.USER_ID.eq(usersRecord.getUserId()))
                .returning()
                .fetchSingle();
    }



    public void deleteAll() {
        dsl.delete(USERS).execute();
    }

}
