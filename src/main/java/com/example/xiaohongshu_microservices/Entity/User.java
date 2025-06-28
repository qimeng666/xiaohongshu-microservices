package com.example.xiaohongshu_microservices.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_email", columnList = "email")
})
@EntityListeners(AuditingEntityListener.class)
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;

    @CreatedDate
    @Column(name = "registration_time", nullable = false, updatable = false)
    private LocalDateTime registrationTime;

    @LastModifiedDate
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

}