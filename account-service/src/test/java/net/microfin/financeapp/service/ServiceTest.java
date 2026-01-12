package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.FinanceAppTest;
import net.microfin.financeapp.config.KeyCloakConfig;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.jooq.tables.records.AccountsRecord;
import net.microfin.financeapp.jooq.tables.records.OutboxEventsRecord;
import net.microfin.financeapp.jooq.tables.records.UsersRecord;
import net.microfin.financeapp.mapper.UserLegacyMapper;
import net.microfin.financeapp.processor.EventProcessor;
import net.microfin.financeapp.repository.*;
import net.microfin.financeapp.util.Currency;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@FinanceAppTest
public class ServiceTest extends AbstractTest {

    @MockitoBean
    private KeycloakUserService keycloakUserService;
    @MockitoBean
    private KeyCloakConfig keyCloakConfig;
    @MockitoBean
    private UsersResource usersResource;
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private AccountService accountService;
    @Autowired
    private UserWriteRepository userWriteRepository;
    @Autowired
    private UserReadRepository userReadRepository;
    @Autowired
    private AccountWriteRepository accountWriteRepository;
    @Autowired
    private AccountReadRepository accountReadRepository;
    @Autowired
    private OutboxEventWriteRepository eventRepository;
    @Autowired
    private UserLegacyMapper userLegacyMapper;

