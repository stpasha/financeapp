package net.microfin.financeapp.repository;

import net.microfin.financeapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepositoryLegacy  extends JpaRepository<User, UUID> {
    Optional<User> findUsersByUsername(String username);
}
