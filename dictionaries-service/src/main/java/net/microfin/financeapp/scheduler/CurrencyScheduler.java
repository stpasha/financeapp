package net.microfin.financeapp.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.producer.CurrencyKafkaProducer;
import net.microfin.financeapp.service.CurrencyService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
@AllArgsConstructor
@Slf4j
public class CurrencyScheduler {

    private final CurrencyKafkaProducer currencyKafkaProducer;

    private final CurrencyService currencyService;

    @Scheduled(fixedDelay = 5000)
    public void scheduleCurrency() {
        currencyKafkaProducer.send(currencyService.listCurrency());
    }
}
