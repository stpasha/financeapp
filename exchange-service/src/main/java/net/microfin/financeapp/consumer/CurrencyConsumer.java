package net.microfin.financeapp.consumer;

import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.CurrencyDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CurrencyConsumer {

    private List<CurrencyDTO> currencyDTOList;

    @KafkaListener(topics = "input-exchange", concurrency = "1")
    public void listen(ConsumerRecord<String, List<CurrencyDTO>> consumerRecord, Acknowledgment acknowledgment) {
        currencyDTOList = consumerRecord.value();
        acknowledgment.acknowledge();
    }

    public List<CurrencyDTO> getCurrencyList() {
        return currencyDTOList;
    }
}
