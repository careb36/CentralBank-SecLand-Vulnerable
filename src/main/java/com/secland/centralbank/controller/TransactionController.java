package com.secland.centralbank.controller;

import com.secland.centralbank.dto.TransactionHistoryDto;
import com.secland.centralbank.dto.TransactionResponseDto;
import com.secland.centralbank.dto.TransferRequestDto;
import com.secland.centralbank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing endpoints for transaction-related operations.
 * <p>
 * <strong>Note:</strong> The {@code @RequestMapping("/api/accounts")} path is kept for backwards compatibility
 * with existing API consumers, even though this controller now handles transactions.
 * </p>
 * <p>
 * <strong>Security Notice:</strong> This controller intentionally contains an Insecure Direct Object Reference (IDOR)
 * vulnerability in the transferMoney endpoint for educational and demonstration purposes.
 * </p>
 *
 * @see <a href="https://spring.io/guides/tutorials/rest/">Building REST services with Spring</a>
 * @see <a href="https://cheatsheetseries.owasp.org/cheatsheets/Insecure_Direct_Object_Reference_Prevention_Cheat_Sheet.html">
 *      OWASP IDOR Prevention Cheat Sheet</a>
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Executes a money transfer between two user accounts.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This endpoint does not verify that the authenticated user
     * is authorized to transfer funds from the specified source account.
     * Attackers could manipulate the payload to transfer funds from any account.
     * </p>
     *
     * @param transferRequestDto payload containing the source account ID, destination account ID, amount, and optional description
     * @return {@code 200 OK} with the created {@link Transaction} if successful;
     *         {@code 400 Bad Request} if an error occurs (e.g., account not found or business rule violation)
     */
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDto> transferMoney(
            @RequestBody TransferRequestDto transferRequestDto) {

        TransactionResponseDto transaction = transactionService.performTransfer(transferRequestDto);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Retrieves transaction history for a specific account.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This endpoint does not verify that
     * the authenticated user owns the specified account. Any authenticated user can retrieve
     * transaction history for any account by providing the account ID in the URL path.
     * </p>
     * <p>
     * <strong>Intentional Vulnerability (Stored XSS):</strong> The returned transaction data
     * includes raw description fields that may contain malicious scripts stored from previous
     * transfer operations. When rendered by a front-end without proper encoding, this could
     * lead to stored XSS attacks.
     * </p>
     *
     * @param accountId the ID of the account to retrieve transaction history for
     * @return ResponseEntity containing a list of TransactionHistoryDto with transaction details
     */
    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionHistoryDto>> getTransactionHistory(
            @PathVariable Long accountId) {

        // VULNERABILITY: No authorization check - any user can access any account's transactions
        List<TransactionHistoryDto> transactions = transactionService.getTransactionHistory(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{accountId}/transactions/paged")
    public ResponseEntity<Page<TransactionHistoryDto>> getTransactionHistoryPaged(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<TransactionHistoryDto> history = transactionService.getTransactionHistory(accountId, pageable);
        return ResponseEntity.ok(history);
    }
}
