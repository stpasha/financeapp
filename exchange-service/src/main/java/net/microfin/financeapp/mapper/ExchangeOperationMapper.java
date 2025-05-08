package net.microfin.financeapp.mapper;

import net.microfin.financeapp.domain.ExchangeOperation;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExchangeOperationMapper {
    ExchangeOperation toEntity(ExchangeOperationDTO exchangeOperationDTO);
    ExchangeOperationDTO toDto(ExchangeOperation exchangeOperation);
}
