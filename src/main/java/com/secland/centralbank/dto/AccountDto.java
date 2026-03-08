package com.secland.centralbank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data transfer object for account data.
 * <p>
 * Used for creating and updating accounts.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "Account type is required")
    private String accountType;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", message = "Balance must be non-negative")
    private BigDecimal balance;

    @NotNull(message = "User ID is required")
    private Long userId;
}