package net.microfin.financeapp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.ServiceApplication;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.CashOperationResultDTO;
import net.microfin.financeapp.service.CashOperationService;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = ServiceApplication.class)
@AutoConfigureMockMvc
public class CashOperationApiIT extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CashOperationService cashOperationService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Nested
    class PerformOperationTest {

        @Test
        void shouldReturnSuccessResponseForValidOperation() throws Exception {

            CashOperationDTO dto = CashOperationDTO.builder()
                    .userId(UUID.randomUUID())
                    .accountId(UUID.randomUUID())
                    .amount(BigDecimal.valueOf(100))
                    .currencyCode(Currency.USD)
                    .operationType(OperationType.CASH_DEPOSIT)
                    .status(OperationStatus.PENDING)
                    .build();

            CashOperationResultDTO result = CashOperationResultDTO.builder()
                    .message("Success")
                    .status(OperationStatus.SENT)
                    .build();

            when(cashOperationService.performOperation(dto)).thenReturn(ResponseEntity.ok(result));

            var response = mockMvc.perform(post("/api/cash/operation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("preferred_username", "user"))))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CashOperationResultDTO actual = objectMapper.readValue(response, CashOperationResultDTO.class);
            assertThat(actual.getStatus()).isEqualTo(OperationStatus.SENT);
        }

        @Test
        void shouldReturnBadRequestForInvalidInput() throws Exception {
            // amount не указан
            CashOperationDTO dto = CashOperationDTO.builder()
                    .userId(UUID.randomUUID())
                    .accountId(UUID.randomUUID())
                    .currencyCode(Currency.EUR)
                    .operationType(OperationType.CASH_WITHDRAWAL)
                    .status(OperationStatus.PENDING)
                    .build();

            mockMvc.perform(post("/api/cash/operation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)).with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("preferred_username", "user"))))
                    .andExpect(status().isBadRequest());
        }
    }
}
