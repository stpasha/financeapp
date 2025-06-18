package net.microfin.financeapp.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.config.ExceptionsProperties;
import net.microfin.financeapp.dto.CashOperationDTO;
import net.microfin.financeapp.dto.ExchangeOperationDTO;
import net.microfin.financeapp.dto.GenericOperationDTO;
import net.microfin.financeapp.dto.TransferOperationDTO;
import net.microfin.financeapp.service.RuleService;
import net.microfin.financeapp.util.OperationType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class RuleApi {
    private final RuleService ruleService;
    private final ObjectMapper objectMapper;
    private final ExceptionsProperties exceptionsProperties;

    @PostMapping
    public ResponseEntity<Boolean> check(@RequestBody JsonNode json) {
        GenericOperationDTO genericOperationDTO;
        String operationType = json.get("operationType").asText("");

        String jsonStr = json.toString();

        switch (OperationType.valueOf(OperationType.class, operationType)) {
            case OperationType.CASH_DEPOSIT, OperationType.CASH_WITHDRAWAL -> {
                genericOperationDTO = fromJson(jsonStr, CashOperationDTO.class);
            }
            case EXCHANGE -> {
                genericOperationDTO = fromJson(jsonStr, ExchangeOperationDTO.class);
            }
            case TRANSFER -> {
                genericOperationDTO = fromJson(jsonStr, TransferOperationDTO.class);
            }
            default -> throw new RuntimeException(exceptionsProperties.getOperationFailure());
        }

        return new ResponseEntity<>(ruleService.checkRulesForOperation(genericOperationDTO), HttpStatus.ACCEPTED);
    }


    private <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(exceptionsProperties.getDeserFailure(), e);
        }
    }
}
