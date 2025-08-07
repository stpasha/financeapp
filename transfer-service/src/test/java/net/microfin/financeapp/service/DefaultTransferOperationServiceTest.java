package net.microfin.financeapp.service;

import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.FinanceAppTest;
import net.microfin.financeapp.client.AccountClientImpl;
import net.microfin.financeapp.client.AuditClientImpl;
import net.microfin.financeapp.client.DictionaryClientImpl;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.mapper.TransferOperationMapper;
import net.microfin.financeapp.repository.TransferOperationRepository;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@FinanceAppTest
@EmbeddedKafka(
        topics = {"input-notification"}
)
public class DefaultTransferOperationServiceTest extends AbstractTest {

    @Autowired
    private DefaultTransferOperationService service;

    @MockitoBean
    private AuditClientImpl auditClient;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @MockitoBean
    private AccountClientImpl accountClient;
    @MockitoBean
    private DictionaryClientImpl dictionaryClient;

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
        when(auditClient.check(dto)).thenReturn(ResponseEntity.ok(true));
        when(dictionaryClient.listCurrency()).thenReturn(ResponseEntity.ok(currencies));
        when(accountClient.getAccount(101)).thenReturn(ResponseEntity.ok(sourceAccount));
        when(accountClient.getAccount(202)).thenReturn(ResponseEntity.ok(targetAccount));
        when(accountClient.transferOperation(dto)).thenReturn(ResponseEntity.ok(resultDTO));

        // then
        TransferOperationResultDTO response = service.performOperation(dto);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SENT);
        assertThat(dto.getTargetAmount()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("notification-group", "false", embeddedKafkaBroker);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "net.microfin.financeapp.dto");
        try (var consumerForTest = new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new IntegerDeserializer(),
                new JsonDeserializer<>()
        ).createConsumer()) {
            consumerForTest.subscribe(List.of("input-notification"));
            var inputMessage = KafkaTestUtils.getSingleRecord(consumerForTest, "input-notification", Duration.ofSeconds(5));
            assertThat(inputMessage.key()).isEqualTo(dto.getUserId());
            assertThat(inputMessage.value()).isInstanceOf(NotificationDTO.class);
            assertThat(((NotificationDTO)inputMessage.value()).getOperationType()).isEqualTo(dto.getOperationType().name());
        }
    }
}
