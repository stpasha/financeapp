package net.microfin.financeapp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.ServiceApplication;
import net.microfin.financeapp.dto.TransferOperationDTO;
import net.microfin.financeapp.dto.TransferOperationResultDTO;
import net.microfin.financeapp.service.TransferOperationService;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
public class TransferOperationApiIT extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransferOperationService transferOperationService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Nested
    class PerformTransferOperationTest {

        @Test
        void shouldReturnSuccessResponseForValidTransferOperation() throws Exception {
            TransferOperationDTO dto = TransferOperationDTO.builder()
                    .userId(1)
                    .sourceAccountId(100)
                    .targetAccountId(101)
                    .amount(BigDecimal.valueOf(200))
                    .operationType(OperationType.TRANSFER)
                    .status(OperationStatus.PENDING)
                    .build();

            TransferOperationResultDTO result = TransferOperationResultDTO.builder()
                    .message("Transfer completed")
                    .status(OperationStatus.SENT)
                    .build();

            when(transferOperationService.performOperation(dto)).thenReturn(result);

            var response = mockMvc.perform(post("/api/transfer/operation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("preferred_username", "user"))))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            TransferOperationResultDTO actual = objectMapper.readValue(response, TransferOperationResultDTO.class);
            assertThat(actual.getStatus()).isEqualTo(OperationStatus.SENT);
            assertThat(actual.getMessage()).isEqualTo("Transfer completed");
        }

        @Test
        void shouldReturnBadRequestForInvalidTransferOperation() throws Exception {
            TransferOperationDTO dto = TransferOperationDTO.builder()
                    .userId(1)
                    .sourceAccountId(100)
                    .targetAccountId(101)
                    .operationType(OperationType.TRANSFER)
                    .status(OperationStatus.PENDING)
                    .build();

            mockMvc.perform(post("/api/transfer/operation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("preferred_username", "user"))))
                    .andExpect(status().isBadRequest());
        }
    }
}
