package com.example.timestamp_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PasswordReset {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    private User user;

    private String passwordResetToken;
    private LocalDateTime passwordResetTokenExpirationDate;

    public PasswordReset() {
    }

    public PasswordReset(User user, String passwordResetToken, LocalDateTime passwordResetTokenExpirationDate) {
        this.user = user;
        this.passwordResetToken = passwordResetToken;
        this.passwordResetTokenExpirationDate = passwordResetTokenExpirationDate;
    }
}
