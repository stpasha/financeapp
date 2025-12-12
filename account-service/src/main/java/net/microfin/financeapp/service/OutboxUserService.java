package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Validator;
import net.microfin.financeapp.exception.InvalidPayloadException;
import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.config.ExceptionsProperties;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.repository.UserRepository;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxUserService {

    private final UserRepository userRepository;
    private final KeycloakUserService keycloakUserService;
    private final ObjectMapper objectMapper;
    private final ExceptionsProperties exceptionsProperties;
    private final Validator validator;

    @Transactional
    public void processUserCreateEvent(OutboxEvent outboxEvent) {
        UserDTO userDTO = fromJson(outboxEvent.getPayload(), UserDTO.class);
        var violations = validator.validate(userDTO);
        if (!violations.isEmpty()) {
            throw new InvalidPayloadException(violations.toString());
        }
        UserRepresentation keycloakUser = keycloakUserService.createUser(userDTO);
        log.info("Keycloak response: {}", keycloakUser);
        userRepository.findById(userDTO.getId()).map(user -> {
            user.setEnabled(true);
            user.setKeycloakId(UUID.fromString(keycloakUser.getId()));
            userRepository.save(user);
            log.info("Processed successfully Outbox event result - {} \n userDTO - {}", outboxEvent, userDTO);
            return user;
        }).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    public void processChangePasswordEvent(OutboxEvent outboxEvent) {
        PasswordDTO passwordDTO = fromJson(outboxEvent.getPayload(), PasswordDTO.class);
        var violations = validator.validate(passwordDTO);
        if (!violations.isEmpty()) {
            throw new InvalidPayloadException(violations.toString());
        }
        userRepository.findById(passwordDTO.getId()).map(user -> {
            passwordDTO.setKeycloakId(user.getKeycloakId());
            keycloakUserService.updateUserPassword(passwordDTO);
            userRepository.save(user);
            log.info("Processed successfully for password Outbox event result - {}", outboxEvent);
            return user;
        }).orElseThrow(() -> new EntityNotFoundException("User not found for password update"));
    }

    private <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new InvalidPayloadException(exceptionsProperties.getDeserFailure(), e);
        }
    }
}
