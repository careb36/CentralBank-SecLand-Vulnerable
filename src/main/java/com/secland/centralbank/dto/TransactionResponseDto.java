package com.secland.centralbank.dto;

import com.secland.centralbank.model.TransactionStatus;
import com.secland.centralbank.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {
    private Long id;
    private Long sourceAccountId;
    private Long destinationAccountId;
    private BigDecimal amount;
    private String description;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime transactionDate;
}
