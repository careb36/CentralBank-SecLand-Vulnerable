package com.secland.centralbank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for new user registration requests.
 * <p>
 * Used to capture and validate input data required for creating a new user account.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDto {

    /**
     * Desired username for the new user account.
     * <p>
     * This field must not be blank and must be between 3 and 20 characters in length.
     * </p>
     */
    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must contain only letters, numbers and underscores")
    private String username;

    /**
     * Password for the new user account.
     * <p>
     * This field must not be blank and must be at least 8 characters for security purposes.
     * </p>
     */
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "Password must contain at least one lowercase letter, one uppercase letter and one digit")
    private String password;

    /**
     * Full name of the user.
     * <p>
     * This field is optional but recommended for improved clarity in user profiles and displays.
     * </p>
     */
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    /**
     * Email address of the user.
     * <p>
     * Must conform to a valid email format.
     * </p>
     */
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
}
