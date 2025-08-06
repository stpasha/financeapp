package net.microfin.financeapp.producer;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.CurrencyDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyKafkaProducer {
    private final KafkaTemplate<String, CurrencyDTO[]> kafkaTemplate;

    public void send(List<CurrencyDTO> currencyDTOs) {
        kafkaTemplate.send("input-exchange", currencyDTOs.toArray(new CurrencyDTO[0]));
    }
}
