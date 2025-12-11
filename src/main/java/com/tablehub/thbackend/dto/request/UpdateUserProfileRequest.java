package com.tablehub.thbackend.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {
    @Email(message = "Invalid email format")
    private String email;

    private String name;
}