package com.example.timestamp_service.repository;

import com.example.timestamp_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findUserByVerificationCode(String code);
    ArrayList<User> findAllByRoleNull();
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);

    User findByEmail(String s);
}
