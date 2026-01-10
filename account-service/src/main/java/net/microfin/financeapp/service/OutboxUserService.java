package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.config.ExceptionsProperties;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.exception.InvalidPayloadException;
import net.microfin.financeapp.jooq.tables.records.OutboxEventsRecord;
import net.microfin.financeapp.jooq.tables.records.UsersRecord;
import net.microfin.financeapp.repository.UserReadRepository;
import net.microfin.financeapp.repository.UserWriteRepository;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxUserService {

    private final UserWriteRepository userWriteRepository;
    private final UserReadRepository userReadRepository;
    private final KeycloakUserService keycloakUserService;
    private final ObjectMapper objectMapper;
    private final ExceptionsProperties exceptionsProperties;
    private final Validator validator;

    @Transactional
    public void processUserCreateEvent(OutboxEventsRecord outboxEvent) {
        UserDTO userDTO = fromJson(outboxEvent.getPayload(), UserDTO.class);
        var violations = validator.validate(userDTO);
        if (!violations.isEmpty()) {
            throw new InvalidPayloadException(violations.toString());
        }
        UserRepresentation keycloakUser = keycloakUserService.createUser(userDTO);
        log.info("Keycloak response: {}", keycloakUser);
        userWriteRepository.updateKeycloakData(userDTO.getId(), UUID.fromString(keycloakUser.getId()));
        log.info("Processed successfully Outbox event result - {} \n userDTO - {}", outboxEvent, userDTO);
    }

    @Transactional
    public void processChangePasswordEvent(OutboxEventsRecord outboxEvent) {
        PasswordDTO passwordDTO = fromJson(outboxEvent.getPayload(), PasswordDTO.class);
        var violations = validator.validate(passwordDTO);
        if (!violations.isEmpty()) {
            throw new InvalidPayloadException(violations.toString());
        }
        UsersRecord user = userReadRepository.findById(passwordDTO.getId());
        passwordDTO.setKeycloakId(user.getKeycloakId());
        keycloakUserService.updateUserPassword(passwordDTO);
        log.info("Processed successfully for password Outbox event result - {}", outboxEvent);
    }

    private <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new InvalidPayloadException(exceptionsProperties.getDeserFailure(), e);
        }
    }
}
