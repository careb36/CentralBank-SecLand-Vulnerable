package com.secland.centralbank.repository;

import com.secland.centralbank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Account} entities.
 * <p>
 * Provides CRUD operations and pagination/sorting out of the box
 * via Spring Data JPA.
 * </p>
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Retrieves all accounts belonging to a specific user.
     *
     * @param userId the ID of the user whose accounts to retrieve
     * @return a list of accounts owned by the specified user
     */
    List<Account> findByUserId(Long userId);

    /**
     * Finds an account by its unique account number.
     *
     * @param accountNumber the account number to search for
     * @return an Optional containing the matching account, or empty if not found
     */
    Optional<Account> findByAccountNumber(String accountNumber);
}
