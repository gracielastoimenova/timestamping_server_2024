package com.example.timestamp_service.model;

import com.example.timestamp_service.model.roles.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@Table(name="my_users")
@Entity
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String fullname;
    @Column( nullable = false, unique = true) private String username;
    @Column(nullable = false) private String password;
    @Enumerated(EnumType.STRING) private UserRole role;
    @Column private boolean isValidated;
    @Column private String verificationCode;

    public User() {
    }

    public User(String email, String fullname, String username, String password) {
        this.email = email;
        this.fullname = fullname;
        this.username = username;
        this.password = password;
        this.role = null;
    }

    public User(String email, String fullname, String username, String password, UserRole role) {
        this.email = email;
        this.fullname = fullname;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return isValidated;
    }



}
