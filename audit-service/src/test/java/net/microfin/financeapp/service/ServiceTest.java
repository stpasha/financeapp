package net.microfin.financeapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.FinanceAppTest;
import net.microfin.financeapp.client.AccountClientImpl;
import net.microfin.financeapp.domain.Rule;
import net.microfin.financeapp.dto.AccountDTO;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.TransferOperationDTO;
import net.microfin.financeapp.repository.RuleRepository;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@FinanceAppTest
public class ServiceTest extends AbstractTest {

    @MockitoBean
    private AccountClientImpl accountClient;
    @Autowired
    private DefaultRuleService ruleService;
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Nested
    class DefaultRuleServiceTest extends AbstractTest {

        @Test
        void testCheckRulesForCashOperation_Pass() {
            CashOperationDTO dto = new CashOperationDTO();
            dto.setAmount(new BigDecimal("500"));
            dto.setCurrencyCode(Currency.RUB);
            dto.setOperationType(OperationType.CASH_DEPOSIT);

            assertTrue(ruleService.checkRulesForOperation(dto));
        }

        @Test
        void testCheckRulesForTransferOperation_Pass() {
            TransferOperationDTO dto = new TransferOperationDTO();
            dto.setAmount(new BigDecimal("150"));
            dto.setOperationType(OperationType.TRANSFER);
            UUID accountId = UUID.randomUUID();
            dto.setSourceAccountId(accountId);

            AccountDTO accountDTO = new AccountDTO();
            accountDTO.setCurrencyCode("USD");

            Rule rule = new Rule();
            rule.setMinAmount(new BigDecimal("100"));
            rule.setMaxAmount(new BigDecimal("500"));

            when(accountClient.getAccount(accountId)).thenReturn(ResponseEntity.ok(accountDTO));
            assertTrue(ruleService.checkRulesForOperation(dto));
        }

        @Test
        void testCheckRulesForExchangeOperation_Fail_NullAccountBody() {
            ExchangeOperationDTO dto = new ExchangeOperationDTO();
            dto.setAmount(new BigDecimal("200"));
            dto.setOperationType(OperationType.EXCHANGE);
            UUID accountId = UUID.randomUUID();
            dto.setSourceAccountId(accountId);

            when(accountClient.getAccount(accountId)).thenReturn(ResponseEntity.ok(null));

            assertFalse(ruleService.checkRulesForOperation(dto));
        }

        @Test
        void testCheckRulesForCashOperation_NoRule_DefaultTrue() {
            CashOperationDTO dto = new CashOperationDTO();
            dto.setAmount(new BigDecimal("1000"));
            dto.setCurrencyCode(Currency.USD);
            dto.setOperationType(OperationType.CASH_WITHDRAWAL);

            assertTrue(ruleService.checkRulesForOperation(dto));
        }

        @Test
        void testCheckRulesForTransferOperation_Non2xxResponse() {
            TransferOperationDTO dto = new TransferOperationDTO();
            dto.setAmount(new BigDecimal("150"));
            dto.setOperationType(OperationType.TRANSFER);
            UUID accountId = UUID.randomUUID();
            dto.setSourceAccountId(accountId);

            when(accountClient.getAccount(accountId)).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

            assertFalse(ruleService.checkRulesForOperation(dto));
        }
    }



}