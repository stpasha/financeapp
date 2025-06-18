package net.microfin.financeapp.service;

import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.FinanceAppTest;
import net.microfin.financeapp.client.TransferOperationClient;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.mapper.TransferOperationMapper;
import net.microfin.financeapp.repository.TransferOperationRepository;
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
public class DefaultTransferOperationServiceTest extends AbstractTest {

    @Autowired
    private DefaultTransferOperationService service;

    @MockitoBean
    private TransferOperationClient operationClient;

    @Autowired
    private TransferOperationMapper mapper;

    @Autowired
    private TransferOperationRepository repository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldPerformTransferSuccessfully() {
        // given
        TransferOperationDTO dto = TransferOperationDTO.builder()
                .userId(1)
                .sourceAccountId(101)
                .targetAccountId(202)
                .amount(BigDecimal.valueOf(100))
                .operationType(OperationType.TRANSFER)
                .status(OperationStatus.PENDING)
                .build();

        AccountDTO sourceAccount = AccountDTO.builder()
                .id(101)
                .balance(BigDecimal.valueOf(500))
                .currencyCode(Currency.USD.name())
                .user(UserDTO.builder().id(1).build())
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

        TransferOperationResultDTO resultDTO = TransferOperationResultDTO.builder()
                .status(OperationStatus.SENT)
                .message("Transfer completed successfully")
                .build();

        // when
        when(operationClient.check(dto)).thenReturn(ResponseEntity.ok(true));
        when(operationClient.listCurrency()).thenReturn(ResponseEntity.ok(currencies));
        when(operationClient.getAccount(101)).thenReturn(ResponseEntity.ok(sourceAccount));
        when(operationClient.getAccount(202)).thenReturn(ResponseEntity.ok(targetAccount));
        when(operationClient.transferOperation(dto)).thenReturn(ResponseEntity.ok(resultDTO));

        // then
        TransferOperationResultDTO response = service.performOperation(dto);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SENT);
        assertThat(dto.getTargetAmount()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
    }
}
