package net.microfin.financeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.domain.OutboxEvent;
import net.microfin.financeapp.domain.User;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.mapper.UserMapper;
import net.microfin.financeapp.repository.OutboxEventRepository;
import net.microfin.financeapp.repository.UserRepository;
import net.microfin.financeapp.util.OperationType;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final OutboxEventRepository eventRepository;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findUsersByUsername(username).map(user -> userMapper.toDto(user));
    }

    @Override
    @Transactional
    public Optional<UserDTO> createUser(UserDTO userDTO) throws JsonProcessingException {
        User user = userRepository.save(userMapper.toEntity(userDTO));
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(user.getId())
                .aggregateType("USER")
                .operationType(OperationType.USER_CREATED)
                .payload(objectMapper.writeValueAsString(userMapper.toDto(user)))
                .build();
        eventRepository.save(outboxEvent);
        return Optional.of(userMapper.toDto(user));
    }

    @Override
    @Transactional
    public Optional<PasswordDTO> updatePassword(PasswordDTO passwordDTO) throws JsonProcessingException {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(passwordDTO.getId())
                .aggregateType("USER")
                .operationType(OperationType.PASSWORD_CHANGED)
                .payload(objectMapper.writeValueAsString(passwordDTO))
                .build();
        eventRepository.save(outboxEvent);
        return Optional.of(passwordDTO);
    }

    @Override
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return userRepository.findById(userDTO.getId())
                .map(existing -> Optional.of(userMapper.toDto(userRepository.save(userMapper.toPatchedEntity(userDTO, existing)))))
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userDTO.getId()));
    }
}
