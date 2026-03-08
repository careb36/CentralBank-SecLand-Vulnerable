package com.secland.centralbank.service;

import com.secland.centralbank.dto.LoginRequestDto;
import com.secland.centralbank.dto.LoginResponseDto;
import com.secland.centralbank.dto.RegisterUserDto;
import com.secland.centralbank.dto.UserResponseDto;
import com.secland.centralbank.model.User;
import com.secland.centralbank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of the AuthService interface. Handles the logic for user registration and login.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    /**
     * Creates a new user, hashes their password, and persists them to the database.
     * This is a secure implementation practice.
     *
     * @param registerUserDto DTO containing the registration details.
     * @return The persisted User object with its new ID.
     */
    @Override
    public UserResponseDto register(RegisterUserDto registerUserDto) {
        User user = new User();
        user.setUsername(registerUserDto.getUsername());
        user.setFullName(registerUserDto.getFullName());
        // SECURITY BEST PRACTICE: Always hash passwords before storing them.
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        User savedUser = userRepository.save(user);
        return UserResponseDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .fullName(savedUser.getFullName())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    /**
     * Authenticates a user using the AuthenticationManager and returns a login response.
     *
     * @param loginRequest DTO containing the login credentials.
     * @return A LoginResponseDto with a message and token.
     */
    @Override
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        try {
            // We delegate the authentication task entirely to the AuthenticationManager.
            // It will use our CustomUserDetailsService and PasswordEncoder automatically.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // If the line above doesn't throw an exception, the user is authenticated.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // For now, we return a simple success message and a simulated token.
            String token = "simulated.jwt.token.for." + loginRequest.getUsername();
            return new LoginResponseDto("Login successful!", token);

        } catch (Exception e) {
            // Return a proper error response instead of letting Spring Security handle it
            return new LoginResponseDto("Invalid username or password", null);
        }
    }
}
