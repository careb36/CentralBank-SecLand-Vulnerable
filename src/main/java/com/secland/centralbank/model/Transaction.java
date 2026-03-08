package com.secland.centralbank.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a transfer transaction between two accounts.
 * <p>
 * Maps to the {@code transactions} table in the database and records details such as:
 * <ul>
 *   <li>Auto-generated primary key</li>
 *   <li>Source and destination account IDs</li>
 *   <li>Transferred amount</li>
 *   <li>Optional description or memo</li>
 *   <li>Timestamp of when the transaction was created</li>
 * </ul>
 * </p>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_txn_source", columnList = "source_account_id"),
        @Index(name = "idx_txn_destination", columnList = "destination_account_id")
})
public class Transaction {

    /**
     * Primary key for the transaction entity, auto-incremented by the database.
     */
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identifier of the account from which funds are withdrawn.
     */
    @Column(name = "source_account_id", nullable = false, insertable = false, updatable = false)
    private Long sourceAccountId;

    /**
     * Identifier of the account to which funds are deposited.
     */
    @Column(name = "destination_account_id", nullable = false, insertable = false, updatable = false)
    private Long destinationAccountId;

    /**
     * JPA relationship to the source account.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;

    /**
     * JPA relationship to the destination account.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id", nullable = false)
    private Account destinationAccount;

    /**
     * Amount of money transferred in the transaction.
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    /**
     * Optional description or memo for the transaction.
     */
    @Column(length = 255)
    private String description;

    /**
     * Type of the transaction.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TransactionType type;

    /**
     * Status of the transaction.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TransactionStatus status;

    /**
     * Timestamp indicating when the transaction was created.
     */
    @CreatedDate
    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate;
}
