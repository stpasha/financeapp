package net.microfin.financeapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.AccountClientImpl;
import net.microfin.financeapp.client.AuditClientImpl;
import net.microfin.financeapp.client.DictionaryClientImpl;
import net.microfin.financeapp.config.ExceptionsProperties;
import net.microfin.financeapp.domain.ExchangeOperation;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.mapper.ExchangeOperationMapper;
import net.microfin.financeapp.producer.NotificationKafkaProducer;
import net.microfin.financeapp.repository.ExchangeOperationRepository;
import net.microfin.financeapp.util.OperationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultExchangeOperationService implements ExchangeOperationService {

    private final AuditClientImpl auditClient;
    private final NotificationKafkaProducer notificationKafkaProducer;
    private final AccountClientImpl accountClient;
    private final DictionaryClientImpl dictionaryClient;
    private final ExchangeOperationMapper operationMapper;
    private final ExchangeOperationRepository operationRepository;
    private final ExceptionsProperties exceptionsProperties;

    @Override
    @Transactional
    public ResponseEntity<ExchangeOperationResultDTO> performOperation(ExchangeOperationDTO exchangeOperationDTO) {
        ResponseEntity<Boolean> check = auditClient.check(exchangeOperationDTO);
        if (check.getStatusCode().is2xxSuccessful()) {
            if (Boolean.TRUE.equals(check.getBody())) {
                ResponseEntity<List<CurrencyDTO>> responseEntity = dictionaryClient.listCurrency();
                ResponseEntity<AccountDTO> sourceAccountResp = accountClient.getAccount(exchangeOperationDTO.getSourceAccountId());
                ResponseEntity<AccountDTO> targetAccountResp = accountClient.getAccount(exchangeOperationDTO.getTargetAccountId());
                return getResponseEntity(exchangeOperationDTO, responseEntity, sourceAccountResp, targetAccountResp);
            }
            return ResponseEntity.ok(ExchangeOperationResultDTO.builder().message("Operation " + exchangeOperationDTO.getOperationType() + " "
                    + exchangeOperationDTO.getAmount() + " "+ "prohibitted").status(OperationStatus.FAILED).build());
        }
        return ResponseEntity.ok(ExchangeOperationResultDTO.builder().message("Audit server is unavailable").status(OperationStatus.FAILED).build());
    }

    private ResponseEntity<ExchangeOperationResultDTO> getResponseEntity(ExchangeOperationDTO exchangeOperationDTO, ResponseEntity<List<CurrencyDTO>> responseEntity, ResponseEntity<AccountDTO> sourceAccountResp, ResponseEntity<AccountDTO> targetAccountResp) {
        if (responseEntity.getStatusCode().is2xxSuccessful()
                && sourceAccountResp.getStatusCode().is2xxSuccessful()
                && targetAccountResp.getStatusCode().is2xxSuccessful()) {


            List<CurrencyDTO> currencies = responseEntity.getBody();
            AccountDTO sourceAccount = sourceAccountResp.getBody();
            AccountDTO targetAccount = targetAccountResp.getBody();

            if (exchangeOperationDTO.getAmount().compareTo(sourceAccount.getBalance()) > 0) {
                throw new RuntimeException(exceptionsProperties.getInsufficientFundsFailure());
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

            BigDecimal convertedAmount = exchangeOperationDTO.getAmount()
                    .multiply(sourceCurrency.value())
                    .divide(targetCurrency.value(), 2, RoundingMode.HALF_UP);

            exchangeOperationDTO.setTargetAmount(convertedAmount);

            ExchangeOperation exchangeOperation = operationRepository.save(operationMapper.toEntity(exchangeOperationDTO));
            ResponseEntity<ExchangeOperationResultDTO> exchangedOperation = accountClient.exchangeOperation(exchangeOperationDTO);
            exchangeOperationDTO.setId(exchangeOperation.getId());
            if (exchangedOperation.getStatusCode().is2xxSuccessful()) {
                notificationKafkaProducer.send(NotificationDTO.builder()
                        .notificationDescription("Выполнена запрос на " + exchangeOperationDTO.getOperationType() + " " +
                                exchangeOperationDTO.getAmount() + " " +
                                sourceCurrency.name())
                                .userId(exchangeOperationDTO.getUserId())
                        .operationType(exchangeOperationDTO.getOperationType().name()).build());
            }

            return ResponseEntity.ok(exchangedOperation.getBody());

        } else {
            throw new RuntimeException(exceptionsProperties.getAccNotFoundFailure());
        }
    }

}
