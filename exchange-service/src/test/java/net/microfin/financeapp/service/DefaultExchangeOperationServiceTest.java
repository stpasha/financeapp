package net.microfin.financeapp.service;

import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.FinanceAppTest;
import net.microfin.financeapp.client.AccountClientImpl;
import net.microfin.financeapp.client.AuditClientImpl;
import net.microfin.financeapp.client.DictionaryClientImpl;
import net.microfin.financeapp.client.NotificationClientImpl;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.mapper.ExchangeOperationMapper;
import net.microfin.financeapp.repository.ExchangeOperationRepository;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@FinanceAppTest
public class DefaultExchangeOperationServiceTest extends AbstractTest {

    @Autowired
    private DefaultExchangeOperationService service;

    @MockitoBean
    private AuditClientImpl auditClient;
    @MockitoBean
    private NotificationClientImpl notificationClient;
    @MockitoBean
    private AccountClientImpl accountClient;
    @MockitoBean
    private DictionaryClientImpl dictionaryClient;

    @Autowired
    private ExchangeOperationMapper mapper;

    @Autowired
    private ExchangeOperationRepository repository;

    @MockitoBean
    private JwtDecoder jwtDecoder;


    @Test
    void shouldPerformExchangeSuccessfully() {
        // given
        ExchangeOperationDTO dto = ExchangeOperationDTO.builder()
                .userId(1)
                .sourceAccountId(101)
                .targetAccountId(202)
                .amount(BigDecimal.valueOf(100))
                .operationType(OperationType.EXCHANGE)
                .status(OperationStatus.PENDING)
                .build();

        AccountDTO sourceAccount = AccountDTO.builder()
                .id(101)
                .balance(BigDecimal.valueOf(500))
                .currencyCode(Currency.USD.name())
                .build();

        AccountDTO targetAccount = AccountDTO.builder()
                .id(202)
                .balance(BigDecimal.valueOf(300))
                .currencyCode(Currency.EUR.name())
                .build();

        List<CurrencyDTO> currencies = List.of(
                new CurrencyDTO("Доллар", Currency.USD.name(), BigDecimal.valueOf(90.00)),
                new CurrencyDTO("Евро", Currency.EUR.name(), BigDecimal.valueOf(100.00))
        );

        ExchangeOperationResultDTO resultDTO = ExchangeOperationResultDTO.builder()
                .status(OperationStatus.SENT)
                .message("Success")
                .build();

        // when
        when(auditClient.check(dto)).thenReturn(ResponseEntity.ok(true));
        when(dictionaryClient.listCurrency()).thenReturn(ResponseEntity.ok(currencies));
        when(accountClient.getAccount(101)).thenReturn(ResponseEntity.ok(sourceAccount));
        when(accountClient.getAccount(202)).thenReturn(ResponseEntity.ok(targetAccount));
        when(accountClient.exchangeOperation(dto)).thenReturn(ResponseEntity.ok(resultDTO));

        // then
        ResponseEntity<ExchangeOperationResultDTO> response = service.performOperation(dto);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(OperationStatus.SENT);
        assertThat(dto.getTargetAmount()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
    }
}
