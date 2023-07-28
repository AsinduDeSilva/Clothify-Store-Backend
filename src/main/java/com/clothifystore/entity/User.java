package com.clothifystore.entity;

import com.clothifystore.enums.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private UserRoles role;

    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean isEnabled;

    private String otp;
    private LocalDateTime otpExpirationTime;
}
