package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.FinanceAppTest;
import net.microfin.financeapp.config.KeyCloakConfig;
import net.microfin.financeapp.domain.Account;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.domain.User;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.mapper.UserMapper;
import net.microfin.financeapp.processor.EventProcessor;
import net.microfin.financeapp.repository.AccountRepository;
import net.microfin.financeapp.repository.OutboxEventRepository;
import net.microfin.financeapp.repository.UserRepository;
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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OutboxEventRepository eventRepository;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;


    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class DefaultAccountServiceTest {


        private User testUser;

        @BeforeEach
        void setup() {
            eventRepository.deleteAll();
            accountRepository.deleteAll();
            userRepository.deleteAll();
            assertThat(eventRepository.findAll()).isEmpty();

            testUser = new User();
            testUser.setUsername("user1");
            testUser.setFullName("Test User");
            testUser.setEnabled(true);
            testUser.setKeycloakId(UUID.randomUUID());
            testUser.setDob(LocalDateTime.now().minusYears(20L));
            userRepository.save(testUser);
        }

        @Test
        void shouldCreateAccountSuccessfully() {
            AccountDTO dto = AccountDTO.builder()
                    .user(userMapper.toDto(testUser))
                    .balance(BigDecimal.valueOf(1000).setScale(2))
                    .currencyCode("USD")
                    .active(true)
                    .build();

            Optional<AccountDTO> result = accountService.createAccount(dto);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isNotNull();
            assertThat(result.get().getCurrencyCode()).isEqualTo("USD");

            Optional<Account> account = accountRepository.findById(result.get().getId());
            assertThat(account).isPresent();
            assertThat(account.get().getUser().getId()).isEqualTo(testUser.getId());
        }

        @Test
        void shouldGetAccountSuccessfully() {
            Account account = Account.builder()
                    .user(testUser)
                    .balance(BigDecimal.valueOf(100))
                    .currencyCode(Currency.USD)
                    .active(true)
                    .build();
            account = accountRepository.save(account);

            Optional<AccountDTO> dto = accountService.getAccount(account.getId());

            assertThat(dto).isPresent();
            assertThat(dto.get().getBalance()).isEqualByComparingTo(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP));
        }

        @Test
        void shouldReturnAccountsByUserId() {
            Account account = Account.builder()
                    .user(testUser)
                    .balance(BigDecimal.valueOf(100))
                    .currencyCode(Currency.EUR)
                    .active(true)
                    .build();
            accountRepository.save(account);

            List<AccountDTO> accounts = accountService.getAccountsByUserId(testUser.getId());
            assertThat(accounts).hasSize(1);
            assertThat(accounts.get(0).getCurrencyCode()).isEqualTo("EUR");
        }

        @Test
        void shouldReturnEmptyListForUnknownUser() {
            List<AccountDTO> accounts = accountService.getAccountsByUserId(100);
            assertThat(accounts).isEmpty();
        }

