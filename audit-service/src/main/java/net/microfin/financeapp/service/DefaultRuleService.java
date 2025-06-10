package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.RuleClient;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.repository.RuleRepository;
import net.microfin.financeapp.util.Currency;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DefaultRuleService implements RuleService {

    private final RuleRepository ruleRepository;
    private final RuleClient ruleClient;

    @Override
    public boolean checkRulesForOperation(GenericOperationDTO genericOperationDTO) {
        if (genericOperationDTO instanceof CashOperationDTO cashOperationDTO) {
            return ruleRepository
                    .findRuleByOperationTypeAndCurrencyCode(cashOperationDTO.getOperationType(), cashOperationDTO.getCurrencyCode())
                    .map(rule -> checkAmounts(rule.getMinAmount(), rule.getMaxAmount(), cashOperationDTO.getAmount())).orElse(true);
        } else if (genericOperationDTO instanceof TransferOperationDTO transferOperationDTO) {
            ResponseEntity<AccountDTO> sourceAccount = ruleClient.getAccount(transferOperationDTO.getSourceAccountId());
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
            ResponseEntity<AccountDTO> sourceAccount = ruleClient.getAccount(exchangeOperationDTO.getSourceAccountId());
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
