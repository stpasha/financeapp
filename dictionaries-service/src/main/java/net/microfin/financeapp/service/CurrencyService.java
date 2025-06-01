package net.microfin.financeapp.service;

import net.microfin.financeapp.dto.CurrencyDTO;
import net.microfin.financeapp.util.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CurrencyService {
    public List<CurrencyDTO> listCurrencyList() {
        return Arrays.stream(Currency.values())
                .map(currency -> new CurrencyDTO(currency.getName(),
                        currency.name(),
                        Currency.RUB.equals(currency) ? BigDecimal.ONE :
                                BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(80,100))
                                        .setScale(2, RoundingMode.HALF_DOWN))).toList();
    }
}
