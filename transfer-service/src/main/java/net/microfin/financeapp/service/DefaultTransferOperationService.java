package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.AccountClientImpl;
import net.microfin.financeapp.client.AuditClientImpl;
import net.microfin.financeapp.client.DictionaryClientImpl;
import net.microfin.financeapp.client.NotificationClientImpl;
import net.microfin.financeapp.config.ExceptionsProperties;
import net.microfin.financeapp.domain.TransferOperation;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.mapper.TransferOperationMapper;
import net.microfin.financeapp.repository.TransferOperationRepository;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultTransferOperationService implements TransferOperationService {

    private final AuditClientImpl auditClient;
    private final NotificationClientImpl notificationClient;
    private final AccountClientImpl accountClient;
    private final DictionaryClientImpl dictionaryClient;
    private final TransferOperationMapper operationMapper;
    private final TransferOperationRepository operationRepository;
    private final ExceptionsProperties exceptionsProperties;

    @Override
    public TransferOperationResultDTO performOperation(TransferOperationDTO transferOperationDTO) {
        ResponseEntity<Boolean> check = auditClient.check(transferOperationDTO);
        if (check.getStatusCode().is2xxSuccessful()) {
            if (Boolean.TRUE.equals(check.getBody())) {
                ResponseEntity<List<CurrencyDTO>> responseEntity = dictionaryClient.listCurrency();
                ResponseEntity<AccountDTO> sourceAccountResp = accountClient.getAccount(transferOperationDTO.getSourceAccountId());
                ResponseEntity<AccountDTO> targetAccountResp = accountClient.getAccount(transferOperationDTO.getTargetAccountId());
                return getDTO(transferOperationDTO, responseEntity, sourceAccountResp, targetAccountResp);
            }
            return TransferOperationResultDTO.builder().message("Operation " + transferOperationDTO.getOperationType() + " "
                  + transferOperationDTO.getAmount() + " "+ "prohibitted").status(OperationStatus.FAILED).build();
        }
        return TransferOperationResultDTO.builder().message("Server unavailable.").status(OperationStatus.FAILED).build();
    }

    private TransferOperationResultDTO getDTO(TransferOperationDTO transferOperationDTO, ResponseEntity<List<CurrencyDTO>> responseEntity, ResponseEntity<AccountDTO> sourceAccountResp, ResponseEntity<AccountDTO> targetAccountResp) {
        if (responseEntity.getStatusCode().is2xxSuccessful()
                && sourceAccountResp.getStatusCode().is2xxSuccessful()
                && targetAccountResp.getStatusCode().is2xxSuccessful()) {


            List<CurrencyDTO> currencies = responseEntity.getBody();
            AccountDTO sourceAccount = sourceAccountResp.getBody();
            AccountDTO targetAccount = targetAccountResp.getBody();

            if (transferOperationDTO.getAmount().compareTo(sourceAccount.getBalance()) > 0) {
                throw new RuntimeException(exceptionsProperties.getInsufficientFundsFailure());
            }

            if (!transferOperationDTO.getUserId().equals(sourceAccount.getUser().getId())) {
                throw new RuntimeException(exceptionsProperties.getIncorectSourceAccountFailure());
            }
            Optional<CurrencyDTO> sourceCurrencyOpt = currencies.stream()
                    .filter(c -> c.code().equals(sourceAccount.getCurrencyCode()))
                    .findFirst();

            Optional<CurrencyDTO> targetCurrencyOpt = currencies.stream()
                    .filter(c -> c.code().equals(targetAccount.getCurrencyCode()))
                    .findFirst();

            if (sourceCurrencyOpt.isEmpty() || targetCurrencyOpt.isEmpty()) {
                throw new RuntimeException(exceptionsProperties.getCurrencyNotFoundFailure());
            }

            CurrencyDTO sourceCurrency = sourceCurrencyOpt.get();
            CurrencyDTO targetCurrency = targetCurrencyOpt.get();

            BigDecimal convertedAmount = transferOperationDTO.getAmount()
                    .multiply(sourceCurrency.value())
                    .divide(targetCurrency.value(), 2, RoundingMode.HALF_UP);

            transferOperationDTO.setTargetAmount(convertedAmount);

            TransferOperation transferOperation = operationRepository.save(operationMapper.toEntity(transferOperationDTO));
            transferOperationDTO.setId(transferOperation.getId());
            ResponseEntity<TransferOperationResultDTO> transferredOperation = accountClient.transferOperation(transferOperationDTO);
            if (transferredOperation.getStatusCode().is2xxSuccessful()) {
                notificationClient.saveNotification(NotificationDTO.builder()
                        .notificationDescription("Выполнена запрос на " + transferOperationDTO.getOperationType() + " " +
                                transferOperationDTO.getAmount() + " " +
                                sourceCurrency.name())
                                .userId(transferOperationDTO.getUserId())
                        .operationType(transferOperationDTO.getOperationType().name()).build());
            }
            return transferredOperation.getBody();

        } else {
            throw new RuntimeException(exceptionsProperties.getAccNotFoundFailure());
        }
    }
}
