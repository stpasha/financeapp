package net.microfin.financeapp.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.microfin.financeapp.client.AccountClientImpl;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.repository.RuleRepository;
import net.microfin.financeapp.util.Currency;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DefaultRuleService implements RuleService {

    private final RuleRepository ruleRepository;
    private final AccountClientImpl accountClient;
    private final MeterRegistry meterRegistry;
    @Setter
    @Value("${testPrometheus}")
    private boolean testPrometheus;

    @Override
    public boolean checkRulesForOperation(GenericOperationDTO genericOperationDTO) {
        boolean isAllowed = performCheck(genericOperationDTO);
        if (!isAllowed && testPrometheus) {
            updateMetric(genericOperationDTO);
        }
        return isAllowed;
    }

    private void updateMetric(GenericOperationDTO genericOperationDTO) {
        if (genericOperationDTO instanceof TransferOperationDTO transferOperationDTO) {
            meterRegistry.counter("financeapp_failed_audit_total",
                    "userId", String.valueOf(transferOperationDTO.getUserId()),
                    "sourceAccountId", String.valueOf(transferOperationDTO.getSourceAccountId()),
                    "targetAccountId", String.valueOf(transferOperationDTO.getTargetAccountId())
            ).increment();
        } else if (genericOperationDTO instanceof ExchangeOperationDTO exchangeOperationDTO) {
            meterRegistry.counter("financeapp_failed_audit_total",
                    "userId", String.valueOf(exchangeOperationDTO.getUserId()),
                    "sourceAccountId", String.valueOf(exchangeOperationDTO.getSourceAccountId()),
                    "targetAccountId", String.valueOf(exchangeOperationDTO.getTargetAccountId())
            ).increment();
        } else if (genericOperationDTO instanceof CashOperationDTO cashOperationDTO) {
            meterRegistry.counter("financeapp_failed_audit_total",
                    "userId", String.valueOf(cashOperationDTO.getUserId()),
                    "sourceAccountId", String.valueOf(cashOperationDTO.getAccountId()),
                    "targetAccountId", ""
            ).increment();
        }

    }

    private boolean performCheck(GenericOperationDTO genericOperationDTO) {
        if (genericOperationDTO instanceof CashOperationDTO cashOperationDTO) {
            return ruleRepository
                    .findRuleByOperationTypeAndCurrencyCode(cashOperationDTO.getOperationType(), cashOperationDTO.getCurrencyCode())
                    .map(rule -> checkAmounts(rule.getMinAmount(), rule.getMaxAmount(), cashOperationDTO.getAmount())).orElse(true);
        } else if (genericOperationDTO instanceof TransferOperationDTO transferOperationDTO) {
            ResponseEntity<AccountDTO> sourceAccount = accountClient.getAccount(transferOperationDTO.getSourceAccountId());
            if (sourceAccount.getStatusCode().is2xxSuccessful()) {
                if (sourceAccount.getBody() == null) {
                    return false;
                }
                return ruleRepository
                        .findRuleByOperationTypeAndCurrencyCode(transferOperationDTO.getOperationType(),
                                Currency.valueOf(Currency.class, sourceAccount.getBody().getCurrencyCode()))
                        .map(rule -> checkAmounts(rule.getMinAmount(), rule.getMaxAmount(), transferOperationDTO.getAmount())).orElse(true);
            }
            return false;
        } else if (genericOperationDTO instanceof ExchangeOperationDTO exchangeOperationDTO) {
            ResponseEntity<AccountDTO> sourceAccount = accountClient.getAccount(exchangeOperationDTO.getSourceAccountId());
            if (sourceAccount.getStatusCode().is2xxSuccessful()) {
                if (sourceAccount.getBody() == null) {
                    return false;
                }
                return ruleRepository
                        .findRuleByOperationTypeAndCurrencyCode(exchangeOperationDTO.getOperationType(),
                                Currency.valueOf(Currency.class, sourceAccount.getBody().getCurrencyCode()))
                        .map(rule -> checkAmounts(rule.getMinAmount(), rule.getMaxAmount(), exchangeOperationDTO.getAmount())).orElse(true);
            }
        }
        return false;
    }

    private boolean checkAmounts(BigDecimal min, BigDecimal max, BigDecimal amount) {
        return max.compareTo(amount) > 0 && min.compareTo(amount) < 0;
    }

}
