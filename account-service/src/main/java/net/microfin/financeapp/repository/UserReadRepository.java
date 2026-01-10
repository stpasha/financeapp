package net.microfin.financeapp.repository;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.exception.UserNotFoundException;
import net.microfin.financeapp.jooq.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.microfin.financeapp.jooq.tables.Users.USERS;

@Repository
@RequiredArgsConstructor
public class UserReadRepository {
    private final DSLContext dsl;

    public UsersRecord findById(UUID userId) {
        return dsl.selectFrom(USERS).where(USERS.USER_ID.eq(userId)).fetchOptional().orElseThrow(() -> new UserNotFoundException("Unable to find user " + userId));
    }


    public Optional<UsersRecord> findUsersByUsername(String userName) {
        return dsl.selectFrom(USERS).where(USERS.USER_NAME.eq(userName)).fetchOptional();
    }

    public List<UsersRecord> findAll() {
        return dsl.selectFrom(USERS).fetch();
    }
}
