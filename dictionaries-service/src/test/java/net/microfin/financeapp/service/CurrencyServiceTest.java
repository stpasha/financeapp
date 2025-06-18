package net.microfin.financeapp.service;

import net.microfin.financeapp.FinanceAppTest;
import net.microfin.financeapp.dto.CurrencyDTO;
import net.microfin.financeapp.util.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@FinanceAppTest
public class CurrencyServiceTest {

    @Autowired
    private CurrencyService currencyService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldReturnAllCurrenciesWithCorrectRates() {
        List<CurrencyDTO> currencies = currencyService.listCurrency();

        assertThat(currencies).isNotNull();
        assertThat(currencies).isNotEmpty();
        assertThat(currencies).hasSize(Currency.values().length);

        for (CurrencyDTO dto : currencies) {
            assertThat(dto.name()).isNotBlank();
            assertThat(dto.code()).isIn(Arrays.stream(Currency.values()).map(Currency::name).toList());
            assertThat(dto.value()).isNotNull();

            if (dto.code().equals(Currency.RUB.name())) {
                assertThat(dto.value()).isEqualByComparingTo(BigDecimal.ONE);
            } else {
                assertThat(dto.value()).isBetween(BigDecimal.valueOf(80), BigDecimal.valueOf(100));
                assertThat(dto.value().scale()).isEqualTo(2);
            }
        }
    }
}
