package net.microfin.financeapp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.ServiceApplication;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationResultDTO;
import net.microfin.financeapp.service.ExchangeOperationService;
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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = ServiceApplication.class)
@AutoConfigureMockMvc
public class ExchangeOperationApiIT extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExchangeOperationService exchangeOperationService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Nested
    class PerformExchangeOperationTest {

        @Test
        void shouldReturnSuccessResponseForValidExchangeOperation() throws Exception {
            ExchangeOperationDTO dto = ExchangeOperationDTO.builder()
                    .userId(1)
                    .sourceAccountId(10)
                    .targetAccountId(11)
                    .amount(BigDecimal.valueOf(150))
                    .operationType(OperationType.EXCHANGE)
                    .status(OperationStatus.PENDING)
                    .build();

            ExchangeOperationResultDTO result = ExchangeOperationResultDTO.builder()
                    .message("Exchange successful")
                    .status(OperationStatus.SENT)
                    .build();

            when(exchangeOperationService.performOperation(dto)).thenReturn(ResponseEntity.ok(result));

            var response = mockMvc.perform(post("/api/exchange/operation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("preferred_username", "user"))))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExchangeOperationResultDTO actual = objectMapper.readValue(response, ExchangeOperationResultDTO.class);
            assertThat(actual.getStatus()).isEqualTo(OperationStatus.SENT);
            assertThat(actual.getMessage()).isEqualTo("Exchange successful");
        }

        @Test
        void shouldReturnBadRequestForInvalidExchangeOperation() throws Exception {
            // Не указана сумма операции
            ExchangeOperationDTO dto = ExchangeOperationDTO.builder()
                    .userId(1)
                    .sourceAccountId(10)
                    .targetAccountId(11)
                    .operationType(OperationType.EXCHANGE)
                    .status(OperationStatus.PENDING)
                    .build();

            mockMvc.perform(post("/api/exchange/operation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("preferred_username", "user"))))
                    .andExpect(status().isBadRequest());
        }
    }
}
