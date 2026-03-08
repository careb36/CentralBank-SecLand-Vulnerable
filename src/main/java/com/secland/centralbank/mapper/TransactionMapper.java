package com.secland.centralbank.mapper;

import com.secland.centralbank.dto.TransactionHistoryDto;
import com.secland.centralbank.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper interface for mapping between Transaction entity and DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransactionMapper {

    /**
     * Maps a Transaction entity to TransactionHistoryDto.
     *
     * @param transaction the Transaction entity to map
     * @return the mapped TransactionHistoryDto
     */
    @Mapping(target = "direction", ignore = true)
    @Mapping(target = "otherParty", ignore = true)
    @Mapping(target = "balanceAfter", ignore = true)
    TransactionHistoryDto toHistoryDto(Transaction transaction);

    /**
     * Maps a list of Transaction entities to a list of TransactionHistoryDto.
     *
     * @param transactions the list of Transaction entities
     * @return the list of mapped TransactionHistoryDto
     */
    List<TransactionHistoryDto> toHistoryDtoList(List<Transaction> transactions);
}