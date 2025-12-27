package net.microfin.financeapp.service;

import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.FinanceAppTest;
import net.microfin.financeapp.client.AccountClientImpl;
import net.microfin.financeapp.client.AuditClientImpl;
import net.microfin.financeapp.client.NotificationClientImpl;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.mapper.ExchangeOperationMapper;
import net.microfin.financeapp.repository.ExchangeOperationRepository;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@FinanceAppTest
@EmbeddedKafka(
        topics = {"input-exchange", "input-notification"}
)
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
    private JwtDecoder jwtDecoder;

    @Autowired
    KafkaTemplate<String, CurrencyDTO[]> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void shouldPerformExchangeSuccessfully() {
        // given
        UUID sourceAccountId = UUID.randomUUID();
        UUID targetAccountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ExchangeOperationDTO dto = ExchangeOperationDTO.builder()
                .userId(userId)
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(BigDecimal.valueOf(100))
                .operationType(OperationType.EXCHANGE)
                .status(OperationStatus.PENDING)
                .build();

        AccountDTO sourceAccount = AccountDTO.builder()
                .id(sourceAccountId)
                .balance(BigDecimal.valueOf(500))
                .currencyCode(Currency.USD.name())
                .build();

        AccountDTO targetAccount = AccountDTO.builder()
                .id(targetAccountId)
                .balance(BigDecimal.valueOf(300))
                .currencyCode(Currency.EUR.name())
                .build();


        ExchangeOperationResultDTO resultDTO = ExchangeOperationResultDTO.builder()
                .status(OperationStatus.SENT)
                .message("Success")
                .build();
        CompletableFuture<SendResult<String, CurrencyDTO[]>> send = kafkaTemplate.send("input-exchange", Arrays.stream(Currency.values())
                .map(currency -> new CurrencyDTO(currency.getName(),
                        currency.name(),
                        Currency.RUB.equals(currency) ? BigDecimal.ONE :
                                BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(80, 100))
                                        .setScale(2, RoundingMode.HALF_DOWN))).toArray(CurrencyDTO[]::new));
        // when
        when(auditClient.check(dto)).thenReturn(ResponseEntity.ok(true));
        when(accountClient.getAccount(sourceAccountId)).thenReturn(ResponseEntity.ok(sourceAccount));
        when(accountClient.getAccount(targetAccountId)).thenReturn(ResponseEntity.ok(targetAccount));
        when(accountClient.exchangeOperation(dto)).thenReturn(ResponseEntity.ok(resultDTO));

        // then
        send.thenAccept(result -> {
            ResponseEntity<ExchangeOperationResultDTO> response = service.performOperation(dto);
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(OperationStatus.SENT);
            assertThat(dto.getTargetAmount()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
            try (var consumerForTest = new DefaultKafkaConsumerFactory<>(
                    KafkaTestUtils.consumerProps("notification-group", "false", embeddedKafkaBroker),
                    new UUIDDeserializer(),
                    new JsonDeserializer<>()
            ).createConsumer()) {
                consumerForTest.subscribe(List.of("input-notification"));
                var inputMessage = KafkaTestUtils.getSingleRecord(consumerForTest, "input-notification", Duration.ofSeconds(5));
                assertThat(inputMessage.key()).isEqualTo(dto.getId());
                assertThat(inputMessage.value()).isInstanceOf(NotificationDTO.class);
            }
        });

    }
}
