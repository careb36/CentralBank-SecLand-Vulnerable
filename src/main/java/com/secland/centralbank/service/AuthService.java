package com.secland.centralbank.service;

import com.secland.centralbank.dto.LoginRequestDto;
import com.secland.centralbank.dto.LoginResponseDto;
import com.secland.centralbank.dto.RegisterUserDto;
import com.secland.centralbank.dto.UserResponseDto;

/**
 * Service interface defining the contract for authentication-related operations.
 */
public interface AuthService {

    /**
     * Registers a new user in the system.
     *
     * @param registerUserDto The DTO containing data for the new user.
     * @return The created UserResponseDto.
     */
    UserResponseDto register(RegisterUserDto registerUserDto);

    /**
     * Authenticates a user and returns a login response with a token.
     *
     * @param loginRequest The DTO containing login credentials.
     * @return A LoginResponseDto with a message and token.
     */
    LoginResponseDto login(LoginRequestDto loginRequest);
}
