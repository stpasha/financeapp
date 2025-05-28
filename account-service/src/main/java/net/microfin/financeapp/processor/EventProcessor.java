package net.microfin.financeapp.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.mapper.UserMapper;
import net.microfin.financeapp.repository.OutboxEventRepository;
import net.microfin.financeapp.repository.UserRepository;
import net.microfin.financeapp.service.KeycloakUserService;
import net.microfin.financeapp.util.OperationStatus;
import net.microfin.financeapp.util.OperationType;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class EventProcessor {
    final OutboxEventRepository outboxEventRepository;
    final UserRepository userRepository;
    final KeycloakUserService keycloakUserService;
    final UserMapper userMapper;
    final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processUserCreateEvent() {
        for (OutboxEvent outboxEvent : outboxEventRepository.findOutboxEventByPendingStatus()) {
            switch (outboxEvent.getOperationType()) {
                case OperationType.USER_CREATED -> {
                    processUserCreateEvent(outboxEvent);
                }
                case OperationType.PASSWORD_CHANGED -> {
                    processChangePasswordEvent(outboxEvent);
                }
            }
        }
    }

    private void processUserCreateEvent(OutboxEvent outboxEvent) {
        try {
            UserDTO userDTO = fromJson(outboxEvent.getPayload(), UserDTO.class);
            UserRepresentation keycloakUser = keycloakUserService.createUser(userDTO);
            log.info("Keycloak response: {}", keycloakUser);
            outboxEvent.setStatus(OperationStatus.SENT);
            userRepository.findById(userDTO.getId()).map(user -> {
                user.setEnabled(true);
                user.setKeycloakId(UUID.fromString(keycloakUser.getId()));
                userRepository.save(user);
                log.info("Processed successfully Outbox event result - {} \n userDTO - {}", outboxEvent, userDTO);
                return user;
            }).orElseThrow(() -> new EntityNotFoundException("User not found"));
        } catch (Exception e) {
            handleRetry(outboxEvent, e);
        }
        outboxEventRepository.save(outboxEvent);
    }

    private void handleRetry(OutboxEvent outboxEvent, Exception e) {
        outboxEvent.setRetryCount(outboxEvent.getRetryCount() + 1);
        outboxEvent.setLastAttemptAt(LocalDateTime.now());
        outboxEvent.setNextAttemptAt(LocalDateTime.now().plusMinutes(5)); // например, через 5 минут повтор
        if (outboxEvent.getRetryCount() > 5) {
            outboxEvent.setStatus(OperationStatus.FAILED);
        } else {
            outboxEvent.setStatus(OperationStatus.RETRYABLE);
        }
        log.error("Error occured event {}, exception", outboxEvent, e);
    }

    private void processChangePasswordEvent(OutboxEvent outboxEvent) {
        try {
            PasswordDTO passwordDTO = fromJson(outboxEvent.getPayload(), PasswordDTO.class);
            userRepository.findById(passwordDTO.getId()).map(user -> {
                user.setPassword(passwordDTO.getPassword());
                passwordDTO.setKeycloakId(user.getKeycloakId());
                keycloakUserService.updateUserPassword(passwordDTO);
                userRepository.save(user);
                log.info("Processed successfully for password Outbox event result - {}", outboxEvent);
                return user;
            }).orElseThrow(() -> new EntityNotFoundException("User not found for password update"));
            outboxEvent.setStatus(OperationStatus.SENT);
        } catch (Exception e) {
            handleRetry(outboxEvent, e);
        }
        outboxEventRepository.save(outboxEvent);
    }

    @Scheduled(fixedDelay = 15000)
    @Transactional
    public void processErrorEvents() {
        for (OutboxEvent outboxEvent : outboxEventRepository.findRetryableOutboxEvent(LocalDateTime.now())) {
            switch (outboxEvent.getOperationType()) {
                case OperationType.USER_CREATED -> {
                    processUserCreateEvent(outboxEvent);
                }
                case OperationType.PASSWORD_CHANGED -> {
                    processChangePasswordEvent(outboxEvent);
                }
            }
        }
    }

    private <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot deserialize payload", e);
        }
    }
}
