package com.example.daugiaonline.application.dto;

import com.example.daugiaonline.enums.RoleName;
import com.example.daugiaonline.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String address;
    private Double balance;
    private RoleName roleName;
    private UserStatus status;
    private String cccd;
}
