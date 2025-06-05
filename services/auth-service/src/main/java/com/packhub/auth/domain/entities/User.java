package com.packhub.auth.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.*;

@Setter
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    private Integer userCode;

    @NotNull
    @Column(nullable = false)
    private String password;
}