    @Autowired
    private UserService userService;
    @Autowired
    private OutboxUserService outboxUserService;
    @Autowired
    private IdempotencyService idempotencyService;
    @Autowired
    private OutboxService outboxService;


    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class DefaultAccountServiceTest {


        private UsersRecord testUser;

        @BeforeEach
        void setup() {
            eventRepository.deleteAll();
            accountWriteRepository.deleteAll();
            userWriteRepository.deleteAll();
            assertThat(eventRepository.findAll()).isEmpty();

            testUser = new UsersRecord();
            testUser.setUserName("user1");
            testUser.setFullName("Test User");
            testUser.setIsEnabled(true);
            testUser.setKeycloakId(UUID.randomUUID());
            testUser.setDob(LocalDateTime.now().minusYears(20L));
            testUser.setUpdatedAt(LocalDateTime.now());
            userWriteRepository.insert(testUser);
        }

        @Test
        void shouldCreateAccountSuccessfully() {
            AccountDTO dto = AccountDTO.builder()
                    .userId(testUser.getUserId())
                    .balance(BigDecimal.valueOf(1000).setScale(2))
                    .currencyCode("USD")
                    .active(true)
                    .build();

            Optional<AccountDTO> result = accountService.createAccount(dto);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isNotNull();
            assertThat(result.get().getCurrencyCode()).isEqualTo("USD");

            Optional<AccountsRecord> account =
                    accountReadRepository.findById(result.get().getId());

            assertThat(account).isPresent();
            assertThat(account.get().getUserId()).isEqualTo(testUser.getUserId());
            assertThat(account.get().getCurrencyCode()).isEqualTo("USD");
        }


        @Test
        void shouldGetAccountSuccessfully() {
            AccountsRecord record = new AccountsRecord();
            record.setUserId(testUser.getUserId());
            record.setBalance(BigDecimal.valueOf(100).setScale(2));
            record.setCurrencyCode(Currency.USD.name());
            record.setIsActive(true);
            record.setCreatedAt(LocalDateTime.now());
            record.setUpdatedAt(LocalDateTime.now());

            AccountsRecord saved = accountWriteRepository.insert(record);

            Optional<AccountDTO> dto = accountService.getAccount(saved.getAccountId());

            assertThat(dto).isPresent();
            assertThat(dto.get().getBalance())
                    .isEqualByComparingTo(BigDecimal.valueOf(100).setScale(2));
        }

        @Test
        void shouldReturnAccountsByUserId() {
            AccountsRecord record = new AccountsRecord();
            record.setUserId(testUser.getUserId());
            record.setBalance(BigDecimal.valueOf(100).setScale(2));
            record.setCurrencyCode(Currency.EUR.name());
            record.setIsActive(true);
            record.setCreatedAt(LocalDateTime.now());
            record.setUpdatedAt(LocalDateTime.now());

            accountWriteRepository.insert(record);

            List<AccountDTO> accounts =
                    accountService.getAccountsByUserId(testUser.getUserId());

            assertThat(accounts).hasSize(1);
            assertThat(accounts.get(0).getBalance())
                    .isEqualByComparingTo(BigDecimal.valueOf(100).setScale(2));
        }

        @Test
        void shouldReturnEmptyListForUnknownUser() {
            List<AccountDTO> accounts = accountService.getAccountsByUserId(UUID.randomUUID());
            assertThat(accounts).isEmpty();
        }

//        @Test
//        void shouldProcessCashDepositAndGenerateOutboxEvent() {
//            AccountsRecord record = new AccountsRecord();
//            record.setUserId(testUser.getUserId());
//            record.setBalance(BigDecimal.valueOf(100).setScale(2));
//            record.setCurrencyCode(Currency.USD.name());
//            record.setIsActive(true);
//            record.setCreatedAt(LocalDateTime.now());
//            record.setUpdatedAt(LocalDateTime.now());
//
//            AccountsRecord saved = accountWriteRepository.insert(record);
//
//            CashOperationDTO dto = new CashOperationDTO();
//            dto.setId(UuidCreator.getTimeOrderedEpoch());
//            dto.setUserId(testUser.getUserId());
//            dto.setCurrencyCode(Currency.USD);
//            dto.setAmount(BigDecimal.valueOf(200));
//            dto.setOperationType(OperationType.CASH_DEPOSIT);
//
//            var result = accountService.processOperation(dto);
//
//            assertThat(result).isPresent();
//            assertThat(result.get().getMessage()).contains("CASH_DEPOSIT");
//
//            var events = eventRepository.findAll();
//            assertThat(events).isNotEmpty();
//            assertThat(events).anySatisfy(e -> {
//                assertThat(e.getPayload()).contains("CASH_DEPOSIT");
//                assertThat(e.getStatus()).isEqualTo(OperationStatus.PENDING.name());
//            });
//        }
//
//    }


        @Nested
        class DefaultUserServiceTest {


            private UUID testUserId = null;

            @BeforeEach
            void setup() {
                eventRepository.deleteAll();
                accountWriteRepository.deleteAll();
                userWriteRepository.deleteAll();

                UsersRecord record = new UsersRecord();
                record.setUserName("johndoe");
                record.setFullName("John Doe");
                record.setDob(LocalDateTime.now().minusYears(30));
                record.setIsEnabled(true);
                record.setKeycloakId(UUID.randomUUID());

                testUserId = userWriteRepository.insert(record).getUserId();
            }

            @Test
            void testGetUserByUsername() {
                Optional<UserDTO> result = userService.getUserByUsername("johndoe");

                assertThat(result).isPresent();
                assertThat(result.get().getUsername()).isEqualTo("johndoe");
                assertThat(result.get().getId()).isEqualTo(testUserId);
            }

            @Test
            void testGetUsers() {
                List<UserDTO> users = userService.getUsers();

                assertThat(users).isNotEmpty();
                assertThat(users)
                        .anySatisfy(user -> {
                            assertThat(user.getUsername()).isEqualTo("johndoe");
                            assertThat(user.getId()).isEqualTo(testUserId);
                        });
            }

            @Test
            void testCreateUserGeneratesOutbox() throws JsonProcessingException {
                UserDTO newUser = UserDTO.builder()
                        .username("janedoe")
                        .fullName("Jane Doe")
                        .dob(LocalDateTime.now().minusYears(25))
                        .password("password")
                        .confirmPassword("password")
                        .enabled(true)
                        .keycloakId(UUID.randomUUID())
                        .build();

                Optional<UserDTO> result = userService.createUser(newUser);

                assertThat(result).isPresent();
                assertThat(result.get().getId()).isNotNull();

                assertNotNull(userReadRepository.findById(result.get().getId()));

                assertThat(eventRepository.findAll())
                        .anySatisfy(event -> {
                            assertThat(event.getOperationType())
                                    .isEqualTo(OperationType.USER_CREATED.name());
                            assertThat(event.getStatus())
                                    .isEqualTo(OperationStatus.PENDING.name());
                        });
            }


            @Test
            void testUpdatePasswordGeneratesOutbox() throws JsonProcessingException {
                PasswordDTO dto = new PasswordDTO();
                dto.setId(testUserId);
                dto.setPassword("securePass123");
                dto.setConfirmPassword("securePass123");

                Optional<PasswordDTO> result = userService.updatePassword(dto);

                assertThat(result).isPresent();
                assertThat(result.get().getPassword()).isEqualTo("securePass123");

                assertThat(eventRepository.findAll())
                        .anySatisfy(event ->
                                assertThat(event.getOperationType())
                                        .isEqualTo(OperationType.PASSWORD_CHANGED.name())
                        );
            }


            @Test
            void testUpdateUser() {
                UpdateUserDTO patch = UpdateUserDTO.builder()
                        .id(testUserId)
                        .fullName("Johnathan Doe")
                        .dob(LocalDateTime.now().minusYears(23))
                        .build();

                Optional<UserDTO> updated = userService.updateUser(patch);

                assertThat(updated).isPresent();
                assertThat(updated.get().getFullName()).isEqualTo("Johnathan Doe");
            }

            @Test
            void testCreateUserThrowsWhenUnderage() {
                UserDTO underageUser = UserDTO.builder()
                        .username("teen")
                        .fullName("Teen User")
                        .dob(LocalDateTime.now().minusYears(17))
                        .enabled(true)
                        .password("123")
                        .confirmPassword("123")
                        .keycloakId(UUID.randomUUID())
                        .build();

                assertThatThrownBy(() -> userService.createUser(underageUser))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("Incorrect age");
            }

            @Test
            void testUpdatePasswordThrowsWhenMismatch() {
                PasswordDTO dto = new PasswordDTO();
                dto.setId(testUserId);
                dto.setPassword("abc123");
                dto.setConfirmPassword("xyz789");

                assertThatThrownBy(() -> userService.updatePassword(dto))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("Passwords should match");
            }

        }

        @Nested
        public class EventProcessorUnitTest {

            @Autowired
            private EventProcessor processor;

            private UUID savedUserId;
            private AccountsRecord accountFirstRUB;
            private AccountsRecord accountSecondRUB;
            private AccountsRecord accountThirdUSD;

            @BeforeEach
            void setUp() {
                accountWriteRepository.deleteAll();
                userWriteRepository.deleteAll();
                eventRepository.deleteAll();

                UsersRecord user = new UsersRecord();
                user.setUserId(UUID.randomUUID());
                user.setUserName("user1");
                user.setFullName("Test User");
                user.setIsEnabled(true);
                user.setKeycloakId(UUID.randomUUID());
                user.setDob(LocalDateTime.now().minusYears(20));

                UsersRecord testUser2 = new UsersRecord();
                testUser2.setUserName("user2");
                testUser2.setFullName("Test User2");
                testUser2.setIsEnabled(true);
                testUser2.setKeycloakId(UUID.randomUUID());
                testUser2.setDob(LocalDateTime.now().minusYears(20L));
                testUser2.setUpdatedAt(LocalDateTime.now());

                UsersRecord savedUser = userWriteRepository.insert(user);
                UsersRecord savedUser2 = userWriteRepository.insert(testUser2);
                savedUserId = savedUser.getUserId();

                accountFirstRUB = new AccountsRecord();
                accountFirstRUB.setAccountId(UUID.randomUUID());
                accountFirstRUB.setUserId(savedUserId);
                accountFirstRUB.setCurrencyCode("RUB");
                accountFirstRUB.setBalance(BigDecimal.valueOf(200));
                accountFirstRUB.setIsActive(true);

                accountSecondRUB = new AccountsRecord();
                accountSecondRUB.setAccountId(UUID.randomUUID());
                accountSecondRUB.setUserId(savedUser2.getUserId());
                accountSecondRUB.setCurrencyCode("RUB");
                accountSecondRUB.setBalance(BigDecimal.ZERO);
                accountSecondRUB.setIsActive(true);

                accountThirdUSD = new AccountsRecord();
                accountThirdUSD.setAccountId(UUID.randomUUID());
                accountThirdUSD.setUserId(savedUserId);
                accountThirdUSD.setCurrencyCode("USD");
                accountThirdUSD.setBalance(BigDecimal.ZERO);
                accountThirdUSD.setIsActive(true);

                accountWriteRepository.insertAll(
                        List.of(accountFirstRUB, accountSecondRUB, accountThirdUSD)
                );
            }

            @Test
            void processUserCreatedEvent_success() throws Exception {
                String payload = objectMapper.writeValueAsString(
                        UserDTO.builder()
                                .id(savedUserId)
                                .username("user1")
                                .fullName("Test User")
                                .password("pswd")
                                .confirmPassword("pswd")
                                .dob(LocalDateTime.now().minusYears(20))
                                .enabled(true)
                                .build()
                );

                OutboxEventsRecord record = new OutboxEventsRecord();
                record.setAggregateType("USER");
                record.setAggregateId(savedUserId);
                record.setOperationType(OperationType.USER_CREATED.name());
                record.setPayload(payload);
                record.setStatus(OperationStatus.PENDING.name());
                record.setCreatedAt(LocalDateTime.now());
                record.setUpdatedAt(LocalDateTime.now());

                OutboxEventsRecord saved = eventRepository.insert(record);

                UserRepresentation kcUser = new UserRepresentation();
                kcUser.setId(UUID.randomUUID().toString());
                when(keycloakUserService.createUser(any())).thenReturn(kcUser);

                processor.processSingleEvent(saved.getOutboxId());

                OutboxEventsRecord persisted =
                        eventRepository.findByIdForUpdateSkipLocked(saved.getOutboxId())
                                .orElseThrow();

                assertEquals(OperationStatus.SENT.name(), persisted.getStatus());
                assertNotNull(persisted.getLastAttemptAt());
                assertNull(persisted.getNextAttemptAt());

                verify(keycloakUserService).createUser(any());
            }


            @Test
            void processPasswordChangedEvent_success() throws Exception {
                String payload = objectMapper.writeValueAsString(
                        PasswordDTO.builder()
                                .id(savedUserId)
                                .keycloakId(UUID.randomUUID())
                                .password("pswd")
                                .confirmPassword("pswd")
                                .build()
                );

                OutboxEventsRecord record = new OutboxEventsRecord();
                record.setAggregateType("USER");
                record.setAggregateId(savedUserId);
                record.setOperationType(OperationType.PASSWORD_CHANGED.name());
                record.setPayload(payload);
                record.setStatus(OperationStatus.PENDING.name());
                record.setCreatedAt(LocalDateTime.now());
                record.setUpdatedAt(LocalDateTime.now());

                OutboxEventsRecord saved = eventRepository.insert(record);

                processor.processSingleEvent(saved.getOutboxId());

                OutboxEventsRecord persisted =
                        eventRepository.findByIdForUpdateSkipLocked(saved.getOutboxId())
                                .orElseThrow();

                assertEquals(OperationStatus.SENT.name(), persisted.getStatus());
                verify(keycloakUserService).updateUserPassword(any());
            }


            @Test
            void processExchange_success() throws Exception {
                BigDecimal rubBalance = accountFirstRUB.getBalance();
                BigDecimal amount = BigDecimal.valueOf(100);

                String payload = objectMapper.writeValueAsString(
                        ExchangeOperationDTO.builder()
                                .operationType(OperationType.EXCHANGE)
                                .amount(amount)
                                .sourceAccountId(accountFirstRUB.getAccountId())
                                .targetAccountId(accountThirdUSD.getAccountId())
                                .createdAt(LocalDateTime.now())
                                .status(OperationStatus.PENDING)
                                .build()
                );

                OutboxEventsRecord record = new OutboxEventsRecord();
                record.setAggregateType("ACCOUNT");
                record.setAggregateId(accountFirstRUB.getAccountId());
                record.setOperationType(OperationType.EXCHANGE.name());
                record.setPayload(payload);
                record.setStatus(OperationStatus.PENDING.name());
                record.setCreatedAt(LocalDateTime.now());
                record.setUpdatedAt(LocalDateTime.now());

                OutboxEventsRecord saved = eventRepository.insert(record);

                processor.processSingleEvent(saved.getOutboxId());

                AccountsRecord source =
                        accountReadRepository.findById(accountFirstRUB.getAccountId()).orElseThrow();
                AccountsRecord target =
                        accountReadRepository.findById(accountThirdUSD.getAccountId()).orElseThrow();

                assertEquals(0, source.getBalance().compareTo(rubBalance.subtract(amount)));
                assertTrue(target.getBalance().compareTo(BigDecimal.ZERO) > 0);
            }

            @Test
            void processTransfer_success() throws Exception {
                BigDecimal rubBalance = accountFirstRUB.getBalance();
                BigDecimal amount = BigDecimal.valueOf(100);

                String payload = objectMapper.writeValueAsString(
                        ExchangeOperationDTO.builder()
                                .operationType(OperationType.TRANSFER)
                                .amount(amount)
                                .sourceAccountId(accountFirstRUB.getAccountId())
                                .targetAccountId(accountSecondRUB.getAccountId())
                                .userId(savedUserId)
                                .createdAt(LocalDateTime.now())
                                .status(OperationStatus.PENDING)
                                .build()
                );

                OutboxEventsRecord record = new OutboxEventsRecord();
                record.setAggregateType("ACCOUNT");
                record.setAggregateId(accountFirstRUB.getAccountId());
                record.setOperationType(OperationType.TRANSFER.name());
                record.setPayload(payload);
                record.setStatus(OperationStatus.PENDING.name());
                record.setCreatedAt(LocalDateTime.now());
                record.setUpdatedAt(LocalDateTime.now());

                OutboxEventsRecord saved = eventRepository.insert(record);

                processor.processSingleEvent(saved.getOutboxId());

                AccountsRecord source =
                        accountReadRepository.findById(accountFirstRUB.getAccountId()).orElseThrow();
                AccountsRecord target =
                        accountReadRepository.findById(accountSecondRUB.getAccountId()).orElseThrow();

                assertEquals(0, source.getBalance().compareTo(rubBalance.subtract(amount)));
                assertEquals(0, target.getBalance().compareTo(amount));
            }
        }
    }
}