package net.microfin.financeapp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.ServiceApplication;
import net.microfin.financeapp.config.KeyCloakConfig;
import net.microfin.financeapp.config.TestConfig;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.service.KeycloakUserService;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(classes = ServiceApplication.class)
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class ControllerIT extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KeycloakUserService keycloakUserService;
    @MockitoBean
    private KeyCloakConfig keyCloakConfig;
    @MockitoBean
    private UsersResource usersResource;
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Nested
    class AccountApiIntegrationTest extends AbstractTest {

        @Test
        void testGetAccountsByUser() throws Exception {
            int userId = 1;

            mockMvc.perform(get("/api/account/user/{userId}", userId).with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("preferred_username", "user"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").isNotEmpty());
        }

        @Test
        void testGetAccount() throws Exception {
            int accountId = 1;

            mockMvc.perform(get("/api/account/{id}", accountId).with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("preferred_username", "user"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(accountId));
        }


        @Test
        void testPerformCashDepositOperation() throws Exception {
            CashOperationDTO cash = CashOperationDTO.builder()
                    .accountId(UUID.randomUUID())
                    .userId(UUID.randomUUID())
                    .operationType(OperationType.CASH_DEPOSIT)
                    .currencyCode(Currency.USD)
                    .amount(BigDecimal.valueOf(500))
                    .status(OperationStatus.SENT)
                    .build();

            String json = objectMapper.writeValueAsString(cash);

            String response = mockMvc.perform(post("/api/account/operation").with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("preferred_username", "user")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CashOperationResultDTO result = objectMapper.readValue(response, CashOperationResultDTO.class);
            assertThat(result.getStatus()).isIn(OperationStatus.SENT);
        }

        @Test
        void testPerformExchangeOperation() throws Exception {
            ExchangeOperationDTO exchange = ExchangeOperationDTO.builder()
                    .sourceAccountId(UUID.randomUUID())
                    .targetAccountId(UUID.randomUUID())
                    .userId(UUID.randomUUID())
                    .amount(BigDecimal.valueOf(100))
                    .operationType(OperationType.EXCHANGE)
                    .status(OperationStatus.SENT)
                    .build();

            String json = objectMapper.writeValueAsString(exchange);

            String response = mockMvc.perform(post("/api/account/operation")
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("preferred_username", "user")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExchangeOperationResultDTO result = objectMapper.readValue(response, ExchangeOperationResultDTO.class);
            assertThat(result.getStatus()).isIn(OperationStatus.SENT);
        }

        @Test
        void testPerformTransferOperation() throws Exception {
            TransferOperationDTO transfer = TransferOperationDTO.builder()
                    .sourceAccountId(UUID.randomUUID())
                    .targetAccountId(UUID.randomUUID())
                    .userId(UUID.randomUUID())
                    .amount(BigDecimal.valueOf(200))
                    .operationType(OperationType.TRANSFER)
                    .status(OperationStatus.SENT)
                    .build();

            String json = objectMapper.writeValueAsString(transfer);

            String response = mockMvc.perform(post("/api/account/operation")
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("preferred_username", "user")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            TransferOperationResultDTO result = objectMapper.readValue(response, TransferOperationResultDTO.class);
            assertThat(result.getStatus()).isIn(OperationStatus.SENT);
        }


    }
}
