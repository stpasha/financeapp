package net.microfin.financeapp.service;

import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.FinanceAppTest;
import net.microfin.financeapp.client.AccountClientImpl;
import net.microfin.financeapp.client.AuditClientImpl;
import net.microfin.financeapp.client.NotificationClientImpl;
import net.microfin.financeapp.domain.CashOperation;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.CashOperationResultDTO;
import net.microfin.financeapp.dto.NotificationDTO;
import net.microfin.financeapp.mapper.CashOperationMapper;
import net.microfin.financeapp.repository.CashOperationRepository;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@FinanceAppTest
public class DefaultCashOperationServiceTest extends AbstractTest {

    @MockitoBean
    private AuditClientImpl auditClient;
    @MockitoBean
    private NotificationClientImpl notificationClient;
    @MockitoBean
    private AccountClientImpl accountClient;


    @MockitoBean
    private CashOperationMapper cashOperationMapper;

    @MockitoBean
    private CashOperationRepository cashOperationRepository;

    @Autowired
    private DefaultCashOperationService cashOperationService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Nested
    class PerformOperationTests {

        @Test
        void shouldPerformCashOperationSuccessfully() {
            CashOperationDTO dto = new CashOperationDTO();
            dto.setAmount(BigDecimal.valueOf(100));
            dto.setCurrencyCode(Currency.RUB);
            dto.setOperationType(OperationType.CASH_WITHDRAWAL);
            dto.setUserId(123);

            CashOperation entity = new CashOperation();
            entity.setId(42);

            CashOperationResultDTO resultDTO = CashOperationResultDTO.builder()
                    .status(OperationStatus.SENT)
                    .message("OK")
                    .build();

            when(auditClient.check(dto)).thenReturn(ResponseEntity.ok(true));
            when(cashOperationMapper.toEntity(dto)).thenReturn(entity);
            when(cashOperationRepository.save(entity)).thenReturn(entity);
            when(accountClient.cashOperation(dto)).thenReturn(ResponseEntity.ok(resultDTO));


            ResponseEntity<CashOperationResultDTO> result = cashOperationService.performOperation(dto);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getStatus()).isEqualTo(OperationStatus.SENT);

            ArgumentCaptor<NotificationDTO> captor = ArgumentCaptor.forClass(NotificationDTO.class);
            verify(notificationClient).saveNotification(captor.capture());

            NotificationDTO notification = captor.getValue();
            assertThat(notification.getUserId()).isEqualTo(123);
            assertThat(notification.getOperationType()).isEqualTo("CASH_WITHDRAWAL");
            assertThat(notification.getNotificationDescription()).contains("100", "Рубль");
        }

        @Test
        void shouldReturnFailureWhenCheckFails() {
            CashOperationDTO dto = new CashOperationDTO();
            dto.setAmount(BigDecimal.valueOf(100));
            dto.setCurrencyCode(Currency.USD);
            dto.setOperationType(OperationType.CASH_DEPOSIT);

            when(auditClient.check(dto)).thenReturn(ResponseEntity.ok(false));

            ResponseEntity<CashOperationResultDTO> result = cashOperationService.performOperation(dto);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getStatus()).isEqualTo(OperationStatus.FAILED);
            assertThat(result.getBody().getMessage()).contains("prohibitted");

            verifyNoInteractions(cashOperationMapper, cashOperationRepository);
        }

        @Test
        void shouldReturnUnavailableWhenCheckCallFails() {
            CashOperationDTO dto = new CashOperationDTO();
            dto.setAmount(BigDecimal.valueOf(50));
            dto.setCurrencyCode(Currency.EUR);
            dto.setOperationType(OperationType.CASH_WITHDRAWAL);

            when(auditClient.check(dto)).thenReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());

            ResponseEntity<CashOperationResultDTO> result = cashOperationService.performOperation(dto);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getStatus()).isEqualTo(OperationStatus.FAILED);
            assertThat(result.getBody().getMessage()).isEqualTo("Server unavailable.");

            verifyNoInteractions(cashOperationMapper, cashOperationRepository);
        }
    }

}
