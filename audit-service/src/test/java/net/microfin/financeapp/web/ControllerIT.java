package net.microfin.financeapp.web;

import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.ServiceApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test")
@SpringBootTest(classes = ServiceApplication.class)
@AutoConfigureMockMvc
public class ControllerIT extends AbstractTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private JwtDecoder jwtDecoder;
//
//    @MockitoBean
//    private AccountClientImpl accountClient;
//
//    @Autowired
//    private RuleRepository ruleRepository;
//
//    @Autowired
//    private RuleService ruleService;
//
//    @Nested
//    class RuleApiIntegrationTest extends AbstractTest {
//
//        @Test
//        void shouldCheckCashOperationWithinLimits() {
//            var dto = CashOperationDTO.builder()
//                    .amount(BigDecimal.valueOf(500))
//                    .currencyCode(Currency.USD)
//                    .operationType(OperationType.CASH_DEPOSIT)
//                    .build();
//
//            boolean result = ruleService.checkRulesForOperation(dto);
//            assertThat(result).isTrue();
//        }
//
//        @Test
//        void shouldCheckTransferOperationUsingAccountCurrency() {
//            var dto = TransferOperationDTO.builder()
//                    .sourceAccountId(1)
//                    .amount(BigDecimal.valueOf(500))
//                    .operationType(OperationType.TRANSFER)
//                    .build();
//
//            var accountDTO = AccountDTO.builder()
//                    .id(1)
//                    .currencyCode("USD")
//                    .build();
//
//            when(accountClient.getAccount(1)).thenReturn(ResponseEntity.ok(accountDTO));
//
//            boolean result = ruleService.checkRulesForOperation(dto);
//            assertThat(result).isTrue();
//        }
//
//        @Test
//        void shouldReturnFalseIfAccountNotFound() {
//            var dto = ExchangeOperationDTO.builder()
//                    .sourceAccountId(1)
//                    .amount(BigDecimal.valueOf(500))
//                    .operationType(OperationType.EXCHANGE)
//                    .build();
//
//            when(accountClient.getAccount(1)).thenReturn(ResponseEntity.ok(null));
//
//            boolean result = ruleService.checkRulesForOperation(dto);
//            assertThat(result).isFalse();
//        }
//    }
}
