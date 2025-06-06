package net.microfin.financeapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.ExchangeOperationClient;
import net.microfin.financeapp.domain.ExchangeOperation;
import net.microfin.financeapp.dto.AccountDTO;
import net.microfin.financeapp.dto.CurrencyDTO;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationResultDTO;
import net.microfin.financeapp.mapper.ExchangeOperationMapper;
import net.microfin.financeapp.repository.ExchangeOperationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultExchangeOperationService implements ExchangeOperationService {

    private final ExchangeOperationClient operationClient;
    private final ExchangeOperationMapper operationMapper;
    private final ExchangeOperationRepository operationRepository;

    @Override
    @Transactional
    public ResponseEntity<ExchangeOperationResultDTO> performOperation(ExchangeOperationDTO exchangeOperationDTO) {
        ResponseEntity<List<CurrencyDTO>> responseEntity = operationClient.listCurrency();
        ResponseEntity<AccountDTO> sourceAccountResp = operationClient.getAccount(exchangeOperationDTO.getSourceAccountId());
        ResponseEntity<AccountDTO> targetAccountResp = operationClient.getAccount(exchangeOperationDTO.getTargetAccountId());

        if (responseEntity.getStatusCode().is2xxSuccessful()
                && sourceAccountResp.getStatusCode().is2xxSuccessful()
                && targetAccountResp.getStatusCode().is2xxSuccessful()) {


            List<CurrencyDTO> currencies = responseEntity.getBody();
            AccountDTO sourceAccount = sourceAccountResp.getBody();
            AccountDTO targetAccount = targetAccountResp.getBody();

            if (exchangeOperationDTO.getAmount().compareTo(sourceAccount.getBalance()) > 0) {
                throw new RuntimeException("Insufficient funds");
            }
            Optional<CurrencyDTO> sourceCurrencyOpt = currencies.stream()
                    .filter(c -> c.code().equals(sourceAccount.getCurrencyCode()))
                    .findFirst();

            Optional<CurrencyDTO> targetCurrencyOpt = currencies.stream()
                    .filter(c -> c.code().equals(targetAccount.getCurrencyCode()))
                    .findFirst();

            if (sourceCurrencyOpt.isEmpty() || targetCurrencyOpt.isEmpty()) {
                throw new RuntimeException("Currency info not found");
            }

            CurrencyDTO sourceCurrency = sourceCurrencyOpt.get();
            CurrencyDTO targetCurrency = targetCurrencyOpt.get();

            BigDecimal convertedAmount = exchangeOperationDTO.getAmount()
                    .multiply(sourceCurrency.value())
                    .divide(targetCurrency.value(), 2, RoundingMode.HALF_UP);

            exchangeOperationDTO.setTargetAmount(convertedAmount);

            ExchangeOperation exchangeOperation = operationRepository.save(operationMapper.toEntity(exchangeOperationDTO));
            exchangeOperationDTO.setId(exchangeOperation.getId());

            return operationClient.exchangeOperation(exchangeOperationDTO);

        } else {
            throw new RuntimeException("Unable to get currency or account info");
        }
    }

}
