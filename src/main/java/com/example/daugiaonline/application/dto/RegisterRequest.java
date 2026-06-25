package com.example.daugiaonline.application.dto;

import com.example.daugiaonline.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Mật khẩu phải từ 6 ký tự")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotNull(message = "Role is required")
    private RoleName roleName;
}
