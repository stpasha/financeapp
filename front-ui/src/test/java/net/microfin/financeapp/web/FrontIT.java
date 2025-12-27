package net.microfin.financeapp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.microfin.financeapp.ServiceFrontUIApplication;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.service.AccountService;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(classes = ServiceFrontUIApplication.class)
@AutoConfigureMockMvc
public class FrontIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private OAuth2AuthorizedClientRepository authClients;

    @Nested
    class AccountControllerIT {

        @Test
        void shouldRedirectWithInfoForValidCashOperation() throws Exception {
            UUID userId = UUID.randomUUID();
            CashOperationDTO dto = CashOperationDTO.builder()
                    .userId(userId)
                    .amount(BigDecimal.valueOf(100))
                    .currencyCode(Currency.USD)
                    .operationType(OperationType.CASH_WITHDRAWAL)
                    .status(OperationStatus.PENDING)
                    .build();

            CashOperationResultDTO result = CashOperationResultDTO.builder()
                    .operationId(UUID.randomUUID())
                    .newBalance(BigDecimal.valueOf(1100))
                    .status(OperationStatus.SENT)
                    .message("Cash ok")
                    .build();

            when(accountService.createCashOperation(dto)).thenReturn(result);

            mockMvc.perform(post("/account/" + userId + "/cash")
                            .flashAttr("cashDTO", dto)
                    .with(csrf())
                    .with(oauth2Login()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/profile?info=Cash ok"));
        }

        @Test
        void shouldRedirectWithErrorForFailedExchangeOperation() throws Exception {
            UUID userId = UUID.randomUUID();
            ExchangeOperationDTO dto = ExchangeOperationDTO.builder()
                    .userId(userId)
                    .sourceAccountId(UUID.randomUUID())
                    .targetAccountId(UUID.randomUUID())
                    .amount(BigDecimal.valueOf(100))
                    .operationType(OperationType.EXCHANGE)
                    .status(OperationStatus.FAILED)
                    .build();

            ExchangeOperationResultDTO result = ExchangeOperationResultDTO.builder()
                    .operationId(UUID.randomUUID())
                    .status(OperationStatus.FAILED)
                    .message("Exchange failed")
                    .build();

            when(accountService.createExchangeOperation(dto)).thenReturn(result);

            mockMvc.perform(post("/account/" + userId + "/exchange")
                            .flashAttr("exchangeDTO", dto)
                    .with(csrf())
                    .with(oauth2Login()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/profile?err=Exchange failed"));
        }

        @Test
        void shouldRedirectWithInfoForSuccessfulTransfer() throws Exception {
            UUID userId = UUID.randomUUID();
            TransferOperationDTO dto = TransferOperationDTO.builder()
                    .userId(userId)
                    .sourceAccountId(UUID.randomUUID())
                    .targetAccountId(UUID.randomUUID())
                    .amount(BigDecimal.valueOf(500))
                    .operationType(OperationType.TRANSFER)
                    .status(OperationStatus.PENDING)
                    .build();

            TransferOperationResultDTO result = TransferOperationResultDTO.builder()
                    .operationId(UUID.randomUUID())
                    .status(OperationStatus.SENT)
                    .message("Transferred")
                    .build();

            when(accountService.createTransferOperation(dto)).thenReturn(result);

            mockMvc.perform(post("/account/" + userId + "/transfer")
                            .flashAttr("transferDTO", dto)
                            .with(csrf())
                            .with(oauth2Login()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/profile?info=Transferred"));
        }
    }

    @Test
    void shouldReturnAccountsForUserId() throws Exception {
        UUID id = UUID.randomUUID();
        List<AccountDTO> accounts = List.of(AccountDTO.builder()
                .id(id)
                .balance(BigDecimal.TEN)
                .build());

        when(accountService.getAccountsByUser(id)).thenReturn(accounts);

        var response = mockMvc.perform(get("/account/" + id)
                        .with(csrf())
                        .with(oauth2Login()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<AccountDTO> actual = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, AccountDTO.class));
        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst().getBalance()).isEqualByComparingTo(BigDecimal.TEN);
    }
}