//        @Test
//        void shouldProcessCashDepositAndGenerateOutboxEvent() {
//            Account account = Account.builder()
//                    .user(testUser)
//                    .balance(BigDecimal.valueOf(100))
//                    .currencyCode(Currency.USD)
//                    .active(true)
//                    .build();
//            account = accountRepository.save(account);
//
//            CashOperationDTO dto = new CashOperationDTO();
//            dto.setId(101);
//            dto.setUserId(testUser.getId());
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
//                assertThat(e.getAggregateId()).isEqualTo(dto.getId());
//                assertThat(e.getPayload()).contains("CASH_DEPOSIT", "200");
//            });
//        }
//
    }


    @Nested
    class DefaultUserServiceTest {


        private User testUser;

        @BeforeEach
        void setup() {
            eventRepository.deleteAll();
            accountRepository.deleteAll();
            userRepository.deleteAll();

            testUser = new User();
            testUser.setUsername("johndoe");
            testUser.setFullName("John Doe");
            testUser.setDob(LocalDateTime.now().minusYears(30));
            testUser.setEnabled(true);
            testUser.setKeycloakId(UUID.randomUUID());
            testUser = userRepository.save(testUser);
        }

        @Test
        void testGetUserByUsername() {
            Optional<UserDTO> result = userService.getUserByUsername("johndoe");

            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("johndoe");
        }

        @Test
        void testGetUsers() {
            List<UserDTO> users = userService.getUsers();

            assertThat(users).isNotEmpty();
            assertThat(users).anyMatch(user -> user.getUsername().equals("johndoe"));
        }

        @Test
        void testCreateUserGeneratesOutbox() throws JsonProcessingException {
            UserDTO newUser = UserDTO.builder()
                    .username("janedoe")
                    .fullName("Jane Doe")
                    .dob(LocalDate.now().minusYears(25))
                    .password("password")
                    .confirmPassword("password")
                    .enabled(true)
                    .keycloakId(UUID.randomUUID())
                    .build();

            Optional<UserDTO> result = userService.createUser(newUser);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isNotNull();
            assertThat(userRepository.findById(result.get().getId())).isPresent();

            assertThat(eventRepository.findAll())
                    .isNotEmpty()
                    .anyMatch(event -> event.getOperationType() == OperationType.USER_CREATED);
        }

        @Test
        void testUpdatePasswordGeneratesOutbox() throws JsonProcessingException {
            PasswordDTO dto = new PasswordDTO();
            dto.setId(testUser.getId());
            dto.setPassword("securePass123");
            dto.setConfirmPassword("securePass123");

            Optional<PasswordDTO> result = userService.updatePassword(dto);

            assertThat(result).isPresent();
            assertThat(result.get().getPassword()).isEqualTo("securePass123");

            assertThat(eventRepository.findAll())
                    .anyMatch(event -> event.getOperationType() == OperationType.PASSWORD_CHANGED);
        }

        @Test
        void testUpdateUser() {
            UserDTO patch = userMapper.toDto(testUser);
            patch.setFullName("Johnathan Doe");

            Optional<UserDTO> updated = userService.updateUser(patch);

            assertThat(updated).isPresent();
            assertThat(updated.get().getFullName()).isEqualTo("Johnathan Doe");
        }

        @Test
        void testCreateUserThrowsWhenUnderage() {
            UserDTO underageUser = UserDTO.builder()
                    .username("teen")
                    .fullName("Teen User")
                    .dob(LocalDate.now().minusYears(17)) // моложе 18 лет
                    .enabled(true)
                    .keycloakId(UUID.randomUUID())
                    .build();

            assertThatThrownBy(() -> userService.createUser(underageUser))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Incorrect age");
        }

        @Test
        void testUpdatePasswordThrowsWhenMismatch() {
            PasswordDTO dto = new PasswordDTO();
            dto.setId(testUser.getId());
            dto.setPassword("abc123");
            dto.setConfirmPassword("xyz789");

            assertThatThrownBy(() -> userService.updatePassword(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Passwords should match");
        }

    }

    @Nested
    public class EventProcessorUnitTest {

        private OutboxEventRepository outboxEventRepository = mock(OutboxEventRepository.class);
        private UserRepository userRepository = mock(UserRepository.class);
        private AccountRepository accountRepository = mock(AccountRepository.class);
        private KeycloakUserService keycloakUserService = mock(KeycloakUserService.class);
        private RetryService retryService = mock(RetryService.class);
        private ObjectMapper objectMapper = mock(ObjectMapper.class);

        private EventProcessor processor;

//        @BeforeEach
//        void setUp() {
//            processor = new EventProcessor(
//                    outboxEventRepository,
//                    userRepository,
//                    accountRepository,
//                    keycloakUserService,
//                    retryService,
//                    objectMapper
//            );
//        }

        @Test
        void processUserCreatedEvent_success() throws Exception {
            Integer userId = 1;
            OutboxEvent event = new OutboxEvent();
            event.setOperationType(OperationType.USER_CREATED);
            event.setPayload("{}");

            UserDTO dto = new UserDTO();
            dto.setId(userId);

            User user = new User();
            user.setId(userId);

            UserRepresentation representation = new UserRepresentation();
            representation.setId(UUID.randomUUID().toString());

            when(objectMapper.readValue(anyString(), eq(UserDTO.class))).thenReturn(dto);
            when(keycloakUserService.createUser(dto)).thenReturn(representation);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            Method method = EventProcessor.class.getDeclaredMethod("processUserCreateEvent", OutboxEvent.class);
            method.setAccessible(true);
            method.invoke(processor, event);
            assertEquals(OperationStatus.SENT, event.getStatus());
            verify(userRepository).save(any(User.class));
        }

        @Test
        void processPasswordChangedEvent_success() throws Exception {
            Integer userId = 1;
            OutboxEvent event = new OutboxEvent();
            event.setOperationType(OperationType.PASSWORD_CHANGED);
            event.setPayload("{}");

            PasswordDTO dto = new PasswordDTO();
            dto.setId(userId);

            User user = new User();
            user.setId(userId);
            user.setKeycloakId(UUID.randomUUID());

            when(objectMapper.readValue(anyString(), eq(PasswordDTO.class))).thenReturn(dto);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            Method method = EventProcessor.class.getDeclaredMethod("processChangePasswordEvent", OutboxEvent.class);
            method.setAccessible(true);
            method.invoke(processor, event);

            assertEquals(OperationStatus.SENT, event.getStatus());
            verify(keycloakUserService).updateUserPassword(dto);
        }




        @Test
        void processExchange_success() throws Exception {
            Integer srcId = 1;
            Integer tgtId = 2;
            OutboxEvent event = new OutboxEvent();
            event.setOperationType(OperationType.EXCHANGE);
            event.setPayload("{}");

            ExchangeOperationDTO dto = new ExchangeOperationDTO();
            dto.setSourceAccountId(srcId);
            dto.setTargetAccountId(tgtId);
            dto.setAmount(BigDecimal.TEN);
            dto.setTargetAmount(BigDecimal.valueOf(8));

            Account src = new Account();
            src.setBalance(BigDecimal.valueOf(100));

            Account tgt = new Account();
            tgt.setBalance(BigDecimal.valueOf(50));

            when(objectMapper.readValue(anyString(), eq(ExchangeOperationDTO.class))).thenReturn(dto);
            when(accountRepository.findById(srcId)).thenReturn(Optional.of(src));
            when(accountRepository.findById(tgtId)).thenReturn(Optional.of(tgt));

            Method method = EventProcessor.class.getDeclaredMethod("processExchange", OutboxEvent.class);
            method.setAccessible(true);
            method.invoke(processor, event);


            assertEquals(OperationStatus.SENT, event.getStatus());
        }

        @Test
        void processTransfer_success() throws Exception {
            Integer srcId = 1;
            Integer tgtId = 2;
            OutboxEvent event = new OutboxEvent();
            event.setOperationType(OperationType.TRANSFER);
            event.setPayload("{}");

            TransferOperationDTO dto = new TransferOperationDTO();
            dto.setSourceAccountId(srcId);
            dto.setTargetAccountId(tgtId);
            dto.setAmount(BigDecimal.TEN);

            Account src = new Account();
            src.setBalance(BigDecimal.valueOf(100));

            Account tgt = new Account();
            tgt.setBalance(BigDecimal.valueOf(50));

            when(objectMapper.readValue(anyString(), eq(TransferOperationDTO.class))).thenReturn(dto);
            when(accountRepository.findById(srcId)).thenReturn(Optional.of(src));
            when(accountRepository.findById(tgtId)).thenReturn(Optional.of(tgt));
            Method method = EventProcessor.class.getDeclaredMethod("processTransfer", OutboxEvent.class);
            method.setAccessible(true);
            method.invoke(processor, event);


            assertEquals(OperationStatus.SENT, event.getStatus());
        }
    }
}