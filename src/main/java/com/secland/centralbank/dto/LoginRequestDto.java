package com.secland.centralbank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object representing the payload for user login requests.
 * <p>
 * Encapsulates the credentials (username and password) required for user authentication.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    /**
     * Username of the user attempting to log in.
     * <p>
     * This field is required and must not be blank.
     * </p>
     */
    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /**
     * Password of the user attempting to log in.
     * <p>
     * This field is required and must not be blank.
     * </p>
     */
    @NotBlank(message = "Password must not be blank")
    @Size(min = 1, max = 100, message = "Password must be between 1 and 100 characters")
    private String password;
}
