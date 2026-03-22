package com.secland.centralbank.mapper;

import com.secland.centralbank.dto.TransactionHistoryDto;
import com.secland.centralbank.model.Transaction;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-22T20:38:37+0000",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public TransactionHistoryDto toHistoryDto(Transaction transaction) {
        if ( transaction == null ) {
            return null;
        }

        TransactionHistoryDto.TransactionHistoryDtoBuilder transactionHistoryDto = TransactionHistoryDto.builder();

        transactionHistoryDto.id( transaction.getId() );
        transactionHistoryDto.amount( transaction.getAmount() );
        transactionHistoryDto.description( transaction.getDescription() );
        transactionHistoryDto.transactionDate( transaction.getTransactionDate() );
        transactionHistoryDto.status( transaction.getStatus() );

        return transactionHistoryDto.build();
    }

    @Override
    public List<TransactionHistoryDto> toHistoryDtoList(List<Transaction> transactions) {
        if ( transactions == null ) {
            return null;
        }

        List<TransactionHistoryDto> list = new ArrayList<TransactionHistoryDto>( transactions.size() );
        for ( Transaction transaction : transactions ) {
            list.add( toHistoryDto( transaction ) );
        }

        return list;
    }
}
