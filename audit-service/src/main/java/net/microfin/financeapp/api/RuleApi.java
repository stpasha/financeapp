package net.microfin.financeapp.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.microfin.financeapp.dto.*;
import net.microfin.financeapp.service.RuleService;
import net.microfin.financeapp.util.OperationType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('zbank.user')")
public class RuleApi {
    private final RuleService ruleService;
    private final ObjectMapper objectMapper;

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
            default -> throw new RuntimeException("Operation type not recognized");
        }

        return new ResponseEntity<>(ruleService.checkRulesForOperation(genericOperationDTO), HttpStatus.ACCEPTED);
    }


    private <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot deserialize payload", e);
        }
    }
}
