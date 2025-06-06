package net.microfin.financeapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.CashOperationClient;
import net.microfin.financeapp.domain.CashOperation;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.CashOperationResultDTO;
import net.microfin.financeapp.mapper.CashOperationMapper;
import net.microfin.financeapp.repository.CashOperationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultCashOperationService implements CashOperationService {

    private final CashOperationClient cashOperationClient;
    private final CashOperationMapper cashOperationMapper;
    private final CashOperationRepository cashOperationRepository;

    @Override
    @Transactional
    public ResponseEntity<CashOperationResultDTO> performOperation(CashOperationDTO operationDTO) {
        CashOperation cashOperation = cashOperationRepository.save(cashOperationMapper.toEntity(operationDTO));
        operationDTO.setId(cashOperation.getId());
        return cashOperationClient.cashOperation(operationDTO);
    }
}
