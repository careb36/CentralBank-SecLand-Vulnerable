package com.secland.centralbank.controller;

import com.secland.centralbank.dto.AccountResponseDto;
import com.secland.centralbank.exception.ResourceNotFoundException;
import com.secland.centralbank.mapper.AccountMapper;
import com.secland.centralbank.model.Account;
import com.secland.centralbank.model.User;
import com.secland.centralbank.repository.AccountRepository;
import com.secland.centralbank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for account-related operations.
 * <p>
 * <strong>Security Note (Counterexample):</strong> Unlike the transfer and transaction
 * history endpoints in {@link TransactionController}, this endpoint correctly enforces
 * ownership. It uses the authenticated user's identity (from the security context) rather
 * than a client-supplied parameter, preventing IDOR attacks on account listing.
 * </p>
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    /**
     * Returns the list of bank accounts belonging to the currently authenticated user.
     * <p>
     * <strong>SECURE PRACTICE:</strong> Ownership is verified using the server-side
     * authentication context ({@link Authentication#getName()}). The client cannot
     * influence which user's accounts are returned, preventing IDOR.
     * </p>
     *
     * @param authentication the currently authenticated principal, injected by Spring Security
     * @return {@code 200 OK} with the list of {@link AccountResponseDto} belonging to the user
     * @throws ResourceNotFoundException if the authenticated username is not found in the database
     */
    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getUserAccounts(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        List<Account> accounts = accountRepository.findByUserId(user.getId());
        return ResponseEntity.ok(accountMapper.toResponseDtoList(accounts));
    }
}
