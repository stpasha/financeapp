package net.microfin.financeapp.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.microfin.financeapp.client.DictionaryClient;
import net.microfin.financeapp.dto.CurrencyDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class DictionaryService {
    private final DictionaryClient dictionaryClient;
    private final MeterRegistry meterRegistry;
    @Value("${testPrometeus}")
    @Setter
    private boolean testPrometeus;

    public List<CurrencyDTO> getCurrencies() {
        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            if (testPrometeus) {
                long delay = ThreadLocalRandom.current().nextInt(100, 6000);
                Thread.sleep(delay);
            }
            ResponseEntity<List<CurrencyDTO>> responseEntity = dictionaryClient.listCurrency();
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
            return List.of();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread Interrupted", e);
        } finally {
            timer.stop(meterRegistry.timer("financeapp_currency_duration"));
        }
    }
}
