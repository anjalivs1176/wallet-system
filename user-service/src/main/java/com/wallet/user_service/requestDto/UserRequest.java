package com.wallet.user_service.requestDto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "Name Cannot Be Empty")
    private String name;

    @Column(unique = true)
    @Email(message = "Invalid email Format")
    private String email;

    @Size(min = 4, message = "Password must be minimum 4 characters")
    private String password;
}
