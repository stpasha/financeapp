package net.microfin.financeapp.service;

import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.client.DictionaryClient;
import net.microfin.financeapp.dto.CurrencyDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictionaryService {
    private final DictionaryClient dictionaryClient;

    public List<CurrencyDTO> getCurrencies() {
        ResponseEntity<List<CurrencyDTO>> responseEntity = dictionaryClient.listCurrencyList();
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        throw new RuntimeException("Unable to update user");
    }
}
