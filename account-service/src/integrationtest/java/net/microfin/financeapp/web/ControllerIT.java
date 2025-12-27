package net.microfin.financeapp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import net.microfin.financeapp.AbstractIT;
import net.microfin.financeapp.ServiceApplication;
import net.microfin.financeapp.config.TestConfig;
import net.microfin.financeapp.config.WireMockTestConfig;
import net.microfin.financeapp.domain.Account;
import net.microfin.financeapp.domain.User;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.CashOperationResultDTO;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationResultDTO;
import net.microfin.financeapp.repository.AccountRepository;
import net.microfin.financeapp.repository.UserRepository;
import net.microfin.financeapp.service.KeycloakUserService;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integrationtest")
@SpringBootTest(classes = ServiceApplication.class)
@AutoConfigureMockMvc
@Import({TestConfig.class, WireMockTestConfig.class})
//@ImportAutoConfiguration(exclude = KeyCloakConfig.class)
public class ControllerIT extends AbstractIT {

    @MockitoBean
    private Keycloak keycloak;

    @MockitoBean
    private KeycloakUserService keycloakUserService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WireMockServer wireMockServer;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private User savedUser;

    private Account accountFirstRUB;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeMethod
    void setup() {
        accountRepository.deleteAll();
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setUsername("user1");
        testUser.setFullName("Test User");
        testUser.setEnabled(true);
        testUser.setKeycloakId(UUID.randomUUID());
        testUser.setDob(LocalDate.now().minusYears(20L));
        savedUser = userRepository.save(testUser);
        accountFirstRUB = accountRepository.save(Account.builder()
                .active(true).currencyCode(Currency.RUB)
                .balance(BigDecimal.valueOf(200))
                .user(savedUser)
                .build());
    }

    @Test
    public void shouldGetAccountsByUser() throws Exception {
        mockMvc.perform(get("/api/account/user/{userId}", savedUser.getId())
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void shouldGetAccountById() throws Exception {
        mockMvc.perform(get("/api/account/{id}", accountFirstRUB.getId())
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountFirstRUB.getId().toString()));
    }

    @Test
    public void shouldPerformCashDeposit() throws Exception {
        CashOperationDTO cash = CashOperationDTO.builder()
                .accountId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .operationType(OperationType.CASH_DEPOSIT)
                .currencyCode(Currency.USD)
                .amount(BigDecimal.valueOf(500))
                .status(OperationStatus.SENT)
                .build();


        String response = mockMvc.perform(post("/api/account/operation")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cash)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CashOperationResultDTO result =
                objectMapper.readValue(response, CashOperationResultDTO.class);

        assertThat(result.getStatus()).isEqualTo(OperationStatus.PENDING);
    }

    @Test
    public void shouldPerformExchangeOperation() throws Exception {
        ExchangeOperationDTO exchange = ExchangeOperationDTO.builder()
                .sourceAccountId(UUID.randomUUID())
                .targetAccountId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(100))
                .operationType(OperationType.EXCHANGE)
                .status(OperationStatus.SENT)
                .build();

        String response = mockMvc.perform(post("/api/account/operation")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exchange)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExchangeOperationResultDTO result =
                objectMapper.readValue(response, ExchangeOperationResultDTO.class);

        assertThat(result.getStatus()).isEqualTo(OperationStatus.PENDING);
    }
}
