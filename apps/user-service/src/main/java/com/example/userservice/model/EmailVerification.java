package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "email_verification")
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)   // 이메일당 한 행
    private String email;

    @Column(length = 6, nullable = false)
    private String code;                       // 6자리 숫자

    private LocalDateTime expiry;              // 만료 시각

    /** ✅ 인증 완료 여부 (기본 false) */
    @Column(nullable = false)
    @Builder.Default
    private boolean verified = false;
}
