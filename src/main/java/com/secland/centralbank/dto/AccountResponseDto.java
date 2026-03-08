package com.secland.centralbank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data transfer object for account response data.
 * <p>
 * Used for returning account information in API responses.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {

    private Long id;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private Long userId;
    private String username;
}