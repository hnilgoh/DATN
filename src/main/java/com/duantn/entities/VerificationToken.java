package com.duantn.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String token;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String email;

    private LocalDateTime expiryTime;

    public VerificationToken(Long id, String token, String email, LocalDateTime expiryTime) {
        this.id = id;
        this.token = token;
        this.email = email;
        this.expiryTime = expiryTime;
    }

    public VerificationToken(String token, String email) {
        this.token = token;
        this.email = email;
        this.expiryTime = LocalDateTime.now().plusDays(2);
    }
}
