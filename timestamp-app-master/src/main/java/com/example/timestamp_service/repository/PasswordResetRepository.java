package com.example.timestamp_service.repository;

import com.example.timestamp_service.model.PasswordReset;
import com.example.timestamp_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByUser(User user);
}
