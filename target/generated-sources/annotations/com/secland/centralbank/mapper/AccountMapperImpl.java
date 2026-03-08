package com.secland.centralbank.mapper;

import com.secland.centralbank.dto.AccountDto;
import com.secland.centralbank.dto.AccountResponseDto;
import com.secland.centralbank.model.Account;
import com.secland.centralbank.model.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-07T22:23:38-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public AccountResponseDto toResponseDto(Account account) {
        if ( account == null ) {
            return null;
        }

        AccountResponseDto.AccountResponseDtoBuilder accountResponseDto = AccountResponseDto.builder();

        accountResponseDto.userId( accountUserId( account ) );
        accountResponseDto.username( accountUserUsername( account ) );
        accountResponseDto.id( account.getId() );
        accountResponseDto.accountNumber( account.getAccountNumber() );
        accountResponseDto.accountType( account.getAccountType() );
        accountResponseDto.balance( account.getBalance() );
        accountResponseDto.createdAt( account.getCreatedAt() );

        return accountResponseDto.build();
    }

    @Override
    public Account toEntity(AccountDto dto) {
        if ( dto == null ) {
            return null;
        }

        Account account = new Account();

        account.setAccountNumber( dto.getAccountNumber() );
        account.setAccountType( dto.getAccountType() );
        account.setBalance( dto.getBalance() );

        return account;
    }

    @Override
    public void updateEntity(AccountDto dto, Account account) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getAccountNumber() != null ) {
            account.setAccountNumber( dto.getAccountNumber() );
        }
        if ( dto.getAccountType() != null ) {
            account.setAccountType( dto.getAccountType() );
        }
        if ( dto.getBalance() != null ) {
            account.setBalance( dto.getBalance() );
        }
    }

    @Override
    public List<AccountResponseDto> toResponseDtoList(List<Account> accounts) {
        if ( accounts == null ) {
            return null;
        }

        List<AccountResponseDto> list = new ArrayList<AccountResponseDto>( accounts.size() );
        for ( Account account : accounts ) {
            list.add( toResponseDto( account ) );
        }

        return list;
    }

    private Long accountUserId(Account account) {
        User user = account.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    private String accountUserUsername(Account account) {
        User user = account.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getUsername();
    }
}
