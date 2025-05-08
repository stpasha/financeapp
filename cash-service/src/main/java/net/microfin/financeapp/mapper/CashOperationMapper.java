package net.microfin.financeapp.mapper;

import net.microfin.financeapp.domain.CashOperation;
import net.microfin.financeapp.dto.CashOperationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CashOperationMapper {
    CashOperation toEntity(CashOperationDTO cashOperationDTO);
    CashOperationDTO toDto(CashOperation cashOperation);
}
