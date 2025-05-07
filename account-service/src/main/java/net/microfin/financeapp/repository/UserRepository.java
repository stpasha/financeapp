package net.microfin.financeapp.repository;

import net.microfin.financeapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, Integer> {
}
