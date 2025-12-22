package com.sagarmalasi.project.domain.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "User is Required")
    @Size(min = 2,max = 50,message = "Category name must be between {min} and {max} characters")
    @Pattern(regexp = "^[\\w\\s-]+$",message = "Category name can only contain letters,numbers,spaces and hyphens")
    String username;

    @NotBlank(message = "Email is Required")
    @Email
    String email;

    @NotBlank(message = "Password is Required")
    @Size(min = 8)
    String password;

}
