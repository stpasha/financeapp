package net.microfin.financeapp.service;

import net.microfin.financeapp.FinanceAppTest;
import net.microfin.financeapp.client.*;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.util.OperationStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@FinanceAppTest
public class ServiceTest {

    @Autowired
    private AccountService accountService;

    @MockitoBean
    private AccountClient accountClient;

    @MockitoBean
    private CashClient cashClient;

    @MockitoBean
    private TransferClient transferClient;

    @MockitoBean
    private ExchangeClient exchangeClient;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private DictionaryClient dictionaryClient;

    @MockitoBean
    private NotificationClient notificationClient;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private UserService userService;

    @Nested
    class AccountServiceTest {
        @Test
        void shouldCreateCashOperationSuccessfully() {
            CashOperationDTO dto = CashOperationDTO.builder()
                    .userId(1)
                    .accountId(100)
                    .amount(BigDecimal.valueOf(150))
                    .build();

            CashOperationResultDTO result = CashOperationResultDTO.builder()
                    .status(OperationStatus.SENT)
                    .message("Cash operation processed")
                    .build();

            when(cashClient.cashOperation(dto)).thenReturn(ResponseEntity.ok(result));

            OperationResult response = accountService.createCashOperation(dto);

            assertThat(response).isInstanceOf(CashOperationResultDTO.class);
            assertThat(((CashOperationResultDTO) response).getStatus()).isEqualTo(OperationStatus.SENT);
        }

        @Test
        void shouldCreateTransferOperationSuccessfully() {
            TransferOperationDTO dto = TransferOperationDTO.builder()
                    .userId(1)
                    .sourceAccountId(100)
                    .targetAccountId(200)
                    .amount(BigDecimal.valueOf(300))
                    .build();

            TransferOperationResultDTO result = TransferOperationResultDTO.builder()
                    .status(OperationStatus.SENT)
                    .message("Transfer done")
                    .build();

            when(transferClient.transferOperation(dto)).thenReturn(ResponseEntity.ok(result));

            OperationResult response = accountService.createTransferOperation(dto);

            assertThat(response).isInstanceOf(TransferOperationResultDTO.class);
            assertThat(((TransferOperationResultDTO) response).getStatus()).isEqualTo(OperationStatus.SENT);
        }

        @Test
        void shouldCreateExchangeOperationSuccessfully() {
            ExchangeOperationDTO dto = ExchangeOperationDTO.builder()
                    .userId(1)
                    .sourceAccountId(100)
                    .targetAccountId(101)
                    .status(OperationStatus.PENDING)
                    .amount(BigDecimal.valueOf(100))
                    .build();

            ExchangeOperationResultDTO result = ExchangeOperationResultDTO.builder()
                    .status(OperationStatus.SENT)
                    .message("Exchange done")
                    .build();

            when(exchangeClient.exchangeOperation(dto)).thenReturn(ResponseEntity.ok(result));

            OperationResult response = accountService.createExchangeOperation(dto);

            assertThat(response).isInstanceOf(ExchangeOperationResultDTO.class);
            assertThat(((ExchangeOperationResultDTO) response).getStatus()).isEqualTo(OperationStatus.SENT);
        }

