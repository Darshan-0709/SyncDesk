package com.SyncDesk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // @Column(nullable = false, length=100)
    private String fullName;
    // @Column(unique = true, nullable = false, length=255)
    private String email;
    // @Column(nullable = false, length = 255)
    private String password;
    // @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    // @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
