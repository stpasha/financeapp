package net.microfin.financeapp.mapper;

import net.microfin.financeapp.domain.TransferOperation;
import net.microfin.financeapp.dto.TransferOperationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferOperationMapper {
    TransferOperation toEntity(TransferOperationDTO dto);
    TransferOperationDTO toDto(TransferOperation entity);
}