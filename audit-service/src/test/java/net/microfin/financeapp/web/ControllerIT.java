package net.microfin.financeapp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.ServiceApplication;
import net.microfin.financeapp.client.RuleClient;
import net.microfin.financeapp.dto.AccountDTO;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.TransferOperationDTO;
import net.microfin.financeapp.repository.RuleRepository;
import net.microfin.financeapp.service.RuleService;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ActiveProfiles("test")
@SpringBootTest(classes = ServiceApplication.class)
@AutoConfigureMockMvc
public class ControllerIT extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private RuleClient ruleClient;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private RuleService ruleService;

    @Nested
    class RuleApiIntegrationTest extends AbstractTest {

        @Test
        void shouldCheckCashOperationWithinLimits() {
            var dto = CashOperationDTO.builder()
                    .amount(BigDecimal.valueOf(500))
                    .currencyCode(Currency.USD)
                    .operationType(OperationType.CASH_DEPOSIT)
                    .build();

            boolean result = ruleService.checkRulesForOperation(dto);
            assertThat(result).isTrue();
        }

        @Test
        void shouldCheckTransferOperationUsingAccountCurrency() {
            var dto = TransferOperationDTO.builder()
                    .sourceAccountId(1)
                    .amount(BigDecimal.valueOf(500))
                    .operationType(OperationType.TRANSFER)
                    .build();

            var accountDTO = AccountDTO.builder()
                    .id(1)
                    .currencyCode("USD")
                    .build();

            when(ruleClient.getAccount(1)).thenReturn(ResponseEntity.ok(accountDTO));

            boolean result = ruleService.checkRulesForOperation(dto);
            assertThat(result).isTrue();
        }

        @Test
        void shouldReturnFalseIfAccountNotFound() {
            var dto = ExchangeOperationDTO.builder()
                    .sourceAccountId(1)
                    .amount(BigDecimal.valueOf(500))
                    .operationType(OperationType.EXCHANGE)
                    .build();

            when(ruleClient.getAccount(1)).thenReturn(ResponseEntity.ok(null));

            boolean result = ruleService.checkRulesForOperation(dto);
            assertThat(result).isFalse();
        }
    }
}
