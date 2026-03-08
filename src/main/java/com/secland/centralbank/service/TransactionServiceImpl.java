package com.secland.centralbank.service;

import com.secland.centralbank.dto.TransactionHistoryDto;
import com.secland.centralbank.dto.TransactionResponseDto;
import com.secland.centralbank.dto.TransferRequestDto;
import com.secland.centralbank.exception.InsufficientFundsException;
import com.secland.centralbank.exception.ResourceNotFoundException;
import com.secland.centralbank.mapper.TransactionMapper;
import com.secland.centralbank.model.Account;
import com.secland.centralbank.model.Transaction;
import com.secland.centralbank.model.TransactionStatus;
import com.secland.centralbank.model.TransactionType;
import com.secland.centralbank.repository.AccountRepository;
import com.secland.centralbank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation for the TransactionService.
 * This class contains deliberate vulnerabilities for ethical hacking purposes.
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final EntityManager entityManager;

    private final TransactionMapper transactionMapper;

    /**
     * Performs a funds transfer from a source account to a destination account.
     *
     * INTENTIONAL VULNERABILITY #1 (IDOR): The method does NOT check if the
     * authenticated user is the owner of the sourceAccountId. An attacker only
     * needs to know another user's account ID to transfer funds from it.
     *
     * INTENTIONAL VULNERABILITY #2 (Business Logic Flaw): The method does NOT
     * check if the source account has sufficient funds, allowing for negative balances.
     *
     * @param transferRequestDto DTO with transfer details.
     * @return The saved Transaction object.
     * @throws RuntimeException if source or destination accounts are not found.
     */
    @Override
    @Transactional
    public TransactionResponseDto performTransfer(TransferRequestDto transferRequestDto) {
        Account sourceAccount = accountRepository.findById(transferRequestDto.getSourceAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", transferRequestDto.getSourceAccountId()));

        Account destinationAccount = accountRepository.findById(transferRequestDto.getDestinationAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", transferRequestDto.getDestinationAccountId()));

        BigDecimal amount = transferRequestDto.getAmount();

        // Perform the transfer
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        // Balances updated via JPA dirty checking within @Transactional

        // Record the transaction
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setAmount(amount);
        transaction.setDescription(transferRequestDto.getDescription());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.COMPLETED);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionResponseDto.builder()
                .id(savedTransaction.getId())
                .sourceAccountId(savedTransaction.getSourceAccountId())
                .destinationAccountId(savedTransaction.getDestinationAccountId())
                .amount(savedTransaction.getAmount())
                .description(savedTransaction.getDescription())
                .type(savedTransaction.getType())
                .status(savedTransaction.getStatus())
                .transactionDate(savedTransaction.getTransactionDate())
                .build();
    }

    /**
     * Retrieves transaction history for a specific account without authorization checks.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This method does not verify that
     * the authenticated user owns the specified account. Any authenticated user can
     * retrieve transaction history for any account by providing the account ID.
     * </p>
     *
     * @param accountId the ID of the account to retrieve transactions for
     * @return List of TransactionHistoryDto containing transaction details
     */
    @Override
    @Transactional(readOnly = true)
    public List<TransactionHistoryDto> getTransactionHistory(Long accountId) {
        // VULNERABILITY: No authorization check - any user can access any account's transactions
        List<Transaction> transactions = transactionRepository.findBySourceAccountIdOrDestinationAccountId(
                accountId, accountId);

        return transactions.stream()
                .map(transaction -> {
                    TransactionHistoryDto dto = transactionMapper.toHistoryDto(transaction);

                    // Get account numbers for display
                    Account sourceAccount = accountRepository.findById(transaction.getSourceAccountId()).orElse(null);
                    Account destinationAccount = accountRepository.findById(transaction.getDestinationAccountId()).orElse(null);

                    dto.setFromAccountNumber(sourceAccount != null ? sourceAccount.getAccountNumber() : "Unknown");
                    dto.setToAccountNumber(destinationAccount != null ? destinationAccount.getAccountNumber() : "Unknown");
                    dto.setAmount(transaction.getAmount());
                    // VULNERABILITY: Raw description returned without sanitization (Stored XSS)
                    dto.setDescription(transaction.getDescription());
                    dto.setTransactionDate(transaction.getTransactionDate());
                    dto.setTransactionType(TransactionType.TRANSFER);
                    dto.setStatus(TransactionStatus.COMPLETED);
                    dto.setDirection("OUT"); // Default direction
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Searches transactions by description using vulnerable SQL concatenation.
     * <p>
     * <strong>Intentional Vulnerability (SQL Injection):</strong> This method constructs
     * SQL queries using string concatenation without proper parameterization, making it
     * vulnerable to SQL injection attacks through the description parameter.
     * </p>
     *
     * @param description the description text to search for in transactions
     * @return List of TransactionHistoryDto matching the search criteria
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<TransactionHistoryDto> searchTransactionsByDescription(String description) {
        // VULNERABILITY: SQL Injection through string concatenation
        String sql = "SELECT t.id, t.source_account_id, t.destination_account_id, t.amount, " +
                     "t.description, t.transaction_date FROM transactions t " +
                     "WHERE t.description LIKE '%" + description + "%'";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(row -> {
                    TransactionHistoryDto dto = new TransactionHistoryDto();
                    dto.setId(((Number) row[0]).longValue());

                    // Get account numbers for display
                    Long sourceAccountId = ((Number) row[1]).longValue();
                    Long destinationAccountId = ((Number) row[2]).longValue();

                    Account sourceAccount = accountRepository.findById(sourceAccountId).orElse(null);
                    Account destinationAccount = accountRepository.findById(destinationAccountId).orElse(null);

                    dto.setFromAccountNumber(sourceAccount != null ? sourceAccount.getAccountNumber() : "Unknown");
                    dto.setToAccountNumber(destinationAccount != null ? destinationAccount.getAccountNumber() : "Unknown");
                    dto.setAmount((BigDecimal) row[3]);
                    // VULNERABILITY: Raw description returned without sanitization (Stored XSS)
                    dto.setDescription((String) row[4]);
                    dto.setTransactionDate(((java.sql.Timestamp) row[5]).toLocalDateTime());
                    dto.setTransactionType(TransactionType.TRANSFER);
                    dto.setStatus(TransactionStatus.COMPLETED);
                    dto.setDirection("OUT"); // Default direction
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionHistoryDto> getTransactionHistory(Long accountId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository
                .findBySourceAccountIdOrDestinationAccountId(accountId, accountId, pageable);

        return transactions.map(transaction -> {
            TransactionHistoryDto dto = transactionMapper.toHistoryDto(transaction);
            // Set calculated fields (same logic as the non-paginated version)
            Account sourceAccount = accountRepository.findById(transaction.getSourceAccountId()).orElse(null);
            Account destinationAccount = accountRepository.findById(transaction.getDestinationAccountId()).orElse(null);

            if (sourceAccount != null) {
                dto.setFromAccountNumber(sourceAccount.getAccountNumber());
            }
            if (destinationAccount != null) {
                dto.setToAccountNumber(destinationAccount.getAccountNumber());
            }

            boolean isOutgoing = transaction.getSourceAccountId().equals(accountId);
            dto.setDirection(isOutgoing ? "OUTGOING" : "INCOMING");

            return dto;
        });
    }
}
