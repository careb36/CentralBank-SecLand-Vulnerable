package com.secland.centralbank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data transfer object representing the payload for a funds transfer request between accounts.
 * <p>
 * Used as input for transfer operations, ensuring all required fields are validated before processing.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {

    /**
     * Identifier of the source account from which funds will be withdrawn.
     * <p>
     * This field is required and must not be {@code null}.
     * </p>
     */
    @NotNull(message = "Source account ID is required")
    private Long sourceAccountId;

    /**
     * Identifier of the destination account to which funds will be deposited.
     * <p>
     * This field is required and must not be {@code null}.
     * </p>
     */
    @NotNull(message = "Destination account ID is required")
    private Long destinationAccountId;

    /**
     * Amount to transfer between accounts.
     * <p>
     * Must not be {@code null} and must be at least 1 (as the minimal transfer amount).
     * {@link BigDecimal} is used for financial precision.
     * </p>
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    @Digits(integer = 15, fraction = 2, message = "Amount must have at most 15 integer digits and 2 decimal places")
    private BigDecimal amount;

    /**
     * Optional description or memo for the transfer transaction.
     * <p>
     * Can be used to specify a note, reference, or purpose for the transfer.
     * </p>
     */
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
}