        @Test
        void shouldReturnAccountsByUser() {
            AccountDTO account = AccountDTO.builder()
                    .id(101)
                    .user(UserDTO.builder().id(1).build())
                    .balance(BigDecimal.valueOf(1000))
                    .currencyCode("RUB")
                    .build();

            when(accountClient.getAccountsByUser(1)).thenReturn(ResponseEntity.ok(singletonList(account)));

            List<AccountDTO> result = accountService.getAccountsByUser(1);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCurrencyCode()).isEqualTo("RUB");
            assertThat(result.get(0).getBalance()).isEqualByComparingTo("1000");
        }
    }

    @Nested
    class DictionaryServiceTest {

        @Test
        void shouldReturnCurrenciesWhenClientReturnsSuccess() {
            CurrencyDTO rub = new CurrencyDTO("Рубль", "RUB", BigDecimal.ONE);


            CurrencyDTO usd = new CurrencyDTO("Доллар", "USD", BigDecimal.valueOf(80));

            List<CurrencyDTO> currencies = List.of(rub, usd);
            when(dictionaryClient.listCurrency()).thenReturn(ResponseEntity.ok(currencies));

            List<CurrencyDTO> result = dictionaryService.getCurrencies();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(CurrencyDTO::code).containsExactly("RUB", "USD");
        }

        @Test
        void shouldReturnEmptyListWhenClientReturnsNonSuccessStatus() {
            when(dictionaryClient.listCurrency()).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

            List<CurrencyDTO> result = dictionaryService.getCurrencies();

            assertThat(result).isEmpty();
        }

    }

    @Nested
    class ListNotificationsTests {

        @Test
        void shouldReturnNotificationsWhenClientReturnsSuccess() {
            NotificationDTO dto1 = new NotificationDTO();
            dto1.setId(1);
            dto1.setUserId(42);
            dto1.setOperationType("CASH_WITHDRAWAL");
            dto1.setNotificationDescription("Снятие 100 RUB");
            dto1.setCreatedAt(LocalDateTime.now());

            NotificationDTO dto2 = new NotificationDTO();
            dto2.setId(2);
            dto2.setUserId(42);
            dto2.setOperationType("CASH_DEPOSIT");
            dto2.setNotificationDescription("Пополнение 200 USD");
            dto2.setCreatedAt(LocalDateTime.now());

            List<NotificationDTO> notifications = List.of(dto1, dto2);
            when(notificationClient.listNotificationsByUserId(42)).thenReturn(ResponseEntity.ok(notifications));

            List<NotificationDTO> result = notificationService.listNotifications(42);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(NotificationDTO::getOperationType).containsExactly("CASH_WITHDRAWAL", "CASH_DEPOSIT");
        }

        @Test
        void shouldReturnEmptyListWhenClientReturnsError() {
            when(notificationClient.listNotificationsByUserId(42)).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

            List<NotificationDTO> result = notificationService.listNotifications(42);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class UserServiceTest {

        @Test
        void shouldUpdateUserSuccessfully() {
            UpdateUserDTO updateUserDTO = new UpdateUserDTO();
            updateUserDTO.setId(1);
            updateUserDTO.setFullName("updated");

            UserDTO responseDTO = new UserDTO();
            responseDTO.setId(1);
            responseDTO.setUsername("updated");

            when(userClient.update(updateUserDTO)).thenReturn(ResponseEntity.ok(responseDTO));

            UserDTO result = userService.updateUser(updateUserDTO);

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("updated");
        }

        @Test
        void shouldThrowWhenUpdateFails() {
            UpdateUserDTO updateUserDTO = new UpdateUserDTO();
            updateUserDTO.setId(1);

            when(userClient.update(updateUserDTO)).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

            assertThatThrownBy(() -> userService.updateUser(updateUserDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Unable to update user");
        }

        @Test
        void shouldReturnUserInfo() {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername("user123");

            when(userClient.getUserByName("user123")).thenReturn(ResponseEntity.ok(userDTO));

            UserDTO result = userService.queryUserInfo("user123");

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("user123");
        }

        @Test
        void shouldReturnNullIfUserNotFound() {
            when(userClient.getUserByName("unknown")).thenReturn(ResponseEntity.ok(null));

            UserDTO result = userService.queryUserInfo("unknown");

            assertThat(result).isNull();
        }

        @Test
        void shouldReturnUserListWhenSuccess() {
            UserDTO user1 = new UserDTO();
            user1.setId(1);
            user1.setUsername("user1");

            UserDTO user2 = new UserDTO();
            user2.setId(2);
            user2.setUsername("user2");

            when(userClient.getUsers()).thenReturn(ResponseEntity.ok(List.of(user1, user2)));

            List<UserDTO> result = userService.queryTargeUsers();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(UserDTO::getUsername).contains("user1", "user2");
        }

        @Test
        void shouldReturnEmptyListWhenClientFails() {
            when(userClient.getUsers()).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

            List<UserDTO> result = userService.queryTargeUsers();

            assertThat(result).isEmpty();
        }

    }
}
