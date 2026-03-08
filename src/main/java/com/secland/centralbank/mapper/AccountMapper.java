package com.secland.centralbank.mapper;

import com.secland.centralbank.dto.AccountDto;
import com.secland.centralbank.dto.AccountResponseDto;
import com.secland.centralbank.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper interface for mapping between Account entity and DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper {

    /**
     * Maps an Account entity to AccountResponseDto.
     *
     * @param account the Account entity to map
     * @return the mapped AccountResponseDto
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    AccountResponseDto toResponseDto(Account account);

    /**
     * Maps an AccountDto to a new Account entity.
     *
     * @param dto the AccountDto to map
     * @return the mapped Account entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Account toEntity(AccountDto dto);

    /**
     * Updates an existing Account entity with data from AccountDto.
     *
     * @param dto the AccountDto with updated data
     * @param account the Account entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntity(AccountDto dto, @MappingTarget Account account);

    /**
     * Maps a list of Account entities to a list of AccountResponseDto.
     *
     * @param accounts the list of Account entities
     * @return the list of mapped AccountResponseDto
     */
    List<AccountResponseDto> toResponseDtoList(List<Account> accounts);
}